package turtleMart.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turtleMart.member.entity.BankAccount;

public interface AccountRepository extends JpaRepository<BankAccount, Long> {
}
