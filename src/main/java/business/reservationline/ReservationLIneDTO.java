package business.reservationline;

import lombok.Data;

@Data
public class ReservationLIneDTO {
    private long idReservation;
    private long flightInstanceId;
    private boolean active;
    private int passengers;
    private double price;
}
