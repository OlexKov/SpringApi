package org.example.interfaces.repositories;

import org.example.entities.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISettlementRepository extends JpaRepository<Settlement, String> {
}
