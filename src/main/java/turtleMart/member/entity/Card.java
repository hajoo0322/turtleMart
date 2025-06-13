package turtleMart.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turtleMart.member.dto.request.CardRegisterRequest;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;
    
    private String cardIssuer;

    private String cardNumber;

    private String expirationDate;

    private String cvcNumber;

    private String cardPassword;

    public Card(
            String cardIssuer,
            String cardNumber,
            String expirationDate,
            String cvcNumber,
            String cardPassword
    ) {
        this.cardIssuer = cardIssuer;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvcNumber = cvcNumber;
        this.cardPassword = cardPassword;
    }

    public static Card of(CardRegisterRequest request) {
        return new Card(
                request.cardIssuer(),
                request.cardNumber(),
                request.expirationDate(),
                request.cvcNumber(),
                request.cardPassword()
        );
    }
}
