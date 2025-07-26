package domainevent.command.event.removereservationevent;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.qualifier.removereservation.RemoveReservationByCommitQualifier;
import business.reservation.ReservationDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.removereservation.RemoveReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.DeleteReservation;
import msa.commons.saga.SagaPhases;

@Stateless
@RemoveReservationByCommitQualifier
@Local(CommandHandler.class)
public class RemoveReservationCommitEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand c = (RemoveReservationCommand) eventData.getData();
        if (this.reservationServices.validateSagaId(c.getIdReservation(), eventData.getSagaId())) {
            ReservationDTO reservation = this.reservationServices.findById(c.getIdReservation());
            reservation.setStatusSaga(SagaPhases.COMPLETED);
            this.reservationLineServices.removeReservation(reservation);
            eventData.setOperation(DeleteReservation.DELETE_RESERVATION_ONLY_AIRLINE_COMMIT);
            this.jmsEventPublisher.publish(EventId.REMOVE_RESERVATION_TRAVEL, eventData);
        }
        else
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
