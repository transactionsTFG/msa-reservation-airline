package business.servicesevent;

import javax.ejb.Stateless;
import javax.inject.Inject;

import business.dto.ReservationDTO;
import business.mapper.ReservationMapper;
import business.services.ReservationServices;
import domainevent.publisher.IJMSEventPublisher;
import msa.commons.event.EventId;

@Stateless
public class ReservationEventAdapterServices implements ReservationEventServices {
    private ReservationServices reservationServices;
    private IJMSEventPublisher jEventPublisher;
    
    @Override
    public boolean beginCreateReservationEvent(ReservationDTO reservationDTO) {
        boolean succes = this.reservationServices.beginCreationReservation(reservationDTO.getFlightInstanceSeats());
        this.jEventPublisher.publish(EventId.RESERVATION_AIRLINE_INIT_CREATE_CUSTOMER, ReservationMapper.INSTANCE.dtoToCommandCreateCustome(reservationDTO));
        return succes;
    }

    @Inject
    public void setReservationServices(ReservationServices reservationServices) {
        this.reservationServices = reservationServices;
    }

    @Inject
    public void setjEventPublisher(IJMSEventPublisher jEventPublisher) {
        this.jEventPublisher = jEventPublisher;
    }

}
