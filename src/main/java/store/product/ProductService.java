package store.product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(Product product) {
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is required");
        }
        if (product.name() == null || product.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (product.price() == null || product.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than zero");
        }
        if (product.unit() == null || product.unit().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit is required");
        }
        return productRepository.save(new ProductModel(product)).to();
    }

    public List<Product> findAll() {
        return productRepository.findAll().stream()
            .map(ProductModel::to)
            .toList();
    }

    public Product findById(String id) {
        return productRepository.findById(id)
            .map(ProductModel::to)
            .orElse(null);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }

}
