package vn.com.itechcorp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Value("${thread.pool.size.core:4}")
    private int corePoolSize;

    @Value("${thread.pool.size.max:200}")
    private int maxPoolSize;

    @Value("${thread.pool.alive.seconds:180}")
    private int aliveSeconds;

    @Bean("threadPoolExecutor")
    ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(aliveSeconds);
        return executor;
    }

}
