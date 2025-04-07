package business.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.dto.ReservationRequestDTO;
import business.mapper.ReservationMapper;
import business.reservation.Reservation;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLine;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;

@Stateless
public class ReservationServicesImpl implements ReservationServices {
    private EntityManager entityManager;
    private EventHandlerRegistry eventHandlerRegistry;

    @Override
    public boolean creationReservationAsync(ReservationRequestDTO request) {
        //TODO: Validacion negocio    
        this.eventHandlerRegistry.getHandler(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_BEGIN_SAGA)
                                 .handleCommand(request);
        return true;
    }

    @Override
    public ReservationDTO creationReservationSync(ReservationDTO dto) {
        //TODO: Validacion negocio
        Reservation r = new Reservation();
        r.setActive(dto.isActive());
        r.setStatusSaga(dto.getStatusSaga());
        r.setCustomerId(dto.getCustomerId());
        r.setTotal(dto.getTotal());
        this.entityManager.persist(r);
        this.entityManager.flush();
        return ReservationMapper.INSTANCE.entityToDto(r);
    }  

    @Override
    public boolean updateReservationAndSaveLines(ReservationWithLinesDTO reservationWithLinesDTO) {
        Reservation r = this.entityManager.find(Reservation.class, reservationWithLinesDTO.getReservation().getId(), LockModeType.OPTIMISTIC);
        r.setActive(reservationWithLinesDTO.getReservation().isActive());
        r.setCustomerId(reservationWithLinesDTO.getReservation().getCustomerId());
        r.setStatusSaga(reservationWithLinesDTO.getReservation().getStatusSaga());
        double priceTotal = 0;
        for (var line : reservationWithLinesDTO.getLines()) {
            ReservationLine reservationLine = new ReservationLine();
            reservationLine.setActive(line.isActive());
            reservationLine.setFlightInstanceId(line.getFlightInstanceId());
            reservationLine.setPassengers(line.getPassengers());
            reservationLine.setPrice(line.getPrice());
            reservationLine.setReservationId(r);
            priceTotal += line.getPrice();
            this.entityManager.merge(reservationLine);
        }
        r.setTotal(priceTotal);
        this.entityManager.merge(r);
        return true;
    }

    @Override
    public boolean removeReservation(long idReservation) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        this.entityManager.remove(r);
        return true;
    }

    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
    @EJB public void setCommandHandlerRegistry(EventHandlerRegistry eventHandlerRegistry) { this.eventHandlerRegistry = eventHandlerRegistry; }
}
