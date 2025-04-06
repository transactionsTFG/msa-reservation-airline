package business.reservationline;

import java.io.Serializable;

public class ReservationLineId implements Serializable {
    private Long reservationId;
    private Long flightInstanceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationLineId)) return false;
        ReservationLineId that = (ReservationLineId) o;
        return reservationId.equals(that.reservationId) &&
            flightInstanceId.equals(that.flightInstanceId);
    }

    @Override
    public int hashCode() {
        return reservationId.hashCode() + flightInstanceId.hashCode();
    }
}
