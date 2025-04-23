package business.services;

import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;

public interface ReservationServices {
    ReservationDTO findById(long idReservation);
    boolean existsById(long idReservation);
    boolean isActiveReservation(long idReservation);
    ReservationWithLinesDTO getReservationWithLinesById(long idReservation);
    ReservationDTO creationReservation(ReservationDTO reservation);
    boolean updateSage(long idReservation, String sagaId);
    boolean validateSagaId(long idReservation, String sagaId);
    boolean updateOnlyReservation(ReservationDTO reservation);
    boolean updateReservationAndSaveLines(ReservationWithLinesDTO reservationWithLinesDTO);
    boolean updateReservationAndUpdateLines(ReservationWithLinesDTO reservationWithLinesDTO);
}
