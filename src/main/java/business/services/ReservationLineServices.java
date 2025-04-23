package business.services;

import java.util.List;
import java.util.Map;

import business.reservation.ReservationDTO;
import business.reservationline.ReservationLIneDTO;

public interface ReservationLineServices  {
    ReservationLIneDTO findById(long idFlightInstance, long idReservation);
    boolean existsById(long idFlightInstance, long idReservation);
    boolean existsById(List<Long> idFlightInstance, long idReservation);
    List<ReservationLIneDTO> findByIdReservation(List<Long> idFlightInstance, long idReservation);
    Map<Long, ReservationLIneDTO> findByIdReservationToMap(List<Long> idFlightInstance, long idReservation);
    Map<Long, ReservationLIneDTO> findByIdReservationToMapIgnoreActive(List<Long> idFlightInstance, long idReservation);
    boolean removeReservation(ReservationDTO reservationDTO);
}
