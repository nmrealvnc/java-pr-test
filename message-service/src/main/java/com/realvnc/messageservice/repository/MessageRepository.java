package com.realvnc.messageservice.repository;

import com.realvnc.messageservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
}
