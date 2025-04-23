package domainevent.command.event.updatereservationevent;


import java.util.ArrayList;
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
import msa.commons.microservices.reservationairline.updatereservation.model.Action;
import msa.commons.microservices.reservationairline.updatereservation.model.IdUpdateFlightInstanceInfo;
import msa.commons.microservices.reservationairline.updatereservation.qualifier.UpdateReservationByModifyReservationRollback;
import msa.commons.saga.SagaPhases;

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
            reservationLines.add(r);
        }
        List<ReservationLIneDTO> buildReservationLine = c.getFlightInstanceInfo().stream().map(info -> {
            ReservationLIneDTO l = new ReservationLIneDTO();
            l.setFlightInstanceId(info.getIdFlightInstance());
            l.setActive(true);
            l.setIdReservation(c.getIdReservation());
            l.setPassengers(info.getAction().equals(Action.ADD_SEATS) ? info.getNumberSeats() :  -info.getNumberSeats());
            l.setPrice(info.getPrice());
            return l;
        }).toList();
        ReservationDTO buildReservation = this.reservationServices.getReservationById(c.getIdReservation());
        buildReservation.setStatusSaga(SagaPhases.COMPLETED);
        ReservationWithLinesDTO reservationWithLinesDTO = ReservationWithLinesDTO.builder()
                                                                                    .reservation(buildReservation)
                                                                                    .lines(buildReservationLine)
                                                                                    .build();
        this.reservationServices.updateReservationAndSaveLines(reservationWithLinesDTO);
        this.jmsEventPublisher.publish(EventId.FLIGHT_UPDATE_FLIGHT_BY_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, eventData);
    }
    
}
