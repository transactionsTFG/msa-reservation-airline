package business.usecase.cancelreservation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.google.gson.Gson;

import business.reservation.ReservationWithLinesDTO;
import business.services.ReservationServices;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import msa.commons.utils.UseCaseResult;

@Stateless
public class CancelReservationUseCaseImpl implements CancelReservationUseCase {
    private EventHandlerRegistry eventRegistry;
    private ReservationServices reservationServices;
    private Gson gson;

    @Override
    @Transactional
    public UseCaseResult<Boolean> cancel(long reservationId) {
        if(!this.reservationServices.isActiveReservation(reservationId))
            return UseCaseResult.failure("Reservation not found or already canceled");

        ReservationWithLinesDTO reservation = this.reservationServices.getReservationWithLinesById(reservationId);
        this.eventRegistry.getHandler(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_BEGIN_SAGA)
                                 .commandPublisher(this.gson.toJson(reservation));
        return UseCaseResult.success(true);
    }
    
    @EJB public void setCommandHandlerRegistry(EventHandlerRegistry eventRegistry) { this.eventRegistry = eventRegistry; }
    @Inject public void setGson(Gson gson) { this.gson = gson;  }
    @Inject public void setReservationServices(ReservationServices reservationServices) { this.reservationServices = reservationServices; }
}
