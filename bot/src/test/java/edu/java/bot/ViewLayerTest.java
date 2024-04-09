package edu.java.bot;

import edu.java.bot.view.telegram.command.CommandHandler;
import java.util.List;
import java.util.regex.Matcher;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ViewLayerTest {
    @Autowired
    List<CommandHandler> commandHandlers;

    @Test
    public void testCommandsSize() {
        Assertions.assertTrue(commandHandlers.size() >= 5);
    }

    @Test
    public void testCommandsAvailable() {
        List<String> commands = List.of("/hello", "/help", "/list", "/track", "/untrack", "/start");
        Assertions.assertAll(
            commands.stream().map(s -> () -> Assertions.assertNotNull(commandHandlers.stream()
                .filter(commandHandler -> {
                    try {
                        Matcher matcher = commandHandler.getPattern().matcher(s);
                        return matcher.matches();
                    } catch (NullPointerException exception) {
                        return false;
                    }
                })
                .findAny()))
        );
    }
}
