package ru.protei.dayPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.protei.dayPlanner.model.PersistentToken;

@Repository
public interface TokenRepository extends JpaRepository<PersistentToken, String> {
}
