package com.example.janusgraph;


import com.example.janusgraph.DTO.EdgeDTO;
import com.example.janusgraph.DTO.VertexDTO;
import com.example.janusgraph.config.GraphSourceConfig;
import com.example.janusgraph.config.JanusGraphConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.NoSuchElementException;


/**
 * @Classname OwnthinkETL
 * @Description
 * @Date 2019/12/11 16:04
 * @Created by Evan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OwnthinkETL {

    @Autowired
    GraphSourceConfig config;
    //= new GraphSourceConfig();

    @Autowired
    JanusGraphConfig janusGraphConfig;

    private void vertexAdd(VertexDTO vertexDTO) {
        Client client = config.getClient();
        GraphTraversalSource g = config.getGts4(client);
        try {
            g.addV(vertexDTO.getName())
                    .property(T.id, vertexDTO.getId())
                    .next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            config.close(g, client);
        }
    }

    private void edgeAdd(EdgeDTO edgeDTO) {
        Client client = config.getClient();
        GraphTraversalSource g = config.getGts4(client);
        try {

            Vertex lVRecord = g.V(edgeDTO.getLVRecord()).limit(1).next();
            Vertex rVRecord = g.V(edgeDTO.getRVRecord()).limit(1).next();
            g.V(lVRecord).as("a").V(rVRecord).addE(edgeDTO.getContent()).from("a").next();

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            config.close(g, client);
        }
    }


    @Test
    public void insertVertex() {
        LineIterator it = null;
        File file = new File("vertex1576655764272.csv");
        try {
            it = FileUtils.lineIterator(file, "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] split = line.split(",");
                if (split.length != 2) {
                    continue;
                }
                if (StringUtils.isNotBlank(split[0])) {
                    VertexDTO vertexDTO = new VertexDTO();
                    long vertexId = Long.parseLong(split[0]);
                    vertexDTO.setId(vertexId);
                    vertexDTO.setName(split[1]);
                    vertexAdd(vertexDTO);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
    }


    @Test
    public void insertEdge() {
        LineIterator it = null;
        File file = new File("edge.csv");
        try {
            it = FileUtils.lineIterator(file, "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] split = line.split(",");
                if (split.length != 3) {
                    continue;
                }
                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setLVRecord(Long.parseLong(split[0]));
                edgeDTO.setRVRecord(Long.parseLong(split[1]));
                edgeDTO.setContent(split[2]);
                edgeAdd(edgeDTO);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
    }
}
