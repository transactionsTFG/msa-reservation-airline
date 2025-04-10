package business.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.google.gson.Gson;

import business.dto.ReservationRequestDTO;
import business.reservation.Reservation;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLine;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import rules.RulesBusinessCustomer;

@Stateless
public class ReservationServicesImpl implements ReservationServices {
    private EntityManager entityManager;
    private EventHandlerRegistry eventHandlerRegistry;
    private Gson gson;
    private RulesBusinessCustomer rulesBusinessCustomer;

    @Override
    public boolean creationReservationAsync(ReservationRequestDTO request) {
        if (!this.rulesBusinessCustomer.isValid(request.getCustomer())) 
            return false;
    
        this.eventHandlerRegistry.getHandler(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_BEGIN_SAGA)
                                 .handleCommand(this.gson.toJson(request));
        return true;
    }

    @Override
    public ReservationDTO creationReservationSync(ReservationDTO dto) {
        Reservation r = new Reservation();
        r.setActive(dto.isActive());
        r.setStatusSaga(dto.getStatusSaga());
        r.setCustomerId(dto.getCustomerId());
        r.setTotal(dto.getTotal());
        this.entityManager.persist(r);
        this.entityManager.flush();
        return r.toDTO();
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
            priceTotal += line.getPrice() * line.getPassengers();
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
    @Inject public void setGson(Gson gson) { this.gson = gson;  }
    @Inject public void setRulesBusinessCustomer(RulesBusinessCustomer rulesBusinessCustomer) { this.rulesBusinessCustomer = rulesBusinessCustomer; }
}
