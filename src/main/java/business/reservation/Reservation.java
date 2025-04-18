package business.reservation;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import business.reservationline.ReservationLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import msa.commons.saga.SagaPhases;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation", indexes = {
    @Index(name = "idx_reservation_saga_id", columnList = "saga_id")
})
public class Reservation {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
	private long id;
	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name = "created_at")
	private LocalDateTime createdAt;
    @Column(nullable = false, name = "total_price")
	private double total;
    @Column(name = "is_active", nullable = false)
	private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status_saga")
    private SagaPhases statusSaga;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    @Column(name = "customer_id", nullable = false)
    private long customerId;
    
    @OneToMany(mappedBy = "reservationId")
	private Set<ReservationLine> reservationLine;

    @Version
    @Column(name = "version")
    private int version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    public ReservationDTO toDTO() {
        return new ReservationDTO(this.id, this.createdAt, this.total, this.active, this.customerId, this.statusSaga, this.sagaId);
    }
}
