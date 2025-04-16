package business.reservationline;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import business.reservation.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ReservationLineId.class)
@Table(name = "reservation_line")
@NamedQuery(name = "ReservationLine.findByFlightInstanceIdAndReservationId",
            query = "SELECT rl FROM ReservationLine rl WHERE rl.flightInstanceId = :flightInstanceId AND rl.reservationId.id = :reservationId")
public class ReservationLine {
    
    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservationId;

    @Id
    @Column(nullable = false, name = "flightinstance_id")
    private long flightInstanceId;

    @Column(nullable = false, name = "is_active")
    private boolean active;

    @Column(nullable = false, name = "passenger_count")
    private int passengers;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;
    
    @Column(nullable = false, name = "price")
    private double price;

    @Version
    @Column(name = "version")
    private int version;
}
