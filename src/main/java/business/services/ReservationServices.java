package business.services;

import java.util.List;

import business.dto.FlightInstanceSeatsDTO;

public interface ReservationServices {
    boolean beginCreationReservation(List<FlightInstanceSeatsDTO> flightInstanceSeatsDTO);
}
