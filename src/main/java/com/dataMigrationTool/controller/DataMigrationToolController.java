package com.dataMigrationTool.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class DataMigrationToolController {

    private Job job;
    private JobLauncher jobLauncher;

    public DataMigrationToolController(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @GetMapping("/startMigration")
    public void startMigration() {

        JobParameters jobParameter1 = new JobParametersBuilder()
                .addString("job_car_carBrand", "dataMigrationJob11289")
                .toJobParameters();

        try {
            jobLauncher.run(job, jobParameter1).getExitStatus().getExitCode();
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }

    }

}
