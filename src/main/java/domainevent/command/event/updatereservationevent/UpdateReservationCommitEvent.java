package domainevent.command.event.updatereservationevent;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLIneDTO;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.updatereservation.command.UpdateReservationCommand;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;
import msa.commons.microservices.reservationairline.updatereservation.qualifier.UpdateReservationByModifyReservationCommit;
import msa.commons.saga.SagaPhases;

@Stateless
@UpdateReservationByModifyReservationCommit
@Local(CommandHandler.class)
public class UpdateReservationCommitEvent extends BaseHandler {

    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, UpdateReservationCommand.class);
        UpdateReservationCommand c = (UpdateReservationCommand) eventData.getData();
        List<Long> listFlightInstances = c.getFlightInstanceInfo().stream().map(IdUpdateFlightInstanceInfo::getIdFlightInstance).toList();
        if (!this.reservationLineServices.validateSagaId(listFlightInstances, c.getIdReservation(), eventData.getSagaId())) {
            this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
        } else {
            List<ReservationLIneDTO> buildReservationLine = c.getFlightInstanceInfo().stream().map(info -> {
                ReservationLIneDTO l = new ReservationLIneDTO();
                l.setFlightInstanceId(info.getIdFlightInstance());
                l.setActive(true);
                l.setIdReservation(c.getIdReservation());
                l.setPassengers(info.getNumberSeats());
                l.setPrice(info.getPrice());
                return l;
            }).toList();
            ReservationDTO buildReservation = new ReservationDTO();
            buildReservation.setId(c.getIdReservation());
            buildReservation.setStatusSaga(SagaPhases.COMPLETED);
            buildReservation.setActive(true);
            ReservationWithLinesDTO reservationWithLinesDTO = ReservationWithLinesDTO.builder()
                                                                                        .reservation(buildReservation)
                                                                                        .lines(buildReservationLine)
                                                                                        .build();
            this.reservationServices.updateReservationAndUpdateLines(reservationWithLinesDTO);
        }
    }
    
}
