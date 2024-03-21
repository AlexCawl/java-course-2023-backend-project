package edu.java.core.response.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnswerResponse(
        @JsonProperty("owner")
        Owner owner,

        @JsonProperty("score")
        int score
) {
}
