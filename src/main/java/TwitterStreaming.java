import io.hgraphdb.HBaseGraph;
import io.hgraphdb.HBaseGraphConfiguration;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreaming {
    public static void main(String args []) throws Exception {
        // CHECKING INPUT PARAMETERS
        final ParameterTool params = ParameterTool.fromArgs(args);
        //System.out.println("Usage: TwitterExample [--output <path>] " +
        //        "[--twitter-source.consumerKey <key> --twitter-source.consumerSecret <secret> --twitter-source.token <token> --twitter-source.tokenSecret <tokenSecret>]");

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
        streamFeed(twitter_cfg, twitter);
    }

    private static void streamFeed(twitter4j.conf.Configuration cb, Twitter twitter) {
        org.apache.commons.configuration.Configuration cfg = new HBaseGraphConfiguration()
                .setInstanceType(HBaseGraphConfiguration.InstanceType.DISTRIBUTED)
                .setGraphNamespace("tweets_graph2")
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
                    System.out.println("--------");
                    Vertex tweet;

                    ///////////////////////////////////////////////////////
                    //     POPULATE THE GRAPH FROM VARIOUS SEEDS         //
                    ///////////////////////////////////////////////////////
                    // Add the status :
                    tweet = tw.getOrCreateTweet(status);
                    tw.createEdgesFromTweet(tweet, status);

                    System.out.println("Status written in database.");
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
        tweetFilterQuery.track(new String[]{"bieber"});

        twitterStream.addListener(listener);
        twitterStream.filter(tweetFilterQuery);
    }
}
