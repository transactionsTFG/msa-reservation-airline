package business.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReservationDTO {
    private CustomerDTO customer;
    private List<FlightInstanceSeatsDTO> flightInstanceSeats;
}
