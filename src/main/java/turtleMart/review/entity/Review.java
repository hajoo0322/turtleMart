package turtleMart.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turtleMart.global.common.BaseEntity;
import turtleMart.member.entity.Member;
import turtleMart.order.entity.OrderItem;
import turtleMart.product.entity.Product;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity @Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private OrderItem orderItem;

    @OneToMany(mappedBy = "review", cascade =  CascadeType.ALL)
    List<TemplateChoice> templateChoiceList = new ArrayList<>();

    @Column(length = 20, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String content;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    private boolean isDeleted = false;

    private Review(Member member, Product product, OrderItem orderItem, String title, String content, Integer rating, String imageUrl) {
        this.member = member;
        this.product = product;
        this.orderItem = orderItem;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public static Review of(Member member, Product product, OrderItem orderItem, String title, String content, Integer rating, String imageUrl) {
        return new Review(member, product, orderItem, title, content, rating, imageUrl);
    }

    public void update(String title, String content, Integer rating, String imageUrl) {
        if(title != null && !title.isEmpty()){this.title = title;}
        if(content != null && !content.isEmpty()){this.content = content;}
        if(rating != null ){this.rating = rating;}
        if(imageUrl != null && !imageUrl.isEmpty()){this.imageUrl = imageUrl;}
    }

    public void delete(){this.isDeleted = true;}
}
