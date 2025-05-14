package ru.buzynnikov.wallet_service.controllers.exceptionsHandlers;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;


import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.buzynnikov.wallet_service.exceptions.NotEnoughMoneyException;
import ru.buzynnikov.wallet_service.exceptions.WalletNotFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ситуацию, когда запрашиваемый кошелёк не найден.
     * Возвращает статус NOT FOUND (404).
     */
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ProblemDetail> response(WalletNotFoundException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Обрабатывает ошибку чтения JSON-данных, отправленных клиентом.
     * Возвращает статус BAD REQUEST (400).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> incorrectTypeOfOperation(HttpMessageNotReadableException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Не корректный тип операции");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Обрабатывает ситуации, когда запрос пользователя нарушает требования валидации.
     * Возвращает статус BAD REQUEST (400) с указанием конкретных нарушений.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> incorrectRequest(MethodArgumentNotValidException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Не корректный запрос");
        problemDetail.setProperty("errors",exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList());
        System.out.println(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Обрабатывает случаи нехватки средств на счёте.
     * Возвращает статус BAD REQUEST (400).
     */
    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<ProblemDetail> notEnoughMoney(NotEnoughMoneyException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Недостаточно средств на балансе");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

}
