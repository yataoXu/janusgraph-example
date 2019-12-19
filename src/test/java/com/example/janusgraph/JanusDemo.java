package com.example.janusgraph;

import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;

import java.io.IOException;

/**
 * @Classname JanusDemo
 * @Description
 * @Date 2019/12/13 14:44
 * @Created by Evan
 */
public class JanusDemo {

    public static void main(String[] args) {
        JanusDemo janusDemo = new JanusDemo();
        JanusGraph graph = janusDemo.getJanusGraph();
        JanusGraphTransaction tx = graph.newTransaction();
        
    }


    public JanusGraph getJanusGraph() {
        JanusGraphFactory.Builder build = JanusGraphFactory.build()
                .set("storage.backend", "hbase")
                .set("storage.cql.keyspace", "janusgraph")
                .set("storage.hostname", "10.2.196.18")
                .set("index.search.backend", "elasticsearch")
                .set("index.search.hostname", "10.2.196.18")
                .set("cache.db-cache", "true")
                .set("cache.db-cache-time", "3000000")
                .set("cache.db-cache-size", "0.25")
                .set("graph.set-vertex-id", "true");

        JanusGraph graph = build.open();
        boolean open = graph.isOpen();
        if (open) {
            System.out.println("[Pre-import]  Node count: " + graph.traversal().V().count().next());
            try {
                graph.io(IoCore.graphml()).readGraph("core/src/test/resources/tinkerpop-modern.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[Post-import] Node count: " + graph.traversal().V().count().next());
            System.out.println("janusgraph open");
            return graph;
        }
        return null;
    }
}
