package cn.org.opendfl.translate.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Data
@Configuration
@ConfigurationProperties(prefix = "task-pool")
@Slf4j
public class TaskPoolConfig {
    /**
     * Set the ThreadPoolExecutor's core pool size.
     */
    private int corePoolSize = 10;
    /**
     * Set the ThreadPoolExecutor's maximum pool size.
     */
    private int maxPoolSize = 10;
    /**
     * Set the capacity for the ThreadPoolExecutor's BlockingQueue.
     */
    private int queueCapacity = 50;

    private String threadNamePrefix = "async-";

    @Bean
    public Executor asyncExecutor() {
        log.info("-----task-pool--corePoolSize={} maxPoolSize={} queueCapacity={}", corePoolSize, maxPoolSize, queueCapacity);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
