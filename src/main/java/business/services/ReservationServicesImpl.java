package business.services;

import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.mapper.ReservationLineMapper;
import business.reservation.Reservation;
import business.reservation.ReservationDTO;
import business.reservation.ReservationWithLinesDTO;
import business.reservationline.ReservationLine;
import integration.dao.reservation.ReservationDAO;
import integration.dao.reservationline.ReservationLineDAO;

@Stateless
public class ReservationServicesImpl implements ReservationServices {
    private EntityManager entityManager;
    private ReservationLineDAO reservationLineDAO;
    private ReservationDAO reservationDAO;
    
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
    @Inject public void setReservationLineDAO(ReservationLineDAO reservationLineDAO) { this.reservationLineDAO = reservationLineDAO; }
    @Inject public void setReservationDAO(ReservationDAO reservationDAO) { this.reservationDAO = reservationDAO; }

    @Override
    public ReservationDTO creationReservation(ReservationDTO dto) {
        return this.reservationDAO.save(dto).toDTO();
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
    public boolean updateReservationAndUpdateLines(ReservationWithLinesDTO reservationWithLinesDTO) {
        Optional<Reservation> r = this.reservationDAO.findById(reservationWithLinesDTO.getReservation().getId());
        if (r.isEmpty()) 
            return false;
        r.get().setActive(reservationWithLinesDTO.getReservation().isActive());
        r.get().setStatusSaga(reservationWithLinesDTO.getReservation().getStatusSaga());
        double priceTotal = r.get().getTotal();
        for (var line : reservationWithLinesDTO.getLines()) {
            Optional<ReservationLine> reservationLineOpt = this.reservationLineDAO.findByFlightInstanceIdAndReservationId(line.getFlightInstanceId(), line.getIdReservation());
            if(reservationLineOpt.isEmpty())
                continue;
            ReservationLine rL = reservationLineOpt.get();
            rL.setActive(line.isActive());
            rL.setFlightInstanceId(line.getFlightInstanceId());
            rL.setPassengers(line.getPassengers() + rL.getPassengers());
            rL.setPrice(line.getPrice());
            priceTotal += line.getPrice() * line.getPassengers();
            this.entityManager.merge(rL);
        }
        r.get().setTotal(priceTotal);
        this.entityManager.merge(r.get());
        return true;
    }

 
    @Override
    public boolean validateSagaId(long idReservation, String sagaId) {
        Optional<Reservation> r = this.reservationDAO.findById(idReservation);
        return r.isPresent() && r.get().getSagaId() != null && r.get().getSagaId().equals(sagaId);
    }
    
    @Override
    public ReservationDTO findById(long idReservation) {
        return this.reservationDAO.findById(idReservation).map(Reservation::toDTO).orElse(null);
    }

    @Override
    public boolean updateSaga(long idReservation, String sagaId) {
        Optional<Reservation> r = this.reservationDAO.findById(idReservation);
        if (r.isEmpty()) 
            return false;
        r.get().setSagaId(sagaId);
        this.entityManager.merge(r.get());
        return true;
    }
    
    @Override
    public boolean existsById(long idReservation) {
        return this.findById(idReservation) != null;
    }

    @Override
    public boolean isActiveReservation(long idReservation) {
        ReservationDTO r = this.findById(idReservation);
        return r != null && r.isActive();
    }

    @Override
    public ReservationWithLinesDTO getReservationWithLinesById(long idReservation) {
        Optional<Reservation> r = this.reservationDAO.findById(idReservation);
        if(r.isEmpty())
            return null;
        Set<ReservationLine> rl = r.get().getReservationLine();
        return new ReservationWithLinesDTO(r.get().toDTO(), rl.stream().map(ReservationLineMapper.INSTANCE::entityToDto).toList());
    }
    @Override
    public ReservationWithLinesDTO getReservationWithLinesByIdAndActive(long idReservation) {
        Optional<Reservation> r = this.reservationDAO.findById(idReservation);
        if(r.isEmpty())
            return null;
        Set<ReservationLine> rl = r.get().getReservationLine();
        return new ReservationWithLinesDTO(r.get().toDTO(), rl.stream().filter(ReservationLine::isActive).map(ReservationLineMapper.INSTANCE::entityToDto).toList());
    }

}
