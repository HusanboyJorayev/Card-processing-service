package org.example.card_processing_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.card_processing_service.dto.CardDto;
import org.example.card_processing_service.dto.TransactionDto;
import org.example.card_processing_service.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cards/")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody @Valid CardDto dto) {
        return ResponseEntity.ok(cardService.createCard(dto));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable("cardId") UUID cardId) {
        return ResponseEntity.ok(this.cardService.getCard(cardId));
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable UUID cardId, @RequestHeader("If-Match") String eTag) {
        if (cardService.blockCard(cardId, eTag)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<Void> unblockCard(@PathVariable UUID cardId, @RequestHeader("If-Match") String eTag) {
        if (cardService.unblockCard(cardId, eTag)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{cardId}/debit")
    public ResponseEntity<?> debit(@PathVariable("cardId") UUID cardId,
                                   @RequestBody TransactionDto dto) {
        return this.cardService.debit(cardId, dto);
    }

    @PostMapping("/{cardId}/credit")
    public ResponseEntity<?> credit(@PathVariable("cardId") UUID cardId,
                                    @RequestBody TransactionDto dto) {
        return this.cardService.credit(cardId, dto);
    }
}
