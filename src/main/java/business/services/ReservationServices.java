package business.services;

import business.dto.ReservationRequestDTO;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;

public interface ReservationServices {
    boolean creationReservationAsync(ReservationRequestDTO request);
    ReservationDTO creationReservationSync(ReservationDTO reservation);
    boolean validateSagaId(long idReservation, String sagaId);
    boolean updateOnlyReservation(ReservationDTO reservation);
    boolean updateReservationAndSaveLines(ReservationWithLinesDTO reservationWithLinesDTO);
}
