package business.services;

import java.util.List;
import java.util.Map;

import business.reservationline.ReservationLIneDTO;

public interface ReservationLineServices  { 
    boolean existsById(List<Long> idFlightInstance, long idReservation);
    List<ReservationLIneDTO> findByIdReservation(List<Long> idFlightInstance, long idReservation);
    Map<Long, ReservationLIneDTO> findByIdReservationToMap(List<Long> idFlightInstance, long idReservation);
    void updateReservationSagaId(List<Long> idFlightInstance, long idReservation, String sagaId);
}
