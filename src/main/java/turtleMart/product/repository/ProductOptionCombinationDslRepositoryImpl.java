package turtleMart.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import turtleMart.member.entity.QSeller;
import turtleMart.product.entity.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOptionCombinationDslRepositoryImpl implements ProductOptionCombinationDslRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductOptionCombination> findAllByProductIdWithMapAndValue(Long productId) {
        QProductOptionCombination productOptionCombination = QProductOptionCombination.productOptionCombination;
        QProductOptionMap productOptionMap = QProductOptionMap.productOptionMap;
        QProductOptionValue productOptionValue = QProductOptionValue.productOptionValue;
        return jpaQueryFactory.selectFrom(productOptionCombination)
                .join(productOptionCombination.productOptionMapList, productOptionMap).fetchJoin()
                .join(productOptionMap.productOptionValue, productOptionValue).fetchJoin()
                .where(productOptionCombination.product.id.eq(productId))
                .distinct()
                .fetch();
    }

    @Override
    public ProductOptionCombination findByIdWithProductAndSeller(Long productOptionCombinationId) {
        QProductOptionCombination productOptionCombination = QProductOptionCombination.productOptionCombination;
        QProduct product = QProduct.product;
        QSeller seller = QSeller.seller;
        return jpaQueryFactory.select(productOptionCombination)
                .from(productOptionCombination)
                .join(productOptionCombination.product,product).fetchJoin()
                .join(productOptionCombination.product.seller,seller).fetchJoin()
                .where(productOptionCombination.id.eq(productOptionCombinationId))
                .fetchOne();
    }
}
