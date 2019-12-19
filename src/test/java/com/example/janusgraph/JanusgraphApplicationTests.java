package com.example.janusgraph;

import com.example.janusgraph.config.GraphSourceConfig;
import com.example.janusgraph.config.JanusGraphConfig;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JanusgraphApplicationTests {


    GraphSourceConfig config = new GraphSourceConfig();

    @Autowired
    JanusGraphConfig janusGraphConfig;


    @Test
    public void test() throws Exception {
        GraphTraversalSource g = config.getGts1();

        long userVertexId = 123456l;
        long vertexId = ((StandardJanusGraph) janusGraphConfig.graph).getIDManager().toVertexId(userVertexId);
        System.out.println(vertexId);
        Vertex next = g.addV("测试1")
                .property(T.id,vertexId)
                .property("name", "测试1")
                .property("age", "25")
                .property("no", "11111111")
                .next();
    }

    @Test
    public void testAdd2() throws Exception {
        GraphTraversalSource g = config.getGts2();
        long userVertexId = 12345678l;
        long vertexId = ((StandardJanusGraph) janusGraphConfig.graph).getIDManager().toVertexId(userVertexId);
        System.out.println(vertexId);
        try {
            g.addV("测试2")
                    .property(T.id,vertexId)
//                    .property("name", "测试2", "no", "2222222", "addr", "北京市海淀区")
                    .next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            g.close();
        }
    }

    @Test
    public void testAdd31() {
        GraphTraversalSource g = null;
        Client client = config.getClient();
        long userVertexId = 12345678l;
        long vertexId = ((StandardJanusGraph) janusGraphConfig.graph).getIDManager().toVertexId(userVertexId);
        System.out.println(vertexId);
        try {
            g.addV("测试3")
                    .property(T.id,vertexId)
                    .next();
//            client.submit("g.addV('测试3').property('name','测试3','no','3333333','addr','北京市海淀区')");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            config.close(g, client);
        }
    }

    @Test
    public void testAdd4() throws Exception {
        Client client = config.getClient();
        GraphTraversalSource g = config.getGts4(client);
        try {
            g.addV("测试444").property("name", "测试444", "no", "44444444").next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            config.close(g, client);

        }
    }


    @Test
    public void testGetVal() throws Exception {
        GraphTraversalSource g = config.getGts1();
        GraphTraversal<Vertex, Map<Object, Object>> vmgt = g.V().valueMap(true);
        System.out.println(vmgt.hasNext());
        while (vmgt.hasNext()) {
            System.out.println(vmgt.next().toString());
        }
    }

}