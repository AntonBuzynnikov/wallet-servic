package ru.buzynnikov.wallet_service.services;

import org.springframework.stereotype.Service;
import ru.buzynnikov.wallet_service.controllers.dto.BalanceOfWalletResponse;
import ru.buzynnikov.wallet_service.controllers.dto.ChangeAmountRequest;

import ru.buzynnikov.wallet_service.controllers.dto.OperationType;
import ru.buzynnikov.wallet_service.exceptions.NotEnoughMoneyException;
import ru.buzynnikov.wallet_service.exceptions.WalletNotFoundException;
import ru.buzynnikov.wallet_service.models.Wallet;
import ru.buzynnikov.wallet_service.repositories.WalletRepository;


import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * Сервис для управления операциями с кошельками пользователей.
 *
 * Реализует интерфейс {@link WalletService}, предоставляя функциональность для добавления новых операций изменения баланса,
 * получения текущего баланса и безопасного выполнения изменений в многопоточном режиме.
 */
@Service
public class DefaultWalletService implements WalletService{


    private final WalletRepository walletRepository;

    /**
     * Мапа, содержащая очереди ожидающих операций изменения баланса для каждого кошелька.
     * Используются потокобезопасные коллекции для поддержки одновременного доступа из различных потоков.
     */
    private final Map<UUID, BlockingQueue<ChangeAmountRequest>> queueMap;

    private final ExecutorService executorService;


    public DefaultWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
        this.queueMap = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Добавляет новую операцию изменения баланса в очередь соответствующих действий для заданного кошелька.
     *
     * @param request объект, содержащий данные о запрашиваемой операции изменения баланса.
     * @throws NotEnoughMoneyException если сумма операции превышает доступный остаток на счету.
     */
    @Override
    public void addDataToChangeBalance(ChangeAmountRequest request){
        Wallet wallet = getWallet(request.walletId());
        if((wallet.getBalance().compareTo(request.amount()) < 0) && request.operationType().equals(OperationType.WITHDRAW)){
            throw new NotEnoughMoneyException("Недостаточно средств на балансе.");
        }
        BlockingQueue<ChangeAmountRequest> queue = queueMap.computeIfAbsent(request.walletId(),key -> {
            BlockingQueue<ChangeAmountRequest> q = new LinkedBlockingQueue<>();
            startConsumer(q);
            return q;
        });
        queue.add(request);
    }

    /**
     * Инициирует отдельный поток для обработки всех поступающих операций из очереди.
     *
     * @param queue очередь, предназначенная для обработки операций изменения баланса.
     */
    private void startConsumer(BlockingQueue<ChangeAmountRequest> queue){
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()){
                ChangeAmountRequest request = queue.poll();
                if(request != null) executeBalanceChange(request);
            }
        });
    }

    /**
     * Возвращает текущий баланс для указанного кошелька.
     *
     * @param walletId уникальный идентификатор кошелька.
     * @return объект {@link BalanceOfWalletResponse}, содержащий текущий баланс.
     */
    @Override
    public BalanceOfWalletResponse getBalanceOfWallet(UUID walletId) {
        return new BalanceOfWalletResponse(getWallet(walletId).getBalance());
    }

    /**
     * Возвращает объект кошелька по его уникальному идентификатору.
     *
     * @param walletId уникальный идентификатор кошелька.
     * @return объект {@link Wallet}, соответствующий данному идентификатору.
     * @throws WalletNotFoundException если указанный кошелек не найден.
     */
    private Wallet getWallet(UUID walletId) {
        return walletRepository.findById(walletId).orElseThrow(
                () -> new WalletNotFoundException("Кошелёк с id " + walletId + " не найден"));
    }


    /**
     * Производит изменение баланса в соответствии с переданной операцией.
     *
     * @param request объект, содержащий сведения о необходимой операции изменения баланса.
     */
    private void executeBalanceChange(ChangeAmountRequest request) {
        Wallet wallet = getWallet(request.walletId());
        BigDecimal currentBalance = wallet.getBalance();
        BigDecimal requestedAmount = request.amount();

        switch (request.operationType()) {
            case DEPOSIT:
                wallet.setBalance(currentBalance.add(requestedAmount));
                break;
            case WITHDRAW:
                if (currentBalance.compareTo(requestedAmount) >= 0) {
                    wallet.setBalance(currentBalance.subtract(requestedAmount));
                } else {
                    throw new NotEnoughMoneyException("Недостаточно средств на балансе.");
                }
                break;
            default:
                throw new IllegalArgumentException("Неподдержанный тип операции.");
        }

        walletRepository.save(wallet);
    }


}
