package business.dto.modifyreservation;

import java.util.List;

import business.dto.FlightInstanceSeatsDTO;
import lombok.Data;

@Data
public class UpdateResevationDTO {
    private long idReservation;
    private List<FlightInstanceSeatsDTO> flightInstanceSeats;
}
