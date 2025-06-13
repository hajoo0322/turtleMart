package turtleMart.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turtleMart.member.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
