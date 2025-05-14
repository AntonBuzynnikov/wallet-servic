package ru.buzynnikov.wallet_service.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.buzynnikov.wallet_service.models.Wallet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с моделями кошельков (Wallet).
 * Позволяет производить стандартные операции CRUD (создание, получение, обновление, удаление),
 * используя возможности инфраструктуры Spring Data JPA.
 */
@Repository
public interface WalletRepository extends CrudRepository<Wallet, UUID> {


}
