package business.services;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import com.google.gson.Gson;

import business.dto.ReservationRequestDTO;
import business.dto.modifyreservation.UpdateResevationDTO;
import business.mapper.ReservationLineMapper;
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

    
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
    @EJB public void setCommandHandlerRegistry(EventHandlerRegistry eventHandlerRegistry) { this.eventHandlerRegistry = eventHandlerRegistry; }
    @Inject public void setGson(Gson gson) { this.gson = gson;  }
    @Inject public void setRulesBusinessCustomer(RulesBusinessCustomer rulesBusinessCustomer) { this.rulesBusinessCustomer = rulesBusinessCustomer; }


    @Override
    public boolean creationReservationAsync(ReservationRequestDTO request) {
        if (!this.rulesBusinessCustomer.isValid(request.getCustomer())) 
            return false;
    
        this.eventHandlerRegistry.getHandler(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_BEGIN_SAGA)
                                 .commandPublisher(this.gson.toJson(request));
        return true;
    }

    @Override
    public ReservationDTO creationReservationSync(ReservationDTO dto) {
        Reservation r = new Reservation();
        r.setActive(dto.isActive());
        r.setStatusSaga(dto.getStatusSaga());
        r.setCustomerId(dto.getCustomerId());
        r.setTotal(dto.getTotal());
        r.setSagaId(dto.getSagaId());
        this.entityManager.persist(r);
        this.entityManager.flush();
        return r.toDTO();
    }  

    @Override
    public boolean updateOnlyReservation(ReservationDTO reservation) {
        Reservation r = this.entityManager.find(Reservation.class, reservation.getId(), LockModeType.OPTIMISTIC);
        r.setActive(reservation.isActive());
        r.setCustomerId(reservation.getCustomerId());
        r.setStatusSaga(reservation.getStatusSaga());
        this.entityManager.merge(r);
        return true;
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
    public boolean modifyReservationAsync(UpdateResevationDTO request) {
        Reservation r = this.entityManager.find(Reservation.class, request.getIdReservation(), LockModeType.OPTIMISTIC);
        if (r == null) 
            return false;
        this.eventHandlerRegistry.getHandler(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_BEGIN_SAGA)
                                 .commandPublisher(this.gson.toJson(request));
        return true;
    }
    
    @Override
    public boolean validateSagaId(long idReservation, String sagaId) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        return r != null && r.getSagaId() != null && r.getSagaId().equals(sagaId);
    }


    @Override
    public boolean updateReservationAndUpdateLines(ReservationWithLinesDTO reservationWithLinesDTO) {
        Reservation r = this.entityManager.find(Reservation.class, reservationWithLinesDTO.getReservation().getId(), LockModeType.OPTIMISTIC);
        r.setActive(reservationWithLinesDTO.getReservation().isActive());
        r.setStatusSaga(reservationWithLinesDTO.getReservation().getStatusSaga());
        double priceTotal = r.getTotal();
        for (var line : reservationWithLinesDTO.getLines()) {
             List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", line.getFlightInstanceId())
                .setParameter("reservationId", line.getIdReservation())
                .getResultList();
            ReservationLine rL = (reservationLine.isEmpty()) ? null : reservationLine.get(0);    
            if(rL == null)
                continue;
            rL.setActive(line.isActive());
            rL.setFlightInstanceId(line.getFlightInstanceId());
            rL.setPassengers(line.getPassengers() + rL.getPassengers());
            rL.setPrice(line.getPrice());
            rL.setReservationId(r);
            priceTotal += line.getPrice() * line.getPassengers();
            this.entityManager.merge(rL);
        }
        r.setTotal(priceTotal);
        this.entityManager.merge(r);
        return true;
    }

    @Override
    public boolean cancelReservationAsync(long idReservation) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        if (r == null || !r.isActive()) 
            return false;
        Set<ReservationLine> rl = r.getReservationLine();
        ReservationWithLinesDTO rlDto = new ReservationWithLinesDTO(r.toDTO(), rl.stream().map(l -> ReservationLineMapper.INSTANCE.entityToDto(l)).toList());
        this.eventHandlerRegistry.getHandler(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_BEGIN_SAGA)
                                 .commandPublisher(this.gson.toJson(rlDto));
        return true;
    }
    @Override
    public ReservationDTO getReservationById(long idReservation) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        if (r == null)
            return null;
        return r.toDTO();
    }
    @Override
    public boolean updateSage(long idReservation, String sagaId) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        if (r == null) 
            return false;
        r.setSagaId(sagaId);
        this.entityManager.merge(r);
        return true;
    }
}
