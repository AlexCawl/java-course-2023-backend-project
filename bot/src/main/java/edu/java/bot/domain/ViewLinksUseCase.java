package edu.java.bot.domain;

import com.pengrad.telegrambot.model.User;
import edu.java.bot.data.LinkTrackerRepository;
import edu.java.bot.domain.model.ErrorTelegramResponse;
import edu.java.bot.domain.model.TelegramResponse;
import edu.java.core.exception.ApiErrorException;
import edu.java.core.response.LinkResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewLinksUseCase {
    private final LinkTrackerRepository repository;

    public TelegramResponse viewLinks(User user) {
        try {
            return new TelegramResponse(
                    String.join(
                            "\n",
                            repository.getUserTrackedLinks(user.id()).links().stream().map(LinkResponse::url).toList()
                    )
            );
        } catch (ApiErrorException exception) {
            return new ErrorTelegramResponse(exception);
        } catch (Exception exception) {
            return new TelegramResponse(String.format("Unable to connect... [%s]", exception.getClass()));
        }
    }
}
