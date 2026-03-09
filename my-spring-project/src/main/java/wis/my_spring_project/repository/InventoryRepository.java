package wis.my_spring_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wis.my_spring_project.entity.Inventory;
import wis.my_spring_project.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductCode(String code);

    Optional<Inventory> findByProductAndLocation(Product product, String location);

    @Query("SELECT DISTINCT i.location FROM Inventory i")
    List<String> findDistinctLocations();

    @Query("SELECT i FROM Inventory i JOIN FETCH i.product")
    List<Inventory> findAllWithProduct();

    @Query("SELECT i FROM Inventory i JOIN FETCH i.product WHERE i.product.code = :code")
    List<Inventory> findByProductCodeWithProduct(@Param("code") String code);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i")
    Integer sumTotalQuantity();
}
