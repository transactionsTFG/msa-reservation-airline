package business.reservation;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import msa.commons.saga.SagaPhases;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private long id;
	private LocalDateTime createdAt;
	private double total;
	private boolean active;
	private long customerId;
	private SagaPhases statusSaga;
	private String sagaId;

	public ReservationDTO(boolean active, long customerId, SagaPhases statusSaga, String sagaId) {
		this.active = active;
		this.customerId = customerId;
		this.statusSaga = statusSaga;
		this.sagaId = sagaId;
	}
}
