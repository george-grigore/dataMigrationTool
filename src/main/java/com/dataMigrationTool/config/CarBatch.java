package com.dataMigrationTool.config;

import com.dataMigrationTool.db1.model.DB1Car;
import com.dataMigrationTool.db1.rowMapper.CarRowMapper;
import com.dataMigrationTool.db2.model.DB2CarBrand;
import com.dataMigrationTool.db2.model.DB2CarType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Configuration
public class CarBatch {

    @Autowired
    @Qualifier("db1")
    private DataSource db1;

    @Autowired
    @Qualifier("db2")
    private DataSource db2;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private static final Integer CHUNK_SIZE = 10;

    @Bean
    public Job dataMigrationJob() throws Exception {

        Flow flowCarToCarBrand = new FlowBuilder<SimpleFlow>("flowCarToCarBrand")
                .start(migrationCarToCarBrandStep())
                .build();

        Flow flowCarToCarType = new FlowBuilder<SimpleFlow>("flowCarToCarType")
                .start(migrationCarToCarTypeStep())
                .build();

        Flow parallelFlow = new FlowBuilder<SimpleFlow>("slaveFlow")
                .split(new SimpleAsyncTaskExecutor()).add(flowCarToCarBrand, flowCarToCarType).build();

        return jobBuilderFactory.get("job_car")
                .incrementer(new RunIdIncrementer())
                .start(parallelFlow)
                .next(migrationCarToCarStep())
                .end()
                .build();
    }

    //***********  car ---> car_brand  ***********
    @Bean
    public Step migrationCarToCarBrandStep() throws Exception {
        return stepBuilderFactory.get("step_car_carBrand")
                .<String, DB2CarBrand>chunk(CHUNK_SIZE)
                .reader(readDistinctCarBrand())
                .processor(processorDistinctCarBrand())
                .writer(writeToCarBrandTable())
                .build();
    }

    @Bean
    public PagingQueryProvider queryProviderDistinctCarBrand() throws Exception {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(db1);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select distinct car_brand");
        sqlPagingQueryProviderFactoryBean.setFromClause("car");
        sqlPagingQueryProviderFactoryBean.setSortKey("car_brand");
        return sqlPagingQueryProviderFactoryBean.getObject();
    }

    @Bean
    public JdbcPagingItemReader<String> readDistinctCarBrand() throws Exception {
        return new JdbcPagingItemReaderBuilder<String>()
                .name("readDistinctCardBrand")
                .dataSource(db1)
                .pageSize(CHUNK_SIZE)
                .queryProvider(queryProviderDistinctCarBrand())
                .rowMapper((rs, rowNum) -> rs.getString("car_brand"))
                .build();
    }

    @Bean
    public ItemProcessor<String, DB2CarBrand> processorDistinctCarBrand() {
        return carBrand -> DB2CarBrand.builder()
                .id(UUID.randomUUID())
                .name(carBrand)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DB2CarBrand> writeToCarBrandTable() {
        return new JdbcBatchItemWriterBuilder<DB2CarBrand>()
                .dataSource(db2)
                .sql("insert into car_brand (id, name) values(:id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<DB2CarBrand>())
                .build();
    }
    //***********  car ---- car_brand  ***********


    //***********  car ---> car_type  ***********
    @Bean
    public Step migrationCarToCarTypeStep() throws Exception {
        return stepBuilderFactory.get("step_car_carType")
                .<String, DB2CarType>chunk(CHUNK_SIZE)
                .reader(readDistinctCarType())
                .processor(processorDistinctCardType())
                .writer(writeToCarTypeTable())
                .build();
    }

    @Bean
    public PagingQueryProvider queryProviderDistinctCarType() throws Exception {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(db1);
        sqlPagingQueryProviderFactoryBean.setSelectClause("select distinct car_type");
        sqlPagingQueryProviderFactoryBean.setFromClause("car");
        sqlPagingQueryProviderFactoryBean.setSortKey("car_type");
        return sqlPagingQueryProviderFactoryBean.getObject();
    }

    @Bean
    public JdbcPagingItemReader<String> readDistinctCarType() throws Exception {
        return new JdbcPagingItemReaderBuilder<String>()
                .name("readDistinctCardType")
                .dataSource(db1)
                .pageSize(CHUNK_SIZE)
                .queryProvider(queryProviderDistinctCarType())
                .rowMapper((rs, rowNum) -> rs.getString("car_type"))
                .build();
    }

    @Bean
    public ItemProcessor<String, DB2CarType> processorDistinctCardType() {
        return carType -> DB2CarType.builder()
                .id(UUID.randomUUID())
                .name(carType)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DB2CarType> writeToCarTypeTable() {
        return new JdbcBatchItemWriterBuilder<DB2CarType>()
                .dataSource(db2)
                .sql("insert into car_type (id, name) values(:id, :name)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<DB2CarType>())
                .build();
    }
    //***********  car ---- car_type  ***********


    //***********  car ---> car  ***********
    @Bean
    public Step migrationCarToCarStep() {
        return stepBuilderFactory.get("step_car_car")
                .<DB1Car, DB1Car>chunk(CHUNK_SIZE)
                .reader(readCar())
                .writer(writeToCarTable())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<DB1Car> readCar() {
        return new JdbcCursorItemReaderBuilder<DB1Car>()
                .name("readCard")
                .dataSource(db1)
                .sql("select id, car_brand, car_model, car_type from car order by id")
                .rowMapper(new CarRowMapper())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<DB1Car> writeToCarTable() {
        return new JdbcBatchItemWriterBuilder<DB1Car>()
                .dataSource(db2)
                .sql("insert into car (name, car_brand_id, car_type_id) values " +
                        "(  ?, " +
                        "   (select id from car_brand where name like ?), " +
                        "   (select id from car_type where name like ?)" +
                        ")")
                .itemPreparedStatementSetter(new DB1CarStatementSetter())
                .build();
    }

    class DB1CarStatementSetter implements ItemPreparedStatementSetter<DB1Car> {
        @Override
        public void setValues(DB1Car db1Car, PreparedStatement preparedStatement) throws SQLException {
            preparedStatement.setString(1, db1Car.getCarModel());
            preparedStatement.setString(2, db1Car.getCarBrand());
            preparedStatement.setString(3, db1Car.getCarType().name().toLowerCase());
        }
    }
    //***********  car ---- car  ***********

}
