package business.usecase.createreservation;

import business.dto.ReservationRequestDTO;

public interface CreateReservationUseCase {
    boolean create(ReservationRequestDTO request);
}
