package com.naum.system.producer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {

    private static final Pattern PATTERN = Pattern.compile("\"email\":\"(.*?)\"");
    private static final String DEFAULT_EMAIL = "test@test.com";

    @Value(value = "${email.resource.url}")
    private String emailResourceUrl;

    public List<String> getEmails() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(emailResourceUrl, String.class);
        String body = response.getBody();
        if (body == null) {
            return List.of(DEFAULT_EMAIL);
        }
        List<String> emails = new ArrayList<>();
        emails.add(DEFAULT_EMAIL);
        Matcher matcher = PATTERN.matcher(response.getBody());
        while (matcher.find()) {
            String email = matcher.group(1);
            emails.add(email);
        }
        return emails;
    }
}
