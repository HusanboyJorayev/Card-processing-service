package org.example.card_processing_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.example.card_processing_service.dto.CardDto;
import org.example.card_processing_service.dto.TransactionDto;
import org.example.card_processing_service.entity.Card;
import org.example.card_processing_service.entity.Transaction;
import org.example.card_processing_service.repository.CardRepository;
import org.example.card_processing_service.repository.TransactionRepository;
import org.example.card_processing_service.role.Status;
import org.example.card_processing_service.role.TransactionType;
import org.example.card_processing_service.service.CBUService;
import org.example.card_processing_service.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CBUService cbuService;

    @Override
    public ResponseEntity<?> createCard(CardDto dto) {
        Integer i = this.cardRepository.countActiveCardsByUserId(dto.getUserId());
        if (i >= 3) {
            throw new RuntimeException("You have enough active cards");
        }
        Card card = Card.builder()
                .userId(dto.getUserId())
                .balance(dto.getBalance())
                .status(dto.getStatus())
                .currency(dto.getCurrency())
                .build();
        this.cardRepository.save(card);
        CardDto response = new CardDto(card.getId(), card.getUserId(), card.getStatus(), card.getBalance(), card.getCurrency());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getCard(UUID cardId) {
        Optional<Card> optional = this.cardRepository.findById(cardId);
        if (optional.isPresent()) {
            Card card = optional.get();
            CardDto response = new CardDto(card.getId(), card.getUserId(), card.getStatus(), card.getBalance(), card.getCurrency());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok("CARD IS NOT FOUND");
    }


    @Override
    public boolean blockCard(UUID cardId, String eTag) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            if (Status.ACTIVE.equals(card.getStatus()) && eTag.equals(card.getETg())) {
                card.setStatus(Status.BLOCKED);
                cardRepository.save(card);
                return true;
            }
        }
        return false;
    }

    public boolean unblockCard(UUID cardId, String eTag) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            if (Status.BLOCKED.equals(card.getStatus()) && eTag.equals(card.getETg())) {
                card.setStatus(Status.ACTIVE);
                cardRepository.save(card);
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<?> debit(UUID cardId, TransactionDto dto) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();

            if (dto.getAmount() == null || dto.getAmount() <= 0) {
                throw new IllegalArgumentException("Invalid transaction amount");
            }

            BigDecimal transactionAmount = BigDecimal.valueOf(dto.getAmount());

            if (!dto.getCurrency().equals(card.getCurrency())) {
                transactionAmount = cbuService.convertAmount(transactionAmount, String.valueOf(dto.getCurrency()), String.valueOf(card.getCurrency()));
            }

            BigDecimal balance = BigDecimal.valueOf(card.getBalance());
            if (balance.compareTo(transactionAmount) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }

            card.setBalance(balance.subtract(transactionAmount).longValue());
            cardRepository.save(card);

            dto.setCardId(cardId);
            dto.setAmount(transactionAmount.longValue());
            Transaction transaction = Transaction.builder()
                    .cardId(dto.getCardId())
                    .amount(dto.getAmount())
                    .currency(dto.getCurrency())
                    .externalId(dto.getExternalId())
                    .purpose(dto.getPurpose())
                    .amount(dto.getAmount())
                    .type(TransactionType.DEBIT)
                    .purpose(dto.getPurpose())
                    .build();
            transactionRepository.save(transaction);

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok("CARD IS NOT FOUND");
    }

    @Override
    public ResponseEntity<?> credit(UUID cardId, TransactionDto dto) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);
        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();

            if (dto.getAmount() == null || dto.getAmount() <= 0) {
                throw new IllegalArgumentException("Invalid transaction amount");
            }
            BigDecimal transactionAmount = BigDecimal.valueOf(dto.getAmount());

            if (!dto.getCurrency().equals(card.getCurrency())) {
                transactionAmount = cbuService.convertAmount(transactionAmount, String.valueOf(dto.getCurrency()), String.valueOf(card.getCurrency()));
            }

            BigDecimal newBalance = BigDecimal.valueOf(card.getBalance()).add(transactionAmount);
            card.setBalance(newBalance.longValue());
            cardRepository.save(card);

            dto.setCardId(cardId);
            dto.setAmount(transactionAmount.longValue());
            Transaction transaction = Transaction.builder()
                    .cardId(dto.getCardId())
                    .amount(dto.getAmount())
                    .currency(dto.getCurrency())
                    .externalId(dto.getExternalId())
                    .purpose(dto.getPurpose())
                    .amount(dto.getAmount())
                    .type(TransactionType.CREDIT)
                    .purpose(dto.getPurpose())
                    .build();
            transactionRepository.save(transaction);

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.ok("CARD IS NOT FOUND");
    }
}
