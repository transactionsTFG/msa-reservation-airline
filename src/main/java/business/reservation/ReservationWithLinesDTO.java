package business.reservation;

import java.util.List;

import business.reservationline.ReservationLIneDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationWithLinesDTO {
    private ReservationDTO reservation;
    private List<ReservationLIneDTO> lines;
}