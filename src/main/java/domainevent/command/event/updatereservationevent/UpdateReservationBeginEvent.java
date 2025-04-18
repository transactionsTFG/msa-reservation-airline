package domainevent.command.event.updatereservationevent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.FlightInstanceSeatsDTO;
import business.dto.modifyreservation.UpdateResevationDTO;
import business.reservationline.ReservationLIneDTO;

import business.saga.updatereservation.qualifier.UpdateReservationBeginQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;
import msa.commons.microservices.reservationairline.updatereservation.model.Action;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;

@Stateless
@UpdateReservationBeginQualifier
@Local(CommandHandler.class)
public class UpdateReservationBeginEvent extends BaseHandler  {

    @Override
    public void commandPublisher(String json) {
        UpdateResevationDTO u = this.gson.fromJson(json, UpdateResevationDTO.class);
        final List<Long> flightInstanceIds = u.getFlightInstanceSeats().stream().map(FlightInstanceSeatsDTO::getIdFlightInstance).toList();
        Map<Long, ReservationLIneDTO> reservationLines = this.reservationLineServices.findByIdReservationToMap(flightInstanceIds, 
                                                                                                               u.getIdReservation());
        final String sagaId = UUID.randomUUID().toString();
        this.reservationLineServices.updateReservationSagaId(flightInstanceIds, u.getIdReservation(), sagaId);
        UpdateReservationCommand command = new UpdateReservationCommand();
        command.setIdReservation(u.getIdReservation());
        List<IdUpdateFlightInstanceInfo> flightInfo = new ArrayList<>();
        for(FlightInstanceSeatsDTO f : u.getFlightInstanceSeats()) {
            ReservationLIneDTO r = reservationLines.get(f.getIdFlightInstance());
            if(r == null)
                continue;
            IdUpdateFlightInstanceInfo id = new IdUpdateFlightInstanceInfo();
            id.setIdFlightInstance(r.getFlightInstanceId());
            id.setNumberSeats(f.getNumberSeats());
            if (f.getNumberSeats() > r.getPassengers()) 
                id.setAction(Action.ADD_SEATS);
            else if(f.getNumberSeats() == 0) 
                id.setAction(Action.REMOVE_FLIGHT);
            else if(f.getNumberSeats() < r.getPassengers()) 
                id.setAction(Action.REMOVE_SEATS);
        }
        command.setFlightInstanceInfo(flightInfo);
        this.jmsEventPublisher.publish(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_MODIFY_RESERVATION, new EventData(sagaId, List.of(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA), command));
    }
    
}
