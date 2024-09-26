package org.example.interfaces;

import org.example.entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartProductRepository extends JpaRepository<CartProduct, Long> {
}
