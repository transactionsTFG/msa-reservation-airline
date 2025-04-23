package integration.dao.reservation;

import java.util.Optional;

import business.reservation.Reservation;
import business.reservation.ReservationDTO;

public interface ReservationDAO {
    Reservation save(ReservationDTO reservationDTO);
    Optional<Reservation> findById(long id);
}
