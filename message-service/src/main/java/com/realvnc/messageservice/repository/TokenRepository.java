package com.realvnc.messageservice.repository;

import com.realvnc.messageservice.model.TokenData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenData, String> {

}
