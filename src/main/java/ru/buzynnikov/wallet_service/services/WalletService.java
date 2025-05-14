package ru.buzynnikov.wallet_service.services;

import ru.buzynnikov.wallet_service.controllers.dto.BalanceOfWalletResponse;
import ru.buzynnikov.wallet_service.controllers.dto.ChangeAmountRequest;

import java.util.UUID;

/**
 * Интерфейс для работы с сервисами кошельков.
 * Служит основой для реализации основного функционала, связанного с изменением баланса и просмотром текущего баланса кошелька.
 */
public interface WalletService {

    /**
     * Принимает данные для дальнейшей обработки
     *
     * @param request Объект запроса, содержащий информацию о типе операции (пополнение или списание) и сумме изменения.
     */
    void addDataToChangeBalance(ChangeAmountRequest request);

    /**
     * Возвращает текущий баланс определенного кошелька.
     *
     * @param walletId Уникальный идентификатор кошелька, для которого нужно получить баланс.
     * @return Объект, содержащий текущий баланс кошелька.
     */
    BalanceOfWalletResponse getBalanceOfWallet(UUID walletId);

}
