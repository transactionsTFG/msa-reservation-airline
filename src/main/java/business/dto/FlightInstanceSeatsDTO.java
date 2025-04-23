package business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightInstanceSeatsDTO {
    private long idFlightInstance;
    private int numberSeats;
}
