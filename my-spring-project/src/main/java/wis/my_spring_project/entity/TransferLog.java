package wis.my_spring_project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransferLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "from_location", length = 50, nullable = false)
    private String fromLocation;

    @Column(name = "to_location", length = 50, nullable = false)
    private String toLocation;

    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(name = "transferred_at", nullable = false, updatable = false)
    private LocalDateTime transferredAt;
}
