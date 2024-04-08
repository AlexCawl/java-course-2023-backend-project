package edu.java.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.core.request.LinkUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
public class RateLimiterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity}")
    private Integer rateLimit;

    @Test
    public void assertThatRateLimiterWorksRight() throws Exception {
        final LinkUpdateRequest updateRequest =
                new LinkUpdateRequest(1L, "https://link.ru", "update", List.of());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest));

        for (int i = 0; i < rateLimit; i++) {
            mockMvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }
}
