package com.sample.service;

import com.sample.model.Token;
import com.sample.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {

    public void save(Token token) {
        tokenRepository.save(token);
    }

    public Token getById(String id) {
        Optional<Token> token = tokenRepository.findById(id);
        return token.orElse(null);
    }

    public void delete(String id) {
        tokenRepository.deleteById(id);
    }
}
