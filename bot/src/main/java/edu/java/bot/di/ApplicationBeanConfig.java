package edu.java.bot.di;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.core.util.ApiQualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class ApplicationBeanConfig {
    @Bean
    public TelegramBot provideBot(ApplicationConfig config) {
        return new TelegramBot(config.telegramToken());
    }

    @Bean
    @ApiQualifier("scrapper")
    public String provideScrapperEndpoint(ApplicationConfig config) {
        return config.scrapper();
    }
}
