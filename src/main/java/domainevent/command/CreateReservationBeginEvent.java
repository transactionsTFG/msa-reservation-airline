package domainevent.command;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import business.dto.ReservationRequestDTO;
import business.reservation.ReservationDTO;
import business.saga.creationreservation.mapper.CreationReservationMapper;
import business.saga.creationreservation.qualifier.CreateReservationBeginQualifier;
import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.event.EventId;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.commandevent.model.IdFlightInstanceInfo;
import msa.commons.saga.SagaPhases;

@Stateless
@CreateReservationBeginQualifier
@Local(EventHandler.class)
public class CreateReservationBeginEvent extends BaseHandler {

    @Override
    public void handleCommand(Object data) {
        ReservationRequestDTO r = (ReservationRequestDTO) data;
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setActive(false);
        reservationDTO.setCustomerId(-1);
        reservationDTO.setStatusSaga(SagaPhases.STARTED);
        reservationDTO = this.reservationServices.creationReservationSync(reservationDTO);

        List<IdFlightInstanceInfo> listFlightInfo = r.getFlightInstanceSeats().stream().map(request -> {
            IdFlightInstanceInfo flightInfo = new IdFlightInstanceInfo();
            flightInfo.setIdFlightInstance(request.getIdFlightInstance());
            flightInfo.setNumberSeats(request.getNumberSeats()); 
            flightInfo.setIdAircraft(-1); flightInfo.setTotalOccupiedSeats(0); flightInfo.setPrice(0);
            return flightInfo;
        }).toList();
        this.jmsEventPublisher.publish(EventId.CUSTOMER_AIRLINE_GET_CUSTOMER_RESERVATION_AIRLINE_CREATE_RESERVATION, CreateReservationCommand.builder()
                                                                                                                            .customerInfo(CreationReservationMapper.INSTANCE.dtoToCustomerInfo(r.getCustomer()))
                                                                                                                            .flightInstanceInfo(listFlightInfo)
                                                                                                                            .idReservation(reservationDTO.getId()));
    }
    
}
