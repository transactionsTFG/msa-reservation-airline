package domainevent.command;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLIneDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationCommitQualifier;
import msa.commons.saga.SagaPhases;

@Stateless
@CreateReservationCommitQualifier
@Local(EventHandler.class)
public class CreateReservationCommmitEvent extends BaseHandler {

    @Override
    public void handleCommand(String json) {
        CreateReservationCommand c = this.gson.fromJson(json, CreateReservationCommand.class);
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
        buildReservation.setCustomerId(c.getCustomerInfo().getIdCustomer());
        buildReservation.setStatusSaga(SagaPhases.COMPLETED);
        buildReservation.setActive(true);
        ReservationWithLinesDTO reservationWithLinesDTO = ReservationWithLinesDTO.builder()
                                                                                    .reservation(buildReservation)
                                                                                    .lines(buildReservationLine)
                                                                                    .build();
        this.reservationServices.updateReservationAndSaveLines(reservationWithLinesDTO);
    }
    
}
