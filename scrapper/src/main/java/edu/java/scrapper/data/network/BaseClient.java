package edu.java.scrapper.data.network;

import edu.java.core.exception.LinkCannotBeHandledException;
import edu.java.scrapper.data.db.entity.Link;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseClient {
    protected final List<Pattern> handledUrlPatterns;

    protected BaseClient(List<String> urls) throws PatternSyntaxException {
        this.handledUrlPatterns = urls.stream().map(Pattern::compile).toList();
    }

    public final @NotNull Boolean canHandle(@NotNull String url) {
        return handledUrlPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(url).matches());
    }

    protected final @NotNull List<String> extractDataTokensFromLink(@NotNull String url)
            throws LinkCannotBeHandledException {
        Matcher matcher = handledUrlPatterns.stream()
                .filter(pattern -> pattern.matcher(url).matches())
                .findAny()
                .orElseThrow(() -> new LinkCannotBeHandledException(url))
                .matcher(url);
        if (!matcher.matches()) {
            throw new IllegalStateException("Dude...");
        }
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < matcher.groupCount() + 1; i++) {
            tokens.add(matcher.group(i));
        }
        return tokens;
    }

    public abstract Pair<@NotNull Link, @Nullable String> handle(Link link) throws LinkCannotBeHandledException;
}
