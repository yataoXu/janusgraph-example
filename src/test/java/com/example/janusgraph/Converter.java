package com.example.janusgraph;

import com.example.janusgraph.config.JanusGraphConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @Classname CsvUtil
 * @Description
 * @Date 2019/12/11 16:12
 * @Created by Evan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
@Slf4j
public class Converter {


    @Autowired
    private JanusGraphConfig janusGraphConfig;


    public int insertFileOfCsv(JanusGraphConfig janusGraphConfig) {


        File srcfile = new File("C:\\Users\\xu.yatao\\Desktop\\rdf-converter\\ownthink_v2.csv");
        if (!srcfile.exists()) {
            throw new RuntimeException("该文件" + srcfile.getPath() + "不存在");
        }

        String vFilePath = "./vertex" + System.currentTimeMillis() + ".csv";
        String eFilePath = "./edge" + System.currentTimeMillis() + ".csv";

        File vFilePathFile = new File(vFilePath);
        File eFilePathFile = new File(eFilePath);

        try {
            vFilePathFile.createNewFile();
            eFilePathFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashSet<Integer> set = new HashSet();

        int lineNum = 0;
        int numErrorLines = 0;
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(srcfile, "UTF-8");

            List<String> vContent = new LinkedList();
            List<String> eContent = new LinkedList();
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] split = line.split(",");

                if (split.length != 3) {
                    numErrorLines++;
                    continue;
                }
                if (StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1]) || StringUtils.isBlank(split[2])) {
                    numErrorLines++;
                    continue;
                }

                lineNum++;

                int lVRecordHashVal = split[0].hashCode() & Integer.MAX_VALUE;
                int rVRecordHashVal = split[2].hashCode() & Integer.MAX_VALUE;

                long lVRecordVertexId = ((StandardJanusGraph) janusGraphConfig.graph).getIDManager().toVertexId(lVRecordHashVal);
                long rVRecordVertexId = ((StandardJanusGraph) janusGraphConfig.graph).getIDManager().toVertexId(rVRecordHashVal);

                if (!set.contains(lVRecordHashVal)) {
                    set.add(lVRecordHashVal);
                    vContent.add(lVRecordVertexId + "," + split[0] + "\n");
                }
                if (!set.contains(rVRecordHashVal)) {
                    set.add(rVRecordHashVal);
                    vContent.add(rVRecordVertexId + "," + split[2] + "\n");
                }
                eContent.add(lVRecordVertexId + "," + rVRecordVertexId + "," + split[1] + "\n");

                if (lineNum % 100000 == 0) {
                    writeFile(vFilePathFile, vContent);
                    writeFile(eFilePathFile, eContent);
                    vContent.clear();
                    eContent.clear();
                    log.info("处理了{}行", lineNum);
                }

                if (lineNum % 10000000 == 0) {
                    vFilePath = "./vertex" + System.currentTimeMillis() + ".csv";
                    eFilePath = "./edge" + System.currentTimeMillis() + ".csv";
                    vFilePathFile = new File(vFilePath);
                    eFilePathFile = new File(eFilePath);
                    try {
                        vFilePathFile.createNewFile();
                        eFilePathFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
        return lineNum;
    }

    @Async
    public void writeFile(File filePath, List<String> contentList) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(filePath, true);
            bw = new BufferedWriter(fw);
            for (String content : contentList) {
                fw.write(content);
            }
            fw.flush();
            fw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw) {
                    fw.close();
                }
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testConverter() {
        long startTime = System.currentTimeMillis();
        int totalNum = insertFileOfCsv(janusGraphConfig);
        long endTime = System.currentTimeMillis();
        log.info("总共处理了{}", totalNum);
        log.info("总耗时{}", endTime - startTime);

    }
}



