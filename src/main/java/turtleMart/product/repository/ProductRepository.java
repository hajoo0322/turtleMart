package turtleMart.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turtleMart.product.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySellerId(Long sellerId);

    List<Product> findAllBySellerIdAndIsDeletedFalse(Long sellerId);

    List<Product> findAllBySellerIdAndIsDeletedTrue(Long sellerId);
}
