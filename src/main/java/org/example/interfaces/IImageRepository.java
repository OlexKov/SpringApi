package org.example.interfaces;

import org.example.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IImageRepository extends JpaRepository<ProductImage, Long> {
}
