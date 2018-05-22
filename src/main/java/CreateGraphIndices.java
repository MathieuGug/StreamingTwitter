import io.hgraphdb.HBaseGraph;
import io.hgraphdb.ElementType;
import io.hgraphdb.ValueType;

import io.hgraphdb.HBaseGraphConfiguration;
import io.hgraphdb.HBaseGraphConfiguration.InstanceType;

import org.apache.commons.configuration.Configuration;

import java.time.LocalDateTime;

public class CreateGraphIndices {
    public static  void main(String args []) throws Exception {
        Configuration cfg = new HBaseGraphConfiguration()
                .setInstanceType(InstanceType.DISTRIBUTED)
                .setGraphNamespace("tweets_graph3")
                .setCreateTables(true)
                .set("hbase.zookeeper.quorum", "127.0.0.1")
                .set("zookeeper.znode.parent", "/hbase")
                .setUseSchema(true);
        HBaseGraph graph = HBaseGraph.open(cfg);

        graph.createIndex(ElementType.VERTEX, "user", "screen_name");
        graph.createIndex(ElementType.VERTEX, "tweet", "created");
        graph.createIndex(ElementType.VERTEX, "person", "name");
        graph.createIndex(ElementType.VERTEX, "organization", "name");
        graph.createIndex(ElementType.VERTEX, "location", "name");
        graph.createIndex(ElementType.VERTEX, "source", "name");
        graph.createIndex(ElementType.VERTEX, "hashtag", "tag");
        graph.createIndex(ElementType.VERTEX, "url", "domain");
        graph.createIndex(ElementType.VERTEX, "url", "expanded_url");
        System.out.println("Indices created");

        graph.close();
    }
}
