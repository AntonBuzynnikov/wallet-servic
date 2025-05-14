package ru.buzynnikov.wallet_service.controllers.dto;

/**
 * Определяет типы операций, допустимых для изменений состояния счета.
 * Поддерживает следующие операции:
 * <ul>
 *     <li>{@link #DEPOSIT}: Пополнение счета.</li>
 *     <li>{@link #WITHDRAW}: Списание денежных средств со счета.</li>
 * </ul>
 */
public enum OperationType {
    DEPOSIT,
    WITHDRAW
}
