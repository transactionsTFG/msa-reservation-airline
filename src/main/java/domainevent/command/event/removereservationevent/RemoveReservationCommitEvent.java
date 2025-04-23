package domainevent.command.event.removereservationevent;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.reservation.ReservationDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.removereservation.command.RemoveReservationCommand;
import msa.commons.microservices.reservationairline.removereservation.qualifier.RemoveReservationByCommitQualifier;
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
        }
        else
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
