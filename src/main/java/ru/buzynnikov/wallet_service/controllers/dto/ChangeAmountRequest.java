package ru.buzynnikov.wallet_service.controllers.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на изменение суммы на счете кошелька.
 * Включает идентификатор кошелька, тип операции и сумму изменения.
 */
public record ChangeAmountRequest(
        @NotNull(message = "Идентификатор кошелька не может быть пустым") UUID walletId,
        @NotNull(message = "Тип операции не может быть пустым") OperationType operationType,
        @DecimalMin(value = "0.01", message = "Значение должно быть выше нуля") @NotNull(message = "Сумма не может быть пустой") BigDecimal amount) {
}
