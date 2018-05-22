import io.hgraphdb.HBaseGraph;
import io.hgraphdb.ElementType;
import io.hgraphdb.ValueType;

import io.hgraphdb.HBaseGraphConfiguration;
import io.hgraphdb.HBaseGraphConfiguration.InstanceType;

import org.apache.commons.configuration.Configuration;

public class CreateGraphEdges {
    public static void main(String args []) {
        Configuration cfg = new HBaseGraphConfiguration()
                .setInstanceType(InstanceType.DISTRIBUTED)
                .setGraphNamespace("tweets_graph3")
                .setCreateTables(true)
                .set("hbase.zookeeper.quorum", "127.0.0.1")
                .set("zookeeper.znode.parent", "/hbase")
                .setUseSchema(true);
        HBaseGraph graph = HBaseGraph.open(cfg);

        /* Edges */
        graph.createLabel(ElementType.EDGE, "RETWEETED_STATUS", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "QUOTED_STATUS", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "IN_REPLY_TO", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "HAS_LINK", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "MENTIONS", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "HAS_TAG", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "POSTED", ValueType.STRING);
        graph.createLabel(ElementType.EDGE, "POSTED_VIA", ValueType.STRING);
        System.out.println("Labels created!");

        graph.connectLabels("tweet", "RETWEETED_STATUS", "tweet");
        graph.connectLabels("tweet", "QUOTED_STATUS", "tweet");
        graph.connectLabels("tweet", "IN_REPLY_TO", "tweet");
        graph.connectLabels("tweet", "HAS_LINK", "url");
        graph.connectLabels("tweet", "MENTIONS", "user");
        graph.connectLabels("tweet", "IN_REPLY_TO", "user");
        graph.connectLabels("tweet", "HAS_TAG", "hashtag");
        graph.connectLabels("user", "POSTED", "tweet");
        graph.connectLabels("tweet", "POSTED_VIA", "source");

        graph.connectLabels("tweet", "MENTIONS", "person");
        graph.connectLabels("tweet", "MENTIONS", "organization");
        graph.connectLabels("tweet", "MENTIONS", "location");
        System.out.println("Edges schema created!");
        graph.close();
    }

}
