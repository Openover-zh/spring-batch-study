package com.langfeiyes.batch._05_job_listener;

import com.langfeiyes.batch._04_param_incr.DailyTimestampParamIncrementer;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

//开启 spring batch 注解--可以让spring容器创建springbatch操作相关类对象
@EnableBatchProcessing
//springboot 项目，启动注解， 保证当前为为启动类
@SpringBootApplication
public class StatusListenerJob {

    //作业启动器
    @Autowired
    private JobLauncher jobLauncher;

    //job构造工厂---用于构建job对象
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    //step 构造工厂--用于构造step对象
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    //构造一个step对象执行的任务（逻辑对象）
    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                System.out.println("----------------");
//                StepExecution stepExecution = contribution.getStepExecution();
//
//
//
                //要执行逻辑--step步骤执行逻辑
                JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
                System.out.println("作业执行中的状态:" + jobExecution.getStatus());
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }

    //创建监听器交给容器管理
//    @Bean
    public JobStateListener jobStateListener(){
        return new JobStateListener();
    }
    @Bean
    public JobStateAnnoListener jobStateAnnoListener(){
        return new JobStateAnnoListener();
    }

    //构造一个step对象
    @Bean
    public Step step1(){
        //tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
        return stepBuilderFactory.get("step1").tasklet(tasklet())
                .build();
    }

    @Bean
    public RunIdIncrementer jobIncrementer(){
        return new RunIdIncrementer();
    }

    @Bean
    public  Job job(){
        return jobBuilderFactory.get("job-state-anno-listener-job2")
                .start(step1())
//                .listener(jobStateListener())
//                .listener(jobStateAnnoListener())
                .incrementer(jobIncrementer())
                .listener(JobListenerFactoryBean.getListener(new JobStateAnnoListener()))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(StatusListenerJob.class, args);
    }



}
