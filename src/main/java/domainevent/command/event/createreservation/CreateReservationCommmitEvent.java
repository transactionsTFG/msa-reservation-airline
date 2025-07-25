package domainevent.command.event.createreservation;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.qualifier.createreservation.CreateReservationCommitQualifier;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLIneDTO;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.createreservation.CreateReservationCommand;
import msa.commons.event.EventData;
import msa.commons.event.EventId;
import msa.commons.event.eventoperation.reservation.CreateReservation;
import msa.commons.saga.SagaPhases;

@Stateless
@CreateReservationCommitQualifier
@Local(CommandHandler.class)
public class CreateReservationCommmitEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CreateReservationCommmitEvent.class);
    @Override
    public void commandPublisher(String json) {
        LOGGER.info("***** INICIAMOS COMMIT SAGA CREACION DE RESERVA *****");
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData();
        if (!this.reservationServices.validateSagaId(c.getIdReservation(), eventData.getSagaId())) {
            this.jmsEventPublisher.publish(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, eventData);
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
            buildReservation.setCustomerId(c.getCustomerInfo().getIdCustomer());
            buildReservation.setStatusSaga(SagaPhases.COMPLETED);
            buildReservation.setActive(true);
            ReservationWithLinesDTO reservationWithLinesDTO = ReservationWithLinesDTO.builder()
                                                                                        .reservation(buildReservation)
                                                                                        .lines(buildReservationLine)
                                                                                        .build();
            this.reservationServices.updateReservationAndSaveLines(reservationWithLinesDTO);
            eventData.setOperation(CreateReservation.CREATE_RESERVATION_ONLY_AIRLINE_COMMIT);
            this.jmsEventPublisher.publish(EventId.CREATE_RESERVATION_TRAVEL, eventData);
            LOGGER.info("***** COMMIT TERMINADO CON EXITO EN SAGA CREACION DE RESERVA *****");
        }
    }
    
}
