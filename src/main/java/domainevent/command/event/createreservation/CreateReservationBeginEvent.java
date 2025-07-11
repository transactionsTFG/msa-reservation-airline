package domainevent.command.event.createreservation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.reservation.ReservationDTO;
import business.saga.creationreservation.qualifier.CreateReservationBeginQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.commands.createreservation.CreateReservationCommand;
import msa.commons.commands.createreservation.model.IdFlightInstanceInfo;
import msa.commons.event.EventData;
import msa.commons.event.EventId;

import msa.commons.saga.SagaPhases;

@Stateless
@CreateReservationBeginQualifier
@Local(CommandHandler.class)
public class CreateReservationBeginEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CreateReservationBeginEvent.class);
    @Override
    public void commandPublisher(String json) {
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData(); 
        ReservationDTO reservationDTO = new ReservationDTO(false, -1, SagaPhases.STARTED, eventData.getSagaId());
        reservationDTO = this.reservationServices.creationReservation(reservationDTO);
        
        Map<Long, Integer> grouped = c.getFlightInstanceInfo().stream()
                                    .collect(Collectors.toMap(
                                        IdFlightInstanceInfo::getIdFlightInstance,
                                        IdFlightInstanceInfo::getNumberSeats,
                                        Integer::sum
                                    ));
        
        List<IdFlightInstanceInfo> listFlightInfo = grouped.entrySet().stream().map(entry -> {
            IdFlightInstanceInfo flightInfo = new IdFlightInstanceInfo();
            flightInfo.setIdFlightInstance(entry.getKey());
            flightInfo.setNumberSeats(entry.getValue()); 
            flightInfo.setIdAircraft(-1); flightInfo.setTotalOccupiedSeats(0); flightInfo.setPrice(0);
            return flightInfo;
        }).toList();
        LOGGER.info("***** INICIAMOS SAGA CREACION DE RESERVA {} *****", eventData.getSagaId());
        EventData eventDataBuild = new EventData(eventData.getSagaId(), 
                                            List.of(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA), 
                                            CreateReservationCommand.builder()
                                                                        .customerInfo(c.getCustomerInfo())
                                                                        .flightInstanceInfo(listFlightInfo)
                                                                        .idReservation(reservationDTO.getId())
                                                                        .build());
        this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_GET_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION, eventDataBuild);
    }
    
}
