package com.example.janusgraph.config;

import lombok.extern.log4j.Log4j2;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.springframework.context.annotation.Configuration;

import java.net.URLDecoder;

/**
 * @author Evan
 * @version V1.0
 * @description :
 * @date 2019/9/3 17:09
 */
@Configuration
@Log4j2
public class JanusGraphConfig {
    private static final String CONFIG_FILE = "conf/janusgraph-cql-es-server.properties";
    public final JanusGraph graph;
    public final JanusGraphManagement mgt;

    /**
     * Initialize the graph and the graph management interface.
     * 使用无参构造
     */
    public JanusGraphConfig() {
        try {
//            this.dropOldKeyspace();
        } catch (Exception ex) {
            log.info("Cannot drop keyspace janusgraph");
        }
        String path = Thread.currentThread()
                .getContextClassLoader()
                .getResource(CONFIG_FILE).getPath().substring(1);

        String decode = "";
        try {
            decode = URLDecoder.decode(path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取配置文件路径错误原因：" + e.getLocalizedMessage());
        }
        log.info("Connecting graph");
        graph = JanusGraphFactory.open(decode);
        log.info("Getting management");
        mgt = graph.openManagement();
    }

    public void close() {
        mgt.commit();
        graph.close();
    }

    public void rollback() {
        Transaction tx = graph.tx();
        tx.rollback();
    }



    /**
     *  此方法也可以获取
     * @return
     */
//    public JanusGraph getJanusGraph1() {
//        JanusGraphFactory.Builder build = JanusGraphFactory.build()
//                .set("storage.backend", "hbase")
//                .set("storage.cql.keyspace", "janusgraph")
//                .set("storage.hostname", "10.2.196.19")
//                .set("index.search.backend", "elasticsearch")
//                .set("index.search.hostname", "10.2.196.18")
//                .set("cache.db-cache", "true")
//                .set("cache.db-cache-time", "3000000")
//                .set("cache.db-cache-size", "0.25");
//        JanusGraph janusGraph = build.open();
//        boolean open = janusGraph.isOpen();
//        if (open) {
//            System.out.println("janusgraph open");
//            return janusGraph;
//        }
//        return null;
//    }
}
