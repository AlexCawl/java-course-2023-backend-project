package edu.java.bot.domain;

import com.pengrad.telegrambot.model.User;
import edu.java.bot.data.UserAuthRepository;
import edu.java.bot.domain.model.ErrorTelegramResponse;
import edu.java.bot.domain.model.TelegramResponse;
import edu.java.core.exception.ApiErrorException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RegisterUserUseCase {
    private final UserAuthRepository repository;

    public TelegramResponse registerUser(User user) {
        try {
            repository.registerUser(user.id());
            return new TelegramResponse("Registration successful");
        } catch (ApiErrorException exception) {
            return new ErrorTelegramResponse(exception);
        } catch (Exception exception) {
            return new TelegramResponse(String.format("Unable to connect... [%s]", exception.getClass()));
        }
    }
}
