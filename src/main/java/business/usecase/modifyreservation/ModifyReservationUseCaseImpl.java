package business.usecase.modifyreservation;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.google.gson.Gson;

import business.dto.FlightInstanceSeatsDTO;
import business.dto.modifyreservation.UpdateResevationDTO;
import business.reservation.ReservationDTO;
import business.services.ReservationLineServices;
import business.services.ReservationServices;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import msa.commons.utils.UseCaseResult;

@Stateless
public class ModifyReservationUseCaseImpl implements ModifyReservationUseCase {
    private EventHandlerRegistry eventRegistry;
    private ReservationServices reservationServices;
    private ReservationLineServices reservationLineServices;
    private Gson gson;
    @EJB public void setCommandHandlerRegistry(EventHandlerRegistry eventRegistry) { this.eventRegistry = eventRegistry; }
    @Inject public void setGson(Gson gson) { this.gson = gson;  }
    @Inject public void setReservationServices(ReservationServices reservationServices) { this.reservationServices = reservationServices; }
    @Inject public void setReservationLineServices(ReservationLineServices reservationLineServices) { this.reservationLineServices = reservationLineServices; }

    @Override
    @Transactional
    public UseCaseResult<Boolean> modify(UpdateResevationDTO request) {
        ReservationDTO r = this.reservationServices.findById(request.getIdReservation());
        if (r == null) 
            return UseCaseResult.failure("Reservation not found");
        
        if(!r.isActive()) 
            return UseCaseResult.failure("Reservation is not active");

        if (request.getFlightInstanceSeats() == null || request.getFlightInstanceSeats().isEmpty()) 
            return UseCaseResult.failure("No flight instance seats provided");
        
        Map<Long, Integer> flighInstanceMap = new HashMap<>();
        for (FlightInstanceSeatsDTO fl : request.getFlightInstanceSeats()) {
            if(fl.getIdFlightInstance() <= 0) 
                return UseCaseResult.failure("Invalid flight instance ID: " + fl.getIdFlightInstance());
            if(fl.getNumberSeats() < 0) 
                return UseCaseResult.failure("Invalid number of seats: " + fl.getNumberSeats());
            
            if(!this.reservationLineServices.existsById(fl.getIdFlightInstance(), request.getIdReservation()))
                return UseCaseResult.failure(MessageFormat.format("Flight instance {0} not found in reservation: {1}", fl.getIdFlightInstance(), request.getIdReservation()));

            flighInstanceMap.merge(fl.getIdFlightInstance(), fl.getNumberSeats(), Integer::sum);
        }

        request.setFlightInstanceSeats(flighInstanceMap.entrySet().stream()
                                        .map(e -> new FlightInstanceSeatsDTO(e.getKey(), e.getValue()))
                                        .toList());
        this.eventRegistry.getHandler(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_BEGIN_SAGA)
                                 .commandPublisher(this.gson.toJson(request));
        return UseCaseResult.success(true);
    }
    
}
