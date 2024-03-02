package edu.java.bot.domain.subscribe;

import com.pengrad.telegrambot.model.User;
import edu.java.bot.data.LinkTrackerRepository;
import edu.java.bot.model.LinkAlreadyTracked;
import edu.java.bot.model.UserIsNotAuthenticated;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TrackLinkUseCase {
    private final LinkTrackerRepository repository;

    public TrackLinkResponse trackLink(User user, String link) {
        try {
            repository.setLinkTracked(user.id(), link);
            return new TrackLinkResponse.Ok();
        } catch (UserIsNotAuthenticated exception) {
            return new TrackLinkResponse.UserIsNotDefined();
        } catch (LinkAlreadyTracked exception) {
            return new TrackLinkResponse.AlreadyRegistered();
        }
    }
}
