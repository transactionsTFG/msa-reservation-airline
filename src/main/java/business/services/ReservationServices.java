package business.services;

import business.dto.ReservationRequestDTO;
import business.dto.modifyreservation.UpdateResevationDTO;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;

public interface ReservationServices {
    boolean creationReservationAsync(ReservationRequestDTO request);
    ReservationDTO creationReservationSync(ReservationDTO reservation);
    ReservationDTO getReservationById(long idReservation);
    boolean modifyReservationAsync(UpdateResevationDTO request);
    boolean cancelReservationAsync(long idReservation);
    boolean updateSage(long idReservation, String sagaId);
    boolean validateSagaId(long idReservation, String sagaId);
    boolean updateOnlyReservation(ReservationDTO reservation);
    boolean updateReservationAndSaveLines(ReservationWithLinesDTO reservationWithLinesDTO);
    boolean updateReservationAndUpdateLines(ReservationWithLinesDTO reservationWithLinesDTO);
}
