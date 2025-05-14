package ru.buzynnikov.wallet_service.exceptions;

/**
 * Исключение возникает, когда на кошельке недостаточно средств для выполнения требуемой операции.
 */
public class NotEnoughMoneyException extends RuntimeException {
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
