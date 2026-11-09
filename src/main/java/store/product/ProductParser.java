package store.product;

import java.util.List;

public class ProductParser {

    public static Product to(ProductIn in) {
        return in == null ? null :
            Product.builder()
                .name(in.name())
                .price(in.price())
                .unit(in.unit())
                .build();
    }

    public static ProductOut to(Product p) {
        return p == null ? null :
            ProductOut.builder()
                .id(p.id())
                .name(p.name())
                .price(p.price())
                .unit(p.unit())
                .build();
    }

    public static List<ProductOut> to(List<Product> products) {
        return products.stream().map(ProductParser::to).toList();
    }

}
