package org.myshop.order;

import org.apache.commons.lang3.StringUtils;
import org.myshop.exception.ValidationException;

public class OrderLine {
    private final Product product;
    private final Integer quantity;

    public OrderLine(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void validate() throws ValidationException {
        if (this.product == null || StringUtils.isBlank(this.product.getId())) {
            throw new ValidationException("Product ID is missing.");
        }
        if (this.quantity == null) {
            throw new ValidationException("Quantity is missing.");
        }
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
