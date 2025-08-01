package business.services;

import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;

public interface ReservationServices {
    ReservationDTO creationReservation(ReservationDTO reservation);
    ReservationDTO findById(long idReservation);
    ReservationWithLinesDTO getReservationWithLinesById(long idReservation);
    ReservationWithLinesDTO getReservationWithLinesByIdAndActive(long idReservation);
    boolean existsById(long idReservation);
    boolean isActiveReservation(long idReservation);
    boolean updateSaga(long idReservation, String sagaId);
    boolean validateSagaId(long idReservation, String sagaId);
    boolean updateOnlyReservation(ReservationDTO reservation);
    boolean updateReservationAndSaveLines(ReservationWithLinesDTO reservationWithLinesDTO);
    boolean updateReservationAndUpdateLines(ReservationWithLinesDTO reservationWithLinesDTO);
}
