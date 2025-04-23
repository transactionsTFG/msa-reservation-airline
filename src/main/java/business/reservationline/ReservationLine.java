package business.reservationline;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
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
@Table(name = "reservation_line")
@NamedQuery(name = "ReservationLine.findByFlightInstanceIdAndReservationId",
            query = "SELECT rl FROM ReservationLine rl WHERE rl.flightInstanceId = :flightInstanceId AND rl.reservationId.id = :reservationId")
public class ReservationLine {
    
    @EmbeddedId
    private ReservationLineId id;

    @ManyToOne(optional = false)
    @MapsId("reservationId")
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservationId;

    @Column(nullable = false, name = "flightinstance_id")
    @MapsId("flightInstanceId") 
    private long flightInstanceId;

    @Column(nullable = false, name = "is_active")
    private boolean active;

    @Column(nullable = false, name = "passenger_count")
    private int passengers;
    
    @Column(nullable = false, name = "price")
    private double price;

    @Version
    @Column(name = "version")
    private int version;
}
