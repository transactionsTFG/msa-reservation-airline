package business.servicesevent;

import business.dto.ReservationDTO;

public interface ReservationEventServices {
    boolean beginCreateReservationEvent(ReservationDTO reservationDTO);
}