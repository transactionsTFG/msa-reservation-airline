package domainevent.command.event.createreservation;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.FlightInstanceSeatsDTO;
import business.dto.ReservationRequestDTO;
import business.reservation.ReservationDTO;
import business.saga.creationreservation.mapper.CreationReservationMapper;
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
        ReservationRequestDTO r = this.gson.fromJson(json, ReservationRequestDTO.class);
        final String sagaId = UUID.randomUUID().toString(); 
        ReservationDTO reservationDTO = new ReservationDTO(false, -1, SagaPhases.STARTED, sagaId);
        reservationDTO = this.reservationServices.creationReservation(reservationDTO);
        
        Map<Long, Integer> grouped = r.getFlightInstanceSeats().stream()
                                    .collect(Collectors.toMap(
                                        FlightInstanceSeatsDTO::getIdFlightInstance,
                                        FlightInstanceSeatsDTO::getNumberSeats,
                                        Integer::sum
                                    ));
        
        List<IdFlightInstanceInfo> listFlightInfo = grouped.entrySet().stream().map(entry -> {
            IdFlightInstanceInfo flightInfo = new IdFlightInstanceInfo();
            flightInfo.setIdFlightInstance(entry.getKey());
            flightInfo.setNumberSeats(entry.getValue()); 
            flightInfo.setIdAircraft(-1); flightInfo.setTotalOccupiedSeats(0); flightInfo.setPrice(0);
            return flightInfo;
        }).toList();
        LOGGER.info("***** INICIAMOS SAGA CREACION DE RESERVA {} *****", sagaId);
        EventData eventData = new EventData(sagaId, 
                                            List.of(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA), 
                                            CreateReservationCommand.builder()
                                                                        .customerInfo(CreationReservationMapper.INSTANCE.dtoToCustomerInfo(r.getCustomer()))
                                                                        .flightInstanceInfo(listFlightInfo)
                                                                        .idReservation(reservationDTO.getId())
                                                                        .build());
        this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_GET_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION, eventData);
    }
    
}
