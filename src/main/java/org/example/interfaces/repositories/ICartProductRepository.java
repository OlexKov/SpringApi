package org.example.interfaces.repositories;

import org.example.entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartProductRepository extends JpaRepository<CartProduct, Long> {
}
