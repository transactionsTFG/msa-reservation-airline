package domainevent.command.event.removereservationevent;

import javax.ejb.Local;
import javax.ejb.Stateless;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.removereservation.command.IdWithSeats;
import msa.commons.microservices.reservationairline.removereservation.command.RemoveReservationCommand;
import msa.commons.microservices.reservationairline.removereservation.qualifier.RemoveReservationByCommitQualifier;

@Stateless
@RemoveReservationByCommitQualifier
@Local(CommandHandler.class)
public class RemoveReservationCommitEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();
        if (this.reservationServices.validateSagaId(c.getIdReservation(), eventData.getSagaId()) && this.reservationLineServices
                                                                                                            .validateSagaId(
                                                                                                                    c.getListIdFlightInstance().stream().map(IdWithSeats::getIdFlightInstance).toList(), 
                                                                                                                    c.getIdReservation(), 
                                                                                                                    eventData.getSagaId()
                                                                                                            )) 
            this.reservationLineServices.removeReservation(c.getIdReservation());
        else
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
