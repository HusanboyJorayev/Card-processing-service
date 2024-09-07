package org.example.card_processing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.card_processing_service.role.Currency;
import org.example.card_processing_service.role.Purpose;
import org.example.card_processing_service.role.TransactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardAndTransaction {
    private String cardId;
    private String transactionId;
    private String externalId;
    private Long afterBalance;
    private Long amount;
    private Currency currency;
    private Purpose purpose;
    private Long exchangeRate;
    private TransactionType type;
}
