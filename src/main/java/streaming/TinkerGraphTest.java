package streaming;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.GremlinDslProcessor;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.*;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Column;
import org.apache.tinkerpop.gremlin.structure.T;

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
            tg.io(IoCore.graphml()).readGraph("/Users/Mathieu/russia2.graphml");
        } catch( IOException e ) {
            System.out.println("File not found");
            System.exit(1);
        }

        GraphTraversalSource g = tg.traversal();
        //SocialTraversalSource social = tg.traversal(SocialTraversalSource.class);
        //social.V().has("name","marko").knows("josh");
        //Map<String,?> np = g.V().has("user", "screen_name", "NewsPolitiques_").valueMap().next();
        //System.out.println(np);

        //List fc = (List)(np.get("followers_count"));
        //System.out.println("Nb of NewsPolitiques_ followers: " + fc.get(0));

        //np.forEach( (k,v) -> System.out.println("Key: " + k + ": Value: " + v));
        // Users with political affiliation
        //Map<String, ?> depute = g.V().hasLabel("user").has("description").
        //    order().by("followers_count", Order.decr).next();
        //Vertex depute = g.V().hasLabel("user").has("description").
        //        order().by("followers_count", Order.decr).next();


        Map<?, Long> nb_retweets = g.V().hasLabel("user").as("u1").
                out().out("RETWEETED_STATUS").
                in("POSTED").as("u2").
                select("u1", "u2").by("screen_name").
                groupCount().by(Column.values).next();
        //nb_retweets.forEach((k,v) -> System.out.println(k + "," + v));

        TinkerGraph rt = TinkerGraph.open();
        GraphTraversalSource rt_g = rt.traversal();

        for (Map.Entry<?, Long> entry : nb_retweets.entrySet()) {
            ArrayList users = (ArrayList) entry.getKey();
            Long weight = entry.getValue();

            // Corresponding users
            GraphTraversal<Vertex, Vertex> u1 = rt_g.V().has("user", "screen_name", users.get(0));
            GraphTraversal<Vertex, Vertex> u2 = rt_g.V().has("user", "screen_name", users.get(1));

            //If first user does not exist yet
            if (!u1.hasNext()) {
                System.out.println("user " + users.get(0) + " created");
                rt_g.addV("user").property("screen_name", users.get(0));
            }

            if (!u2.hasNext()) rt_g.addV("user").property("screen_name", users.get(1));

            rt_g.V().has("user", "screen_name", users.get(0)).as("u1").
                    V().has("user", "screen_name", users.get(1)).as("u2").
                    addE("RETWEETED_USER").property("weight", weight).
                    from("u1").to("u2").
                    iterate();

            System.out.println(rt_g.V().has("user", "screen_name", "bridgetc33").valueMap().next().toString());
        }

        try {
            rt.io(IoCore.graphml()).writeGraph("/Users/Mathieu/russia_rt2.xml");
        } catch (IOException e) {
            System.out.println("File not found");
            System.exit(1);
        }
    }

}
