package com.naum.system.producer.service;

import com.naum.system.producer.service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private static final String DEFAULT_EMAIL = "test@test.com";
    private final RestClient moneyServiceClient;

    public List<String> getEmails() {
        try {
            List<UserResponse> users = moneyServiceClient.get()
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return Stream.concat(Stream.of(DEFAULT_EMAIL),
                            users == null ? Stream.empty() : users.stream().map(UserResponse::email))
                    .toList();
        } catch (RestClientException e) {
            log.warn("money-service unavailable, using default email", e);
            return List.of(DEFAULT_EMAIL);
        }
    }
}
