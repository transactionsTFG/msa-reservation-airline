package domainevent.command.event.updatereservationevent;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.qualifier.modifyreservation.UpdateReservationByModifyReservationCommit;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLIneDTO;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.modifyreservation.UpdateReservationCommand;
import msa.commons.commands.modifyreservation.model.Action;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.UpdateReservation;
import msa.commons.saga.SagaPhases;

@Stateless
@UpdateReservationByModifyReservationCommit
@Local(CommandHandler.class)
public class UpdateReservationCommitEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        if (!this.reservationServices.validateSagaId(c.getIdReservation(), eventData.getSagaId())) {
            this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
        } else {
            List<ReservationLIneDTO> buildReservationLine = c.getFlightInstanceInfo().stream().map(info -> {
                ReservationLIneDTO l = new ReservationLIneDTO();
                l.setFlightInstanceId(info.getIdFlightInstance());
                l.setActive(!info.getAction().equals(Action.REMOVE_FLIGHT));
                l.setIdReservation(c.getIdReservation());
                l.setPassengers(info.getAction().equals(Action.ADD_SEATS) ? info.getNumberSeats() :  -info.getNumberSeats());
                l.setPrice(info.getPrice());
                return l;
            }).toList();
            ReservationDTO buildReservation = this.reservationServices.findById(c.getIdReservation());
            buildReservation.setStatusSaga(SagaPhases.COMPLETED);
            buildReservation.setActive(true);
            ReservationWithLinesDTO reservationWithLinesDTO = ReservationWithLinesDTO.builder()
                                                                                        .reservation(buildReservation)
                                                                                        .lines(buildReservationLine)
                                                                                        .build();
            this.reservationServices.updateReservationAndUpdateLines(reservationWithLinesDTO);
            eventData.setOperation(UpdateReservation.UPDATE_RESERVATION_ONLY_AIRLINE_COMMIT);
            this.jmsEventPublisher.publish(EventId.UPDATE_RESERVATION_TRAVEL, eventData);
        }
    }
    
}
