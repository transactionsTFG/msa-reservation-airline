package business.usecase.cancelreservation;

import msa.commons.utils.UseCaseResult;

public interface CancelReservationUseCase {
   UseCaseResult<Boolean> cancel(long reservationId); 
}
