package com.naum.system.producer.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EmailService создаёт RestTemplate внутри метода, поэтому замокать HTTP через MockRestServiceServer нельзя.
 * Вместо этого поднимаем настоящий HTTP-сервер из JDK на случайном порту.
 */
class EmailServiceTest {

    private HttpServer server;
    private volatile String responseBody = "";
    private EmailService emailService;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/users", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            if (bytes.length == 0) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream body = exchange.getResponseBody()) {
                    body.write(bytes);
                }
            }
            exchange.close();
        });
        server.start();

        emailService = new EmailService();
        ReflectionTestUtils.setField(emailService, "emailResourceUrl",
                "http://localhost:" + server.getAddress().getPort() + "/users");
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void getEmails_returnsDefaultEmailAndEmailsOfAllUsers() {
        responseBody = """
                [{"id":1,"name":"Ivan","email":"ivan@test.com"},{"id":2,"name":null,"email":"petr@test.com"}]""";

        assertThat(emailService.getEmails()).containsExactly("test@test.com", "ivan@test.com", "petr@test.com");
    }

    @Test
    void getEmails_whenNoUsers_returnsOnlyDefaultEmail() {
        responseBody = "[]";

        assertThat(emailService.getEmails()).containsExactly("test@test.com");
    }

    @Test
    void getEmails_whenBodyIsEmpty_returnsOnlyDefaultEmail() {
        responseBody = "";

        assertThat(emailService.getEmails()).containsExactly("test@test.com");
    }

    @Disabled("Задача 25: JSON парсится регуляркой — любой пробел после двоеточия ломает разбор")
    @Test
    void getEmails_parsesPrettyPrintedJson() {
        responseBody = """
                [
                  { "id" : 1, "name" : "Ivan", "email" : "ivan@test.com" }
                ]""";

        assertThat(emailService.getEmails()).containsExactly("test@test.com", "ivan@test.com");
    }
}
