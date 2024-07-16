package com.realvnc.messageservice.controller;

import com.realvnc.messageservice.model.Message;
import com.realvnc.messageservice.model.Permission;
import com.realvnc.messageservice.model.TokenData;
import com.realvnc.messageservice.repository.MessageRepository;
import com.realvnc.messageservice.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.EnumSet;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class MessagesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testReadMessage() throws Exception {
        TokenData tokenData = createToken(EnumSet.of(Permission.READ_MESSAGE));
        Message message = createTestMessage(tokenData.getUserId());

        this.mockMvc
                .perform(
                        get("/messages/{messageId}", message.getId())
                                .header("Authorization", "Bearer " + tokenData.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(message.getId()))
                .andExpect(jsonPath("senderId").value(message.getSenderId()))
                .andExpect(jsonPath("subject").value(message.getSubject()))
                .andExpect(jsonPath("body").value(message.getBody()));
    }

    @Test
    public void testCreateMessage() throws Exception {
        TokenData tokenData = createToken(EnumSet.of(Permission.CREATE_MESSAGE));

        this.mockMvc
                .perform(
                        post("/messages")
                                .header("Authorization", "Bearer " + tokenData.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"subject\": \"Test message subject\", " +
                                           "\"body\": \"Test message body\" }")
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("senderId").value(tokenData.getUserId()))
                .andExpect(jsonPath("subject").value("Test message subject"))
                .andExpect(jsonPath("body").value("Test message body"));
    }

    @Test
    public void testUpdateMessage() throws Exception {
        TokenData tokenData = createToken(EnumSet.of(Permission.UPDATE_MESSAGE));
        Message message = createTestMessage(tokenData.getUserId());

        this.mockMvc
                .perform(
                        post("/messages/{messageId}", message.getId())
                                .header("Authorization", "Bearer " + tokenData.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"subject\": \"A new message subject\", " +
                                           "\"body\": \"A new message body\" }")
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("senderId").value(tokenData.getUserId()))
                .andExpect(jsonPath("subject").value("A new message subject"))
                .andExpect(jsonPath("body").value("A new message body"));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        TokenData tokenData = createToken(EnumSet.of(Permission.DELETE_MESSAGE));
        Message message = createTestMessage(tokenData.getUserId());

        this.mockMvc
                .perform(
                        delete("/messages/{messageId}", message.getId())
                                .header("Authorization", "Bearer " + tokenData.getToken())
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private TokenData createToken(EnumSet<Permission> permissions) {
        TokenData tokenData = new TokenData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), permissions);
        return tokenRepository.save(tokenData);
    }

    private Message createTestMessage(String senderId) {
        Message message = new Message(
                UUID.randomUUID().toString(), senderId, "Test message subject", "Test message body");
        return messageRepository.save(message);
    }
}
