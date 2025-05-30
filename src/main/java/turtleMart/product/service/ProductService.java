package turtleMart.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.global.exception.RoleMismatchException;
import turtleMart.member.entity.Seller;
import turtleMart.member.repository.SellerRepository;
import turtleMart.product.dto.request.ProductRequest;
import turtleMart.product.dto.response.ProductResponse;
import turtleMart.product.dto.response.ProductResponseForSeller;
import turtleMart.product.entity.Product;
import turtleMart.product.repository.ProductDslRepository;
import turtleMart.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final ProductDslRepository productDslRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest, Long sellerId) {
        if (!sellerRepository.existsById(sellerId)) {
            throw new NotFoundException(ErrorCode.SELLER_NOT_FOUND);
        }

        Seller seller = sellerRepository.getReferenceById(sellerId);

        Product product = Product.of(seller, productRequest.name(), productRequest.price(), productRequest.description());
        productRepository.save(product);
        return ProductResponse.from(product);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productDslRepository.findByIdWithSeller(productId);
        if (product == null) {
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return ProductResponse.from(product);
    }

    public List<ProductResponseForSeller> getProductBySellerIdWithEverything(Long sellerId) {
        List<Product> productList = productRepository.findAllBySellerId(sellerId);
        return productList.stream().map(ProductResponseForSeller::from).toList();
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long sellerId, Long productId) {
        Product product = productDslRepository.findByIdWithSeller(productId);
        if (product == null) {
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        checkPermission(sellerId, product);
        product.update(productRequest);
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
        checkPermission(sellerId,product);
        //상품에 연결된 콤비네이션은? 존재해도 괜찮지않을까? 같이 딜리트상태로 만들까?
        product.delete(true);
    }

    @Transactional
    public ProductResponse reviveProduct(Long productId, Long sellerId) {
        Product product = productDslRepository.findByIdWithSeller(productId);
        if (product == null) {
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (!product.isDeleted()) {
            throw new BadRequestException(ErrorCode.PRODUCT_ALL_READY_SURVIVE);
        }
        checkPermission(sellerId, product);
        product.delete(false);
        return ProductResponse.from(product);
    }

    private void checkPermission(Long sellerId, Product product) {
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RoleMismatchException(ErrorCode.FORBIDDEN);
        }
    }

    public List<ProductResponseForSeller> getProductBySellerId(Long sellerId) {
        List<Product> productList = productRepository.findAllBySellerIdAndIsDeletedFalse(sellerId);
        return productList.stream().map(ProductResponseForSeller::from).toList();
    }

    public List<ProductResponseForSeller> getProductBySellerIdWithDeleted(Long sellerId) {
        List<Product> productList = productRepository.findAllBySellerIdAndIsDeletedTrue(sellerId);
        return productList.stream().map(ProductResponseForSeller::from).toList();
    }
}
