package domainevent.command.event.removereservationevent;

import java.util.List;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLIneDTO;
import business.saga.deletereservation.qualifier.RemoveReservationBeginQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.removereservation.IdWithSeats;
import msa.commons.commands.removereservation.RemoveReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

import msa.commons.saga.SagaPhases;


@Stateless
@RemoveReservationBeginQualifier
@Local(CommandHandler.class)
public class RemoveReservationBeginEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        ReservationWithLinesDTO reservationWithLinesDTO = this.gson.fromJson(json, ReservationWithLinesDTO.class);
        final String sagaId = UUID.randomUUID().toString();
        reservationWithLinesDTO.getReservation().setStatusSaga(SagaPhases.STARTED);
        reservationWithLinesDTO.getReservation().setSagaId(sagaId);
        this.reservationServices.updateSaga(reservationWithLinesDTO.getReservation().getId(), sagaId);
        final List<Long> flightInstanceIds = reservationWithLinesDTO.getLines().stream().map(ReservationLIneDTO::getFlightInstanceId).toList();
        this.reservationServices.updateOnlyReservation(reservationWithLinesDTO.getReservation());
        RemoveReservationCommand removeReservationCommand = new RemoveReservationCommand();
        removeReservationCommand.setIdReservation(reservationWithLinesDTO.getReservation().getId());
        List<ReservationLIneDTO> reservationLines = this.reservationLineServices.findByIdReservation(flightInstanceIds, reservationWithLinesDTO.getReservation().getId());
        removeReservationCommand.setListIdFlightInstance(reservationLines.stream().map(rL -> new IdWithSeats(rL.getFlightInstanceId(), rL.getPassengers())).toList());
        this.jmsEventPublisher.publish(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_REMOVE_RESERVATION, new EventData(sagaId,  List.of(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA), removeReservationCommand));
    }
    
}
