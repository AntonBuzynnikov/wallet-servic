package ru.buzynnikov.wallet_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.buzynnikov.wallet_service.controllers.dto.BalanceOfWalletResponse;
import ru.buzynnikov.wallet_service.controllers.dto.ChangeAmountRequest;
import ru.buzynnikov.wallet_service.services.WalletService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Контроллер для управления кошельком пользователя.
 * Предоставляет методы для изменения баланса и получения текущего баланса.
 */
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Метод для изменения баланса кошелька пользователя.<br/>
     * Выполняет операцию увеличения или уменьшения баланса согласно переданному запросу.<br/>
     * Если операция выполнена успешно, возвращает HTTP-код 204 No Content.<br/>
     * Параметры операции проверяются на корректность с помощью аннотации {@code @Valid}.<br/>
     * Ошибочные запросы вернут HTTP-код 400 Bad Request.
     *
     * @param request объект запроса, содержащий необходимую информацию для изменения баланса.
     * @return {@code ResponseEntity<Void>}, содержащий HTTP-код 204 No Content при успешной операции.
     */
    @PatchMapping
    public ResponseEntity<Void> changeBalance(@Valid @RequestBody ChangeAmountRequest request){
        walletService.addDataToChangeBalance(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Метод для получения текущего баланса указанного кошелька.<br/>
     * Возврат осуществляется в формате JSON с объектом {@code BalanceOfWalletResponse}.<br/>
     * При удачном выполнении возвращает HTTP-код 200 OK с результатом в теле ответа.<br/>
     * Если указанный кошелек не существует, возможна дополнительная обработка исключений (например, HTTP-код 404 Not Found).
     *
     * @param walletId уникальный идентификатор кошелька, чей баланс требуется получить.
     * @return {@code ResponseEntity<BalanceOfWalletResponse>}, содержащий объект с информацией о балансе и HTTP-код 200 OK.
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<BalanceOfWalletResponse> getBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getBalanceOfWallet(walletId));
    }
}
