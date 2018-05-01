package streaming;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDslProcessor;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.*;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.*;
//import org.apache.tinkerpop.gremlin.process.traversal.step.filter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class TinkerGraphTest {
    public static void main(String args []) {
        TinkerGraph tg = TinkerGraph.open() ;
        try {
            tg.io(IoCore.graphml()).readGraph("/Users/Mathieu/russia.graphml");
        } catch( IOException e ) {
            System.out.println("File not found");
            System.exit(1);
        }

        GraphTraversalSource g = tg.traversal();
        //SocialTraversalSource social = tg.traversal(SocialTraversalSource.class);
        //social.V().has("name","marko").knows("josh");
        Map<String,?> np = g.V().has("user", "screen_name", "NewsPolitiques_").valueMap().next();
        System.out.println(np);

        List fc = (List)(np.get("followers_count"));
        System.out.println("Nb of NewsPolitiques_ followers: " + fc.get(0));

        np.forEach( (k,v) -> System.out.println("Key: " + k + ": Value: " + v));
        // Users with political affiliation
        //Map<String, ?> depute = g.V().hasLabel("user").has("description").
        //    order().by("followers_count", Order.decr).next();
        //Vertex depute = g.V().hasLabel("user").has("description").
        //        order().by("followers_count", Order.decr).next();

        List<Map<String, ?>> depute = g.V().has("user").has("description")


    }

}
