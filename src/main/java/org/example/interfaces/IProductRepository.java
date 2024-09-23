package org.example.interfaces;
import org.example.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT  p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(p.category.name) IN :categories " +
            "AND LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("categories") String[] categories,
            @Param("description") String description,
            Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    Page<Product> getProducts(
            @Param("ids") Long[] ids ,Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    Set<Product> getProducts(
            @Param("ids") Long[] ids );

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);
}
