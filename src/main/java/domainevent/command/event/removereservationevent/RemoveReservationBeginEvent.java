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
        EventData eventData = EventData.fromJson(json, RemoveReservationCommand.class);
        RemoveReservationCommand removeReservationCommand = (RemoveReservationCommand) eventData.getData();
        ReservationWithLinesDTO reservationWithLinesDTO = this.reservationServices.getReservationWithLinesById(removeReservationCommand.getIdReservation());
        reservationWithLinesDTO.getReservation().setStatusSaga(SagaPhases.STARTED);
        reservationWithLinesDTO.getReservation().setSagaId(eventData.getSagaId());
        this.reservationServices.updateSaga(reservationWithLinesDTO.getReservation().getId(), eventData.getSagaId());
        final List<Long> flightInstanceIds = reservationWithLinesDTO.getLines().stream().map(ReservationLIneDTO::getFlightInstanceId).toList();
        this.reservationServices.updateOnlyReservation(reservationWithLinesDTO.getReservation());
        List<ReservationLIneDTO> reservationLines = this.reservationLineServices.findByIdReservation(flightInstanceIds, reservationWithLinesDTO.getReservation().getId());
        removeReservationCommand.setListIdFlightInstance(reservationLines.stream().map(rL -> new IdWithSeats(rL.getFlightInstanceId(), rL.getPassengers())).toList());
        this.jmsEventPublisher.publish(EventId.FLIGHT_VALIDATE_FLIGHT_RESERVATION_AIRLINE_REMOVE_RESERVATION, eventData);
    }
    
}
