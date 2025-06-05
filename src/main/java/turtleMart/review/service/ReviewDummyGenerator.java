package turtleMart.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.member.entity.Member;
import turtleMart.member.repository.MemberRepository;
import turtleMart.order.entity.Order;
import turtleMart.order.entity.OrderItem;
import turtleMart.order.repository.OrderItemRepository;
import turtleMart.order.repository.OrderRepository;
import turtleMart.product.entity.Product;
import turtleMart.product.entity.ProductOptionCombination;
import turtleMart.product.repository.ProductOptionCombinationRepository;
import turtleMart.review.entity.Review;
import turtleMart.review.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ReviewDummyGenerator {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
//    private final Faker faker = new Faker(new Locale("ko")); // 한글 Faker
    public static Random random = new Random();

    private final String[] titles = {
            "완전 만족해요", "배송 빠르고 좋아요", "별로였어요", "재구매 의사 있습니다", "가격 대비 괜찮아요",
            "기대 이상이에요", "딱 보통", "품질이 너무 별로", "아주 만족합니다", "실망이에요"
    };


    private final String[] templates = {
            "배송이 %s고 품질이 %s네요. %s!",
            "포장도 %s고 제품이 %s합니다. %s.",
            "%s 가격에 %s 퀄리티. %s.",
            "%s해서 쓰기 좋아요. 특히 %s 기능이 마음에 들어요.",
            "생각보다 %s네요. %s.",
            "%s %s %s 다시 살래요."
    };

    private final String[] words1 = {"빠르", "느리", "좋", "별로", "맛", "향", "양", "크기", "실용"};
    private final String[] words2 = {"좋", "별로", "괜찮", "훌륭", "나쁨", "애매함", "그다지", "최악", "만족"};
    private final String[] endings = {"재구매 의사 있어요", "추천합니다", "별로에요", "또 이용할게요", "다신 안살래요"};
    private final Integer[] ratingList = {1,2,3,4,5};

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void generate(int count, int productCount) {


        Member member = memberRepository.getReferenceById(1L); // 동일한 회원 사용
        List<OrderItem> orderItems = orderItemRepository.findAll(); // 여러 주문상품 가져오기

        List<Review> reviews = new ArrayList<>();

        int reviewCount = Math.min(count, orderItems.size()); // 최대 count개 또는 주문상품 수만큼

        for (int i = 0; i < reviewCount; i++) {
            OrderItem orderItem = orderItems.get(i);
            Product product = orderItem.getProductOptionCombination().getProduct(); // 주문상품에서 상품 참조

            Review review = Review.of(
                    member,
                    product,
                    orderItem,
                    pick(titles),
                    makeContent(),
                    pick(ratingList),
                    ""
            );

            reviews.add(review);
        }

        reviewRepository.saveAll(reviews);
    }

    private String makeContent() {
        String template = templates[random.nextInt(templates.length)];
        return String.format(template,
                pick(words1),
                pick(words2),
                pick(endings)
        );
    }

    public static <T> T pick(T[] arr) {
        return arr[random.nextInt(arr.length)];
    }

}
@Component
@RequiredArgsConstructor
class OrderItemGenerator{

    private final OrderRepository orderRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final OrderItemRepository orderItemRepository;




   Integer[] priceList = {325810, 45244, 149020, 272389, 394886, 111188, 339242, 132583, 479384, 125717,
                    215254, 45626, 270666, 153739, 184857, 467939, 464812, 271899, 320346, 182237,
                    306586, 352124, 482582, 303800, 208792, 213661, 476202, 165980, 327369, 200849};

String[]itemName = {
        "슈퍼 가방", "스마트 모니터", "프리미엄 모니터", "스마트 스니커즈", "에코 노트북",
            "클래식 모니터", "스마트 핸드폰", "에코 시계", "클래식 노트북", "뉴 모니터",
            "클래식 책상", "에코 책상", "프리미엄 핸드폰", "에코 핸드폰", "에센셜 책상",
            "클래식 이어폰", "슈퍼 핸드폰", "프리미엄 가방", "프리미엄 이어폰", "프리미엄 스니커즈",
            "슈퍼 이어폰", "뉴 이어폰", "에코 책상", "프리미엄 책상", "뉴 노트북",
            "라이트 이어폰", "뉴 선풍기", "프리미엄 노트북", "슈퍼 핸드폰", "슈퍼 의자",
            "스마트 가방", "스마트 노트북", "클래식 이어폰", "클래식 핸드폰", "슈퍼 책상",
            "프리미엄 선풍기", "프리미엄 이어폰", "라이트 가방", "프리미엄 가방", "라이트 이어폰",
            "라이트 시계", "스마트 이어폰", "스마트 스니커즈", "라이트 책상", "프리미엄 책상",
            "뉴 이어폰", "에코 이어폰", "스마트 가방", "슈퍼 이어폰", "뉴 스니커즈"
};

    Integer[] quantity = {1,5,6,2,22,4,3,14,8};




    public void generatorOrderItem(int repeat){

        Order order = orderRepository.getReferenceById(1L);
        ProductOptionCombination productOptionCombination = productOptionCombinationRepository.getReferenceById(1L);
        ArrayList<OrderItem> orderItems = new ArrayList<>();

        for(int i = 0 ; i < repeat ; i++){
            OrderItem  orderItem =  OrderItem.of(order, productOptionCombination, ReviewDummyGenerator.pick(priceList),
                    ReviewDummyGenerator.pick(itemName) ,  ReviewDummyGenerator.pick(quantity));

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
    }
}
