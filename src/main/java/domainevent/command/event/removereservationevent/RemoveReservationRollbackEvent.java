package domainevent.command.event.removereservationevent;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.qualifier.removereservation.RemoveReservationByRollbackQualifier;
import business.reservation.ReservationDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.removereservation.RemoveReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.saga.SagaPhases;

@Stateless
@RemoveReservationByRollbackQualifier
@Local(CommandHandler.class)
public class RemoveReservationRollbackEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();    
        ReservationDTO buildReservation = this.reservationServices.findById(c.getIdReservation());
        buildReservation.setStatusSaga(SagaPhases.CANCELLED);
        buildReservation.setActive(true);
        this.reservationServices.updateOnlyReservation(buildReservation);
        this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
