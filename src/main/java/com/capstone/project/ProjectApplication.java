package com.capstone.project;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@RequiredArgsConstructor
public class ProjectApplication {
    private final Runnable MessageListener;

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor) {
        return new CommandLineRunner() {
            public void run(String... args) throws Exception {
                executor.execute(MessageListener);
            }
        };
    }
}
