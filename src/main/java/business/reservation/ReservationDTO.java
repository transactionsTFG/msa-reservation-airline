package business.reservation;

import java.time.LocalDateTime;

import lombok.Data;
import msa.commons.saga.SagaPhases;

@Data
public class ReservationDTO {
    private long id;
	private LocalDateTime createdAt;
	private double total;
	private boolean active;
	private long customerId;
	private SagaPhases statusSaga;
}
