package business.reservation;

import java.util.List;

import business.reservationline.ReservationLIneDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationWithLinesDTO {
    private ReservationDTO reservation;
    private List<ReservationLIneDTO> lines;
}