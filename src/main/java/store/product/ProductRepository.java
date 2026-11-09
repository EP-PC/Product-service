package store.product;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductModel, String> {

    List<ProductModel> findAll();

    List<ProductModel> findByNameContainingIgnoreCase(String name);

}
