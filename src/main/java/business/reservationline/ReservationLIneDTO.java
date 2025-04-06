package business.reservationline;

import lombok.Data;

@Data
public class ReservationLIneDTO {
    private long idReeservation;
    private long fligjhtInstanceId;
    private boolean active;
    private int passengers;
    private double price;
}
