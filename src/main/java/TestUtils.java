import io.hgraphdb.HBaseGraph;
import io.hgraphdb.HBaseGraphConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {
    public static void main(String args []) {
        Configuration cfg = new HBaseGraphConfiguration()
                .setInstanceType(HBaseGraphConfiguration.InstanceType.DISTRIBUTED)
                .setGraphNamespace("tweets_graph3")
                .setCreateTables(true)
                .set("hbase.zookeeper.quorum", "127.0.0.1")
                .set("zookeeper.znode.parent", "/hbase")
                .setUseSchema(true);
        HBaseGraph graph = HBaseGraph.open(cfg);

        // On dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        GraphTraversal<Vertex, Vertex> vt = graph.traversal().V().has("created", P.gt(LocalDateTime.parse("2018-05-19 00:00", formatter)));
        System.out.println(vt.next());
    }
}
