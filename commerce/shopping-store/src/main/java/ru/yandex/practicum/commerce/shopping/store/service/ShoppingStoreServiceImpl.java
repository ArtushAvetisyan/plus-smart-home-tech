package ru.yandex.practicum.commerce.shopping.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductDto;
import ru.yandex.practicum.commerce.interaction.dto.store.ProductState;
import ru.yandex.practicum.commerce.interaction.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.shopping.store.entity.Product;
import ru.yandex.practicum.commerce.interaction.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shopping.store.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = productRepository.findByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable);
        return products.map(productMapper::toProductDto);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.getProductId() == null) {
            throw new ProductNotFoundException("Ошибка во время обновления продукта (отсутствует ID)",
                    "Невозможно обновить продукт (отсутствует ID)");
        }

        Product existingProduct = getProductOrThrow(productDto.getProductId());

        productMapper.updateProductFromDto(productDto, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toProductDto(updatedProduct);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product existingProduct = getProductOrThrow(productId);
        existingProduct.setProductState(ProductState.DEACTIVATE);
        productRepository.save(existingProduct);
        return true;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product existingProduct = getProductOrThrow(request.getProductId());
        existingProduct.setQuantityState(request.getQuantityState());
        productRepository.save(existingProduct);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = getProductOrThrow(productId);
        return productMapper.toProductDto(product);
    }

    private Product getProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Продукт с id - " + productId + " не найден",
                        "Продукт с указанным ID не найден"));
    }
}