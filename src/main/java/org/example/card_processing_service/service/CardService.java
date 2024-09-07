package org.example.card_processing_service.service;

import org.example.card_processing_service.dto.CardDto;
import org.example.card_processing_service.dto.TransactionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface CardService {
    ResponseEntity<?> createCard(CardDto dto);

    ResponseEntity<?> getCard(UUID cardId);

    ResponseEntity<?> debit(UUID cardId, TransactionDto dto);

    ResponseEntity<?> credit(UUID cardId, TransactionDto dto);

    boolean blockCard(UUID cardId, String eTag);

    boolean unblockCard(UUID cardId, String eTag);
}
