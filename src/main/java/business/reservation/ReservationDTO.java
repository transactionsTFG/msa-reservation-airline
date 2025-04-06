package business.reservation;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReservationDTO {
    private long id;
	private LocalDateTime createdAt;
	private double total;
	private boolean active;
}
