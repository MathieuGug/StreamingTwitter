import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import twitter4j.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TwitterToGraph
{
    private Graph g;
    private Twitter twitter;

    // The instance
    public TwitterToGraph(Twitter twitter,
                          Graph graph) {
        g = graph;
        this.twitter = twitter;
    }

    ///////////////////////////////////////////////////////
    //                 CREATE THE VERTICES               //
    ///////////////////////////////////////////////////////
    public Vertex getOrCreateTweet(Status status) {
        System.out.printf("%s - %s : %s\n", status.getId(), status.getUser().getScreenName(), status.getText());

        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("id", status.getId());
        if (vt.hasNext()) {
            g.traversal().V(status.getId()).property("favourites_count", status.getFavoriteCount(),
                    "retweets_count", status.getRetweetCount());

            System.out.println("Tweet data updated.");
            return vt.next();
        }
        else {
            User user = status.getUser();

            Vertex tweet = g.addVertex(T.id, status.getId(), T.label, "tweet",
                    "id", status.getId(),
                    "created_str", status.getCreatedAt().toString(),
                    "created_at", status.getCreatedAt().getTime()/1000,
                    "text", status.getText(),
                    "favourites_count", status.getFavoriteCount(),
                    "retweets_count", status.getRetweetCount(),
                    "retweeted", status.isRetweeted(),
                    "followers_at_time", status.getUser().getFollowersCount(),
                    "friends_at_time", user.getFriendsCount(),
                    "statuses_at_time", user.getStatusesCount(),
                    "listed_at_time", user.getListedCount());
            System.out.println("Tweet created!");
            return tweet;
        }
    }

    public Vertex getOrCreateUser(User u) {
        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("user_key", u.getName());
        if (vt.hasNext()) {
            g.traversal().V(u.getId()).property("favourites_count", u.getFavouritesCount(),
                    "followers_count", u.getFollowersCount(),
                    "friends_count", u.getFriendsCount(),
                    "statuses_count", u.getStatusesCount(),
                    "listed_count", u.getListedCount());
            System.out.println("User updated.");
            return vt.next();
        }
        else {
            System.out.println("User created!");

            Vertex user = g.addVertex(T.id, u.getId(), T.label, "user",
                    "user_key", u.getName(),
                    "screen_name", u.getScreenName(),
                    "created_at", u.getCreatedAt().getTime()/1000,
                    "created_str", u.getCreatedAt().toString(),
                    "favourites_count", u.getFavouritesCount(),
                    "followers_count", u.getFollowersCount(),
                    "listed_count", u.getListedCount(),
                    "friends_count", u.getFriendsCount(),
                    "statuses_count", u.getStatusesCount(),
                    "verified", u.isVerified());

            if (u.getLang() != null) user.property("lang", u.getLang());
            if (u.getLocation() != null) user.property("location", u.getLocation());
            if (u.getDescription() != null) user.property("description", u.getDescription());
            if (u.getTimeZone() != null) user.property("time_zone", u.getTimeZone());

            return user;
        }
    }

    public Vertex getOrCreateMention(UserMentionEntity user) throws Exception {
        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("id", user.getId());
        User u = twitter.showUser(user.getId());

        if (vt.hasNext()) return vt.next();
        else {
            return getOrCreateUser(u);
        }
    }

    public Vertex getOrCreateHashtag(HashtagEntity h) {
        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("tag", h.getText());
        if (vt.hasNext()) return vt.next();
        else {
            return g.addVertex(T.label, "hashtag", "tag", h.getText());
        }
    }

    private Vertex getOrCreateUrl(URLEntity u) {
        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("expanded_url", u.getExpandedURL());
        if (vt.hasNext()) return vt.next();
        else {
            return g.addVertex(T.label, "url", "expanded_url", u.getExpandedURL());
        }
    }

    private Vertex getOrCreateSource(String s) {
        GraphTraversal<Vertex, Vertex> vt = g.traversal().V().has("name", s);
        if (vt.hasNext()) return vt.next();
        else {
            return g.addVertex(T.label, "source", "name", s);
        }
    }

    ///////////////////////////////////////////////////////
    //                  CREATE THE EDGES                 //
    ///////////////////////////////////////////////////////
    public void createEdgesFromTweet(Vertex status, Status tweet) throws Exception {
        getOrCreateUser(tweet.getUser()).addEdge("POSTED", getOrCreateTweet(tweet));
        status.addEdge("POSTED_VIA", getOrCreateSource(tweet.getSource()));

        // Hashtags, mentions, url
        for (HashtagEntity hashtag : tweet.getHashtagEntities()) status.addEdge("HAS_TAG",
                getOrCreateHashtag(hashtag));
        for (UserMentionEntity u : tweet.getUserMentionEntities()) status.addEdge("MENTIONS",
                getOrCreateMention(u));
        for (URLEntity url : tweet.getURLEntities()) status.addEdge("HAS_LINK",
                getOrCreateUrl(url));

        // Quoted, retweeted, in reply to
        if (tweet.getInReplyToStatusId() != -1) {
            Status in_reply_to = twitter.showStatus(tweet.getInReplyToStatusId());
            status.addEdge("IN_REPLY_TO",
                    getOrCreateUser(in_reply_to.getUser()));
        }
        if (tweet.getQuotedStatusId() != -1) status.addEdge("QUOTED_STATUS",
                getOrCreateTweet(tweet.getQuotedStatus()));
        if (tweet.isRetweet()) status.addEdge("RETWEETED_STATUS",
                getOrCreateTweet(tweet.getRetweetedStatus()));

    }
}