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
import ru.buzynnikov.wallet_service.repositories.WalletRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerEndpoints {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository repository;

    @Test
    void testAddMoneyAndReturnStatus204() throws Exception {

        
        String request = """
                    {
                        "walletId": "31c186cd-57fd-40e2-914d-e4b580dd89b1",
                        "operationType": "DEPOSIT",
                        "amount": 500.0
                    }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/add-money")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
