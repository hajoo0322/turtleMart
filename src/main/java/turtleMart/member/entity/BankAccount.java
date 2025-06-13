package turtleMart.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turtleMart.member.dto.request.AccountRegisterRequest;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String bankName;

    private String accountNumber;

    private String accountPassword;

    public BankAccount(String bankName, String accountNumber, String accountPassword) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountPassword = accountPassword;
    }

    public static BankAccount of(AccountRegisterRequest request) {
        return new BankAccount(
                request.bankName(),
                request.accountNumber(),
                request.accountPassword()
        );
    }
}
