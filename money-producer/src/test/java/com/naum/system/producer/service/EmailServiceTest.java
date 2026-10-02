package com.naum.system.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naum.system.producer.config.MoneyProducerConfig;
import com.naum.system.producer.config.MoneyProducerProperties;
import com.naum.system.producer.service.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(EmailService.class)
@Import({MoneyProducerConfig.class})
class EmailServiceTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MoneyProducerProperties props;

    @Test
    void getEmails_returnsDefaultEmailAndEmailsOfAllUsers() {
        List<UserResponse> userResponses = List.of(
                new UserResponse(1L, "Ivan", "ivan@test.com"),
                new UserResponse(2L, null, "petr@test.com"));
        String jsonResponse = assertDoesNotThrow(
                () -> objectMapper.writeValueAsString(userResponses));
        mockServer.expect(requestTo(props.email().url()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        assertThat(emailService.getEmails()).containsExactly("test@test.com", "ivan@test.com", "petr@test.com");
        mockServer.verify();
    }

    @Test
    void getEmails_whenNoUsers_returnsOnlyDefaultEmail() {
        String responseBody = "[]";
        mockServer.expect(requestTo(props.email().url()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertThat(emailService.getEmails()).containsExactly("test@test.com");
        mockServer.verify();
    }

    @Test
    void getEmails_whenBodyIsEmpty_returnsOnlyDefaultEmail() {
        String responseBody = "";
        mockServer.expect(requestTo(props.email().url()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertThat(emailService.getEmails()).containsExactly("test@test.com");
        mockServer.verify();
    }

    @Test
    void getEmails_parsesPrettyPrintedJson() {
        String responseBody = """
                [
                  { "id" : 1, "name" : "Ivan", "email" : "ivan@test.com" }
                ]""";
        mockServer.expect(requestTo(props.email().url()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertThat(emailService.getEmails()).containsExactly("test@test.com", "ivan@test.com");
        mockServer.verify();
    }
}
