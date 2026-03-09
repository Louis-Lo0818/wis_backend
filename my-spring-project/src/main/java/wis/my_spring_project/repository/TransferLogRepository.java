package wis.my_spring_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wis.my_spring_project.entity.TransferLog;

import java.util.List;

@Repository
public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {

    List<TransferLog> findByProductId(Long productId);
}
