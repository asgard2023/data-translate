package cn.org.opendfl.translate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {
    /**
     * Set the ThreadPoolExecutor's core pool size.
     */
    private int corePoolSize = 10;
    /**
     * Set the ThreadPoolExecutor's maximum pool size.
     */
    private int maxPoolSize = 200;
    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     */
    private int queueCapacity = 50;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
