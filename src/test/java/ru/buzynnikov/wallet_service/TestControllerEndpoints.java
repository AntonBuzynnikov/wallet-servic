package ru.buzynnikov.wallet_service;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Набор тестов для проверки работоспособности конечных точек API (контроллеров).
 * Используем Autowired для внедрения экземпляра MockMvc, который позволяет эмулировать веб-запросы.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerEndpoints {
    @Autowired
    private MockMvc mockMvc;


    /**
     * Тестирует успешное пополнение баланса.
     * Отправляет запрос на изменение баланса (операция DEPOSIT) и проверяет статус ответа 204 (No Content).
     */
    @Test
    @Transactional
    void testAddMoneyAndReturnStatus204() throws Exception {

        
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b1",
                        "operationType": "DEPOSIT",
                        "amount": 500.0
                    }
                """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Тестирует успешное получение текущего баланса кошелька.
     * Отправляет GET-запрос на получение баланса и проверяет, что баланс равен 1000.
     */
    @Test
    void testGetBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/31c186cd-57fd-40e2-914d-e4b580dd89b2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1000.0));
    }

    /**
     * Тестирует процесс пополнения баланса и последующее получение обновленного баланса.
     * Сначала пополняет баланс, затем проверяет, что баланс изменился на нужное значение.
     */
    @Test
    void testAddMoneyAndCheckResult() throws Exception {
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b3",
                        "operationType": "DEPOSIT",
                        "amount": 500.0
                    }
                """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        Thread.sleep(500);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/31c186cd-57fd-40e2-914d-e4b580dd89b3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1500.0));
    }

    /**
     * Тестирует процедуру снятия денег и последующую проверку баланса.
     * Сначала снимает деньги, затем проверяет, что баланс уменьшился на правильную величину.
     */
    @Test
    void testSubtractMoneyAndCheckResult() throws Exception {
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b4",
                        "operationType": "WITHDRAW",
                        "amount": 500.0
                    }
                """;

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Thread.sleep(500);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/31c186cd-57fd-40e2-914d-e4b580dd89b4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(500.0));
    }

    /**
     * Тестирует сценарий, когда кошелек не найден при попытке изменить баланс.
     * Проверяет, что сервер возвращает код 404 и соответствующее сообщение об ошибке.
     */
    @Test
    void testNotFoundWalletInChangeBalance() throws Exception {
        String request = """
                {
                    "walletId": "31c186cd-57fd-40e2-914d-e4b580dd0000",
                    "operationType": "WITHDRAW",
                    "amount": 500.0
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Кошелёк с id 31c186cd-57fd-40e2-914d-e4b580dd0000 не найден"));
    }

    /**
     * Тестирует сценарий, когда кошелек не найден при попытке получить баланс.
     * Проверяет, что сервер возвращает код 404 и соответствующее сообщение об ошибке.
     */
    @Test
    void testNotFoundWalletInGetBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/31c186cd-57fd-40e2-914d-e4b580dd0000"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                        .value("Кошелёк с id 31c186cd-57fd-40e2-914d-e4b580dd0000 не найден"));
    }

    /**
     * Тестирует сценарий, когда отправляется запрос с недопустимым типом операции.
     * Проверяет, что сервер возвращает код 400 и соответствующее сообщение об ошибке.
     */
    @Test
    void testBadRequestForOperationType() throws Exception {
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b5",
                        "operationType": "INVALID_TYPE",
                        "amount": 1000
                    }
                    """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Не корректный тип операции"));
    }

    /**
     * Тестирует сценарий, когда отправляются некорректные данные (пустой идентификатор кошелька и нулевая сумма).
     * Проверяет, что сервер возвращает код 400 и список ошибок.
     */
    @Test
    void testBadRequestForAmountAndId() throws Exception {
        String request = """
                    {
                        "walletId": "",
                        "operationType": "DEPOSIT",
                        "amount": null
                    }
                    """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(2));
    }

    /**
     * Тестирует попытку снять больше денег, чем имеется на балансе.
     * Проверяет, что сервер возвращает код 400 и соответствующее сообщение об ошибке.
     */
    @Test
    void testNotEnoughMoneyToWithdraw() throws Exception {
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b7",
                        "operationType": "WITHDRAW",
                        "amount": 1500.0
                    }
                    """;
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Недостаточно средств на балансе"));
    }


}
