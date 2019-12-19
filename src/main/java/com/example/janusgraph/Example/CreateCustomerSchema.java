package com.example.janusgraph.Example;


import lombok.extern.log4j.Log4j2;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CreateCustomerSchema {

    protected boolean useMixedIndex = true;
    protected String mixedIndexConfigName = "search";


    /**
     *  定义图的顶点
     * @param management
     */
    public void createCustomerVertexLabels(JanusGraphManagement management) {
        management.makeVertexLabel(CustomerSchemaVertex.CUSTOMERSCHEMAVERTEX).make();
        management.makeVertexLabel(CustomerSchemaVertex.LOCALHOST).make();
    }

    /**
     *  定义图的属性
     * @param management
     */
    public void createProperties(JanusGraphManagement management) {
        management.makePropertyKey(CustomerSchemaProperties.NAME).dataType(String.class).make();
        management.makePropertyKey(CustomerSchemaProperties.PROPERTY).dataType(String.class).cardinality(Cardinality.SET).make();
        management.makePropertyKey(CustomerSchemaProperties.VALUE).dataType(String.class).cardinality(Cardinality.SET).make();
    }




}
