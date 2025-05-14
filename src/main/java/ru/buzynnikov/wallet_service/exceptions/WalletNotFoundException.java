package ru.buzynnikov.wallet_service.exceptions;

/**
 * Этот класс предназначен для случаев, когда кошелек с указанным идентификатором не найден.
 */
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
