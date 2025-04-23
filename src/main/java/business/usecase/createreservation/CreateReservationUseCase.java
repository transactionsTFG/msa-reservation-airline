package business.usecase.createreservation;

import business.dto.ReservationRequestDTO;
import msa.commons.utils.UseCaseResult;

public interface CreateReservationUseCase {
    UseCaseResult<Boolean> create(ReservationRequestDTO request);
}
