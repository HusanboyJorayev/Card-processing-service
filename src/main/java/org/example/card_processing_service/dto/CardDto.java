package org.example.card_processing_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.card_processing_service.role.Currency;
import org.example.card_processing_service.role.Status;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto {
    private UUID id;
    private Long userId;
    private UUID externalId;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    private Long balance = 0L;
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.UZS;
    private String eTg;

    public CardDto(UUID id, Long userId, Status status, Long balance, Currency currency) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.balance = balance;
        this.currency = currency;
    }
}
