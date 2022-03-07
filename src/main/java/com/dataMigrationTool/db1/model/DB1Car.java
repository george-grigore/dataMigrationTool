package com.dataMigrationTool.db1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DB1Car {

    private UUID id;
    private String carBrand;
    private String carModel;
    private CarType carType;

}
