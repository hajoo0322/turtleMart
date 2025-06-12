package turtleMart.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "product_option_combination",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","unique_key"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOptionCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer price;

    private Integer inventory;

    private String uniqueKey;


    @Enumerated(EnumType.STRING)
    private CombinationStatus combinationStatus;

    @OneToMany(mappedBy = "productOptionCombination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionMap> productOptionMapList = new ArrayList<>();

    private ProductOptionCombination(Product product, Integer price, Integer inventory,String uniqueKey) {
        this.product = product;
        this.price = price;
        this.inventory = inventory;
        this.uniqueKey = uniqueKey;
        this.combinationStatus = CombinationStatus.READY;
    }

    public static ProductOptionCombination of(Product product, Integer price, Integer inventory,String uniqueKey) {
        return new ProductOptionCombination(product, price, inventory,uniqueKey);
    }

    public void addOptionMap(ProductOptionMap productOptionMap) {
        this.productOptionMapList.add(productOptionMap);
    }

    public void updateStatus(CombinationStatus combinationStatus) {
        this.combinationStatus = combinationStatus;
    }

    public void decreaseInventory(Integer quantity) {
        this.inventory -= quantity;
    }

    public void increaseInventory(Integer quantity) {
        this.inventory += quantity;
    }

    public void updatePrice(Integer newPrice) {
        this.price = newPrice;
    }
}
