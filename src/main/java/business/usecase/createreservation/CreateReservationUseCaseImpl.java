package business.usecase.createreservation;

import javax.ejb.Stateless;

import business.dto.ReservationRequestDTO;

@Stateless
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {



    @Override
    public boolean create(ReservationRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
}
