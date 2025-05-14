package ru.buzynnikov.wallet_service.controllers.dto;

import java.math.BigDecimal;

/**
 * Представляет баланс кошелька в виде BigDecimal.
 */
public record BalanceOfWalletResponse(BigDecimal balance) {
}
