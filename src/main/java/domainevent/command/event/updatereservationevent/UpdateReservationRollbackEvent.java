package domainevent.command.event.updatereservationevent;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.reservationline.ReservationLIneDTO;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;
import msa.commons.microservices.reservationairline.updatereservation.qualifier.UpdateReservationByModifyReservationRollback;

@Stateless
@UpdateReservationByModifyReservationRollback
@Local(CommandHandler.class)
public class UpdateReservationRollbackEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        List<ReservationLIneDTO> reservationLines = new ArrayList<>();
        for (IdUpdateFlightInstanceInfo info : c.getFlightInstanceInfo()) {
            ReservationLIneDTO r = new ReservationLIneDTO();
            r.setIdReservation(c.getIdReservation());
            r.setFlightInstanceId(info.getIdFlightInstance());
            r.setSagaId(eventData.getSagaId());
            reservationLines.add(r);
        }
        this.reservationLineServices.updateReservationLines(reservationLines);
        this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
