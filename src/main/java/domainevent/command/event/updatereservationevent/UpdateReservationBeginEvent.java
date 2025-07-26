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
import msa.commons.commands.modifyreservation.UpdateReservationCommand;
import msa.commons.commands.modifyreservation.model.Action;
import msa.commons.commands.modifyreservation.model.IdUpdateFlightInstanceInfo;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

@Stateless
@UpdateReservationBeginQualifier
@Local(CommandHandler.class)
public class UpdateReservationBeginEvent extends BaseHandler  {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        final List<Long> flightInstanceIds = c.getFlightInstanceInfo().stream().map(IdUpdateFlightInstanceInfo::getIdFlightInstance).toList();
        Map<Long, ReservationLIneDTO> reservationLines = this.reservationLineServices.findByIdReservationToMapIgnoreActive(flightInstanceIds, 
                                                                                                               c.getIdReservation());
        this.reservationServices.updateSaga(c.getIdReservation(), eventData.getSagaId());
        UpdateReservationCommand command = new UpdateReservationCommand();
        command.setIdReservation(c.getIdReservation());
        for(IdUpdateFlightInstanceInfo f : c.getFlightInstanceInfo()) {
            ReservationLIneDTO r = reservationLines.get(f.getIdFlightInstance());
            if(r == null || r.getPassengers() == f.getNumberSeats())
                continue;
            f.setIdFlightInstance(r.getFlightInstanceId());
            if (f.getNumberSeats() > r.getPassengers()){
                f.setAction(Action.ADD_SEATS);
                f.setNumberSeats(f.getNumberSeats() - r.getPassengers());
            }
            else if(f.getNumberSeats() == 0) {
                f.setNumberSeats(r.getPassengers());
                f.setAction(Action.REMOVE_FLIGHT);
            }
            else if(f.getNumberSeats() < r.getPassengers()) {
                f.setNumberSeats(r.getPassengers() - f.getNumberSeats());
                f.setAction(Action.REMOVE_SEATS);
            }
        }
        this.jmsEventPublisher.publish(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_MODIFY_RESERVATION, eventData);
    }
    
}
