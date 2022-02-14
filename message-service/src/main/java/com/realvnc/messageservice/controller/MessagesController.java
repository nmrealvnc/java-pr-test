package com.realvnc.messageservice.controller;

import com.realvnc.messageservice.exception.NotFoundException;
import com.realvnc.messageservice.exception.UnauthorizedException;
import com.realvnc.messageservice.model.Message;
import com.realvnc.messageservice.model.Permission;
import com.realvnc.messageservice.model.TokenData;
import com.realvnc.messageservice.repository.MessageRepository;
import com.realvnc.messageservice.repository.TokenRepository;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.realvnc.messageservice.model.Permission.*;

@Slf4j
@RestController
@RequestMapping("/messages")
@Transactional
public class MessagesController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping("/{messageId}")
    public Message readMessage(@PathVariable String messageId) {
        checkPermission(READ_MESSAGE);
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NotFoundException("Message " + messageId + " not found");
        }
        return message;
    }

    @Value
    static class MessageRequest {
        String subject;
        String body;
    }

    @PostMapping
    public Message createMessage(@RequestBody MessageRequest body) {
        String userId = checkPermission(CREATE_MESSAGE);
        Message message = new Message(
                UUID.randomUUID().toString(), userId, body.getSubject(), body.getBody());
        messageRepository.save(message);
        return message;
    }

    @PostMapping("/{messageId}")
    public Message updateMessage(@PathVariable String messageId, @RequestBody MessageRequest body) {
        String userId = checkPermission(UPDATE_MESSAGE);
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NotFoundException("Message " + messageId + " not found");
        }
        if (message.getSenderId() != userId) {
            throw new UnauthorizedException("User cannot edit another user's message");
        }
        if (body.getSubject() != null) {
            message.setSubject(body.getSubject());
        }
        if (body.getBody() != null) {
            message.setBody(body.getBody());
        }
        return message;
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable String messageId) {
        String userId = checkPermission(DELETE_MESSAGE);
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NotFoundException("Message " + messageId + " not found");
        }
        if (message.getSenderId() != userId) {
            throw new UnauthorizedException("User cannot delete another user's message");
        }
        messageRepository.delete(message);
    }

    private String checkPermission(Permission permission) {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Bearer token required");
        }
        String token = authorizationHeader.substring("Bearer ".length());
        TokenData tokenData = tokenRepository.findById(token).orElse(null);
        if (tokenData == null) {
            log.error("Token not found");
            return null;
        }
        if (!tokenData.getPermissions().contains(permission)) {
            throw new UnauthorizedException("User does not have permission to call this endpoint");
        }
        return tokenData.getUserId();
    }
}
