import io.hgraphdb.HBaseGraph;
import io.hgraphdb.ElementType;
import io.hgraphdb.ValueType;

import io.hgraphdb.HBaseGraphConfiguration;
import io.hgraphdb.HBaseGraphConfiguration.InstanceType;

import org.apache.commons.configuration.Configuration;

public class CreateGraphNodes {
    public static void main(String[] args) {
        Configuration cfg = new HBaseGraphConfiguration()
                .setInstanceType(InstanceType.DISTRIBUTED)
                .setGraphNamespace("tweets_graph2")
                .setCreateTables(true)
                .set("hbase.zookeeper.quorum", "127.0.0.1")
                .set("zookeeper.znode.parent", "/hbase")
                .setUseSchema(true);
        HBaseGraph graph = HBaseGraph.open(cfg);

        // Schema of the database

        //User
        graph.createLabel(ElementType.VERTEX, "user", ValueType.LONG,
                "id", ValueType.LONG,
                "user_key", ValueType.STRING,
                "screen_name", ValueType.STRING,
                "created_at", ValueType.LONG,
                "created_str", ValueType.STRING,
                "favourites_count", ValueType.INT,
                "followers_count", ValueType.INT,
                "listed_count", ValueType.INT,
                "friends_count", ValueType.INT,
                "statuses_count", ValueType.INT,
                "lang", ValueType.STRING,
                "time_zone", ValueType.STRING,
                "verified", ValueType.BOOLEAN,
                "description", ValueType.STRING,
                "location", ValueType.STRING);
        System.out.println("User OK!");

        // Tweet
        graph.createLabel(ElementType.VERTEX, "tweet", /* id */ ValueType.LONG,
                "id", ValueType.LONG,
                "created_at", ValueType.LONG,
                "created_str", ValueType.STRING,
                "text", ValueType.STRING,
                "favourites_count", ValueType.INT,
                "retweets_count", ValueType.INT,
                "replies_count", ValueType.INT,
                "retweeted", ValueType.BOOLEAN,
                "followers_at_time", ValueType.INT,
                "friends_at_time", ValueType.INT,
                "statuses_at_time", ValueType.INT,
                "listed_at_time", ValueType.INT);
        System.out.println("Tweet OK!");


        // Person/organization/location
        graph.createLabel(ElementType.VERTEX, "person", ValueType.STRING,
                "name", ValueType.STRING);

        graph.createLabel(ElementType.VERTEX, "organization", ValueType.STRING,
                "name", ValueType.STRING);

        graph.createLabel(ElementType.VERTEX, "location", ValueType.STRING,
                "name", ValueType.STRING,
                "country", ValueType.STRING);
        System.out.println("Person/orga/location OK!");

        // Hashtag
        graph.createLabel(ElementType.VERTEX, "hashtag", /* id */ ValueType.STRING, "tag", ValueType.STRING);
        System.out.println("Hashtag OK!");

        // URL
        graph.createLabel(ElementType.VERTEX, "url", /* id */ ValueType.STRING,
                "expanded_url", ValueType.STRING,
                "domain", ValueType.STRING);
        System.out.println("URL OK!");

        // Source
        graph.createLabel(ElementType.VERTEX, "source", /* id */ ValueType.STRING, "name", ValueType.STRING);
        System.out.println("Source OK!");
        System.out.println("Database created!");

        graph.close();
    }

}
