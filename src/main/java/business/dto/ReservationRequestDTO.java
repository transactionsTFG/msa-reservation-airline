package business.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReservationRequestDTO {
    private CustomerDTO customer;
    private List<FlightInstanceSeatsDTO> flightInstanceSeats;
}
