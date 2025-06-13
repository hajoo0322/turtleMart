package turtleMart.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.member.dto.request.CardRegisterRequest;
import turtleMart.member.dto.response.CardResponse;
import turtleMart.member.entity.Card;
import turtleMart.member.repository.CardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    private final CardRepository cardRepository;

    public CardResponse cardRegister(CardRegisterRequest request) {
        Card card = Card.of(request);
        cardRepository.save(card);
        return CardResponse.from(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> findMyCardList() {
        List<Card> cardList = cardRepository.findAll();
        if (cardList.isEmpty()) {
            throw new NotFoundException(ErrorCode.CARD_NOT_REGISTER);
        }
        return cardList.stream().map(CardResponse::from).toList();
    }

    public String deleteMyCard(Long cardId) {
        Card foundCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CARD_NOT_REGISTER));
        cardRepository.delete(foundCard);
        return "카드가 삭제되었습니다.";
    }
}
