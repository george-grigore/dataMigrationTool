package com.dataMigrationTool.db2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DB2Car {

    private Integer id;
    private String name;
    private UUID carBrandId;
    private UUID carTypeId;

}
