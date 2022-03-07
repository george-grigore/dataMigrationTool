package com.dataMigrationTool.db1.rowMapper;

import com.dataMigrationTool.db1.model.DB1Car;
import com.dataMigrationTool.db1.model.CarType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CarRowMapper implements RowMapper<DB1Car> {

    @Override
    public DB1Car mapRow(ResultSet rs, int rowNum) throws SQLException {
            return DB1Car.builder()
                    .id(rs.getObject("id", UUID.class))
                    .carBrand(rs.getString("car_brand"))
                    .carModel(rs.getString("car_model"))
                    .carType(CarType.valueOf(rs.getString("car_type").trim().toUpperCase()))
                    .build();
    }
}
