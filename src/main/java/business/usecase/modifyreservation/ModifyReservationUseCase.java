package business.usecase.modifyreservation;

import business.dto.modifyreservation.UpdateResevationDTO;
import msa.commons.utils.UseCaseResult;

public interface ModifyReservationUseCase {
    UseCaseResult<Boolean> modify(UpdateResevationDTO request);
}
