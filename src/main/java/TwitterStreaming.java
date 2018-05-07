import io.hgraphdb.HBaseGraph;
import io.hgraphdb.HBaseGraphConfiguration;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

public class TwitterStreaming {
    public static void main(String args []) throws Exception {
        // CHECKING INPUT PARAMETERS
        final ParameterTool params = ParameterTool.fromArgs(args);
        //System.out.println("Usage: TwitterStreaming +
        //        "[--twitter-source.consumerKey <key> --twitter-source.consumerSecret <secret> --twitter-source.token <token> --twitter-source.tokenSecret <tokenSecret>]");
        //          --keywords <inputFile>
        String CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET;
        CONSUMER_KEY = params.get("twitter-source.consumerKey");
        CONSUMER_SECRET = params.get("twitter-source.consumerSecret");
        ACCESS_TOKEN = params.get("twitter-source.token");
        ACCESS_TOKEN_SECRET = params.get("twitter-source.tokenSecret");

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        twitter4j.conf.Configuration twitter_cfg = cb.build();

        Twitter twitter =  new TwitterFactory(twitter_cfg).getInstance();

        // The keywords
        try {
            // usage --keywords input_file
            String input_file = params.get("keywords");
            System.out.println(input_file);
            Scanner scanner = new Scanner(new File(input_file));
            List<String> keywords = new ArrayList<>();
            while (scanner.hasNextLine()) {
                keywords.add(scanner.nextLine());
            }
            streamFeed(twitter_cfg, twitter, keywords.toArray(new String[0]));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void streamFeed(twitter4j.conf.Configuration cb, Twitter twitter, String[] keywords) {
        org.apache.commons.configuration.Configuration cfg = new HBaseGraphConfiguration()
                    .setInstanceType(HBaseGraphConfiguration.InstanceType.DISTRIBUTED)
                    .setGraphNamespace("tweets_graph")
                    .setCreateTables(true)
                    .set("hbase.zookeeper.quorum", "127.0.0.1")
                    .set("zookeeper.znode.parent", "/hbase")
                    .setUseSchema(true);
            HBaseGraph graph = HBaseGraph.open(cfg);

        TwitterToGraph tw = new TwitterToGraph(twitter, graph);

        StatusListener listener = new StatusListener(){
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
                System.out.println("Got a status deletion notice id:" + arg.getStatusId());
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onStatus(Status status) {
                try {
                    //System.out.println("--------");
                    Vertex tweet;

                    ///////////////////////////////////////////////////////
                    //     POPULATE THE GRAPH FROM VARIOUS SEEDS         //
                    ///////////////////////////////////////////////////////
                    // Add the status :
                    tweet = tw.getOrCreateTweet(status);
                    tw.createEdgesFromTweet(tweet, status);

                    //System.out.println("Status written in database.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
        };

        TwitterStream twitterStream = new TwitterStreamFactory(cb).getInstance();
        FilterQuery tweetFilterQuery = new FilterQuery();
        //tweetFilterQuery.track(new String[]{"russie", "poutine", "macron", "syrie", "rtenfrancais", "sputnik"});

        System.out.println(Arrays.toString(keywords));
        tweetFilterQuery.track(keywords);
        tweetFilterQuery.language("fr");

        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }

}
