package com.example.janusgraph.DTO;

import lombok.Data;

/**
 * @Classname EdgeDto
 * @Description
 * @Date 2019/12/17 10:26
 * @Created by Evan
 */

@Data
public class EdgeDTO {

    private long LVRecord;
    private long RVRecord;
    private String Content;
}

