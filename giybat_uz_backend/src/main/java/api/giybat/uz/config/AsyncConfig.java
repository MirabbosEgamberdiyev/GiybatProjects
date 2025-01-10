package api.giybat.uz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Adjust based on your needs
        executor.setMaxPoolSize(10); // Adjust based on your needs
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("EmailSender-");
        executor.initialize();
        return executor;
    }
}
