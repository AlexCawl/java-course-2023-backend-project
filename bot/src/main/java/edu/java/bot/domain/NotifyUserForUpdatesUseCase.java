package edu.java.bot.domain;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import io.micrometer.core.instrument.Counter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotifyUserForUpdatesUseCase {
    private final static String NOTIFICATION_TEMPLATE = "Link [%s] updated!\nChanges:\n%s";
    private final static String LOG_TEMPLATE = "Unable to notify user [%d] with updates [%s]";

    private final TelegramBot bot;
    private final Counter processedMessagesCounter;

    public void notify(Long userId, String message, String url) {
        try {
            SendMessage sendMessage = new SendMessage(userId, String.format(NOTIFICATION_TEMPLATE, url, message));
            bot.execute(sendMessage);
            processedMessagesCounter.increment();
        } catch (Exception exception) {
            log.warn(String.format(LOG_TEMPLATE, userId, message));
        }
    }
}
