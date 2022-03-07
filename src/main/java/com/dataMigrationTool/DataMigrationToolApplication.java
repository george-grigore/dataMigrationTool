package com.dataMigrationTool;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableBatchProcessing()
public class DataMigrationToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataMigrationToolApplication.class, args);
	}

}
