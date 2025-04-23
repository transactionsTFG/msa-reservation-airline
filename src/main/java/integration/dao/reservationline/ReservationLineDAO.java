package integration.dao.reservationline;

import java.util.Optional;

import business.reservationline.ReservationLine;

public interface ReservationLineDAO {
    Optional<ReservationLine> findByFlightInstanceIdAndReservationId(long flightInstanceId, long reservationId);
}
