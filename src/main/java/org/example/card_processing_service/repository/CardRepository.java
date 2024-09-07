package org.example.card_processing_service.repository;

import org.example.card_processing_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {

    @Query(value = "select count(*)\n" +
            "from card as c\n" +
            "where c.user_id =?1\n" +
            "  and c.status = 'ACTIVE'", nativeQuery = true)
    Integer countActiveCardsByUserId(Long userId);
}
