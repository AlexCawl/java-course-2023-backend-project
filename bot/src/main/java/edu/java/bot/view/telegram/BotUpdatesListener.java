package edu.java.bot.view.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.view.telegram.command.CommandHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final BotExceptionHandler exceptionHandler;
    private final List<CommandHandler> commandHandlers;

    @PostConstruct
    private void subscribe() {
        List<BotCommand> botCommands = commandHandlers.stream().map(CommandHandler::convertToApi).toList();
        SetMyCommands commands = new SetMyCommands(botCommands.toArray(BotCommand[]::new));
        telegramBot.execute(commands);
        telegramBot.setUpdatesListener(this, exceptionHandler);
    }

    @PreDestroy
    private void unsubscribe() {
        telegramBot.shutdown();
    }

    @Override
    public int process(List<Update> list) {
        for (Update update : list) {
            Optional<CommandHandler> handler = commandHandlers.stream()
                .filter(commandHandler -> {
                    try {
                        Matcher matcher = commandHandler.getPattern().matcher(update.message().text());
                        return matcher.matches();
                    } catch (NullPointerException exception) {
                        return false;
                    }
                })
                .findAny();
            handler.ifPresentOrElse(
                commandHandler -> commandHandler.handle(update).ifPresent(telegramBot::execute),
                () -> {
                    try {
                        SendMessage msg = new SendMessage(update.message().chat().id(), "Unknown command!");
                        telegramBot.execute(msg);
                    } catch (NullPointerException ignored) {
                    }
                }
            );
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
