package business.reservationline;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationLineId implements Serializable {
    private Long reservationId;
    @Column(name = "flightinstance_id", insertable = false, updatable = false)
    private Long flightInstanceId;
}
