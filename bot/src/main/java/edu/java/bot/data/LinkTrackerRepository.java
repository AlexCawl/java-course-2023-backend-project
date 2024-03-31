package edu.java.bot.data;

import edu.java.core.exception.ApiErrorException;
import edu.java.core.response.ListLinksResponse;

public interface LinkTrackerRepository {
    ListLinksResponse getUserTrackedLinks(Long userId) throws ApiErrorException;

    void setLinkTracked(Long userId, String link) throws ApiErrorException;

    void setLinkUntracked(Long userId, String link) throws ApiErrorException;
}
