package turtleMart.member.dto.response;

import turtleMart.member.entity.Card;

public record CardResponse(
        Long cardId,
        String cardIssuer,
        String cardNumber
) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getCardIssuer(),
                card.getCardNumber()
        );
    }
}
