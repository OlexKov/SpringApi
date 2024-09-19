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

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.category " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(p.category.name) IN :categories " +
            "AND LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("categories") String[] categories,
            @Param("description") String description,
            Pageable pageable);

    @EntityGraph(attributePaths = {"category", "images"})
    Page<Product> findAll(Pageable pageable);
}
