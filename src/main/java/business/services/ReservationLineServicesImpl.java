package business.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.mapper.ReservationLineMapper;
import business.reservation.Reservation;
import business.reservation.ReservationDTO;
import business.reservationline.ReservationLIneDTO;
import business.reservationline.ReservationLine;
import integration.dao.reservationline.ReservationLineDAO;


@Stateless
public class ReservationLineServicesImpl implements ReservationLineServices {
    private EntityManager entityManager;
    private ReservationLineDAO reservationLineDAO;
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
    @Inject public void setReservationLineDAO(ReservationLineDAO reservationLineDAO) { this.reservationLineDAO = reservationLineDAO;}

    @Override
    public boolean existsById(List<Long> ids, long idReservation) {
        for(long idFlightInstance: ids) {
            Optional<ReservationLine> reservationLineOpt = this.reservationLineDAO.findByFlightInstanceIdAndReservationId(idFlightInstance, idReservation);
            if (reservationLineOpt.isEmpty() || !reservationLineOpt.get().isActive()) 
                return false;
        }
        return true;
    }

    @Override
    public List<ReservationLIneDTO> findByIdReservation(List<Long> idFlightInstance, long idReservation) {
        List<ReservationLIneDTO> reservationLIneDTOs = new ArrayList<>(); 
        for (long idFlightInstance1 : idFlightInstance) {
            Optional<ReservationLine> reservationLineOpt = this.reservationLineDAO.findByFlightInstanceIdAndReservationId(idFlightInstance1, idReservation);
            if (reservationLineOpt.isEmpty() || !reservationLineOpt.get().isActive()) 
                continue;
            reservationLIneDTOs.add(ReservationLineMapper.INSTANCE.entityToDto(reservationLineOpt.get()));
        }
        return reservationLIneDTOs;
    } 

    @Override
    public Map<Long, ReservationLIneDTO> findByIdReservationToMap(List<Long> idFlightInstance, long idReservation) {
        Map<Long, ReservationLIneDTO> map = new HashMap<>();
        for (long idFlightInstance1 : idFlightInstance) {
            Optional<ReservationLine> reservationLineOpt = this.reservationLineDAO.findByFlightInstanceIdAndReservationId(idFlightInstance1, idReservation);
            if (reservationLineOpt.isEmpty() || !reservationLineOpt.get().isActive()) 
                continue;
            map.put(reservationLineOpt.get().getFlightInstanceId(), ReservationLineMapper.INSTANCE.entityToDto(reservationLineOpt.get()));
        }
        return map;
    }


    @Override
    public boolean removeReservation(ReservationDTO reservation) {
        Reservation r = this.entityManager.find(Reservation.class, reservation.getId(), LockModeType.OPTIMISTIC);
        if (r == null) 
            return false;
        r.setStatusSaga(reservation.getStatusSaga());
        r.setActive(false);
        for (ReservationLine instance : r.getReservationLine()) {
            instance.setActive(false);
            this.entityManager.merge(instance);
        }
        return true;
    }

    @Override
    public Map<Long, ReservationLIneDTO> findByIdReservationToMapIgnoreActive(List<Long> idFlightInstance,
            long idReservation) {
            Map<Long, ReservationLIneDTO> map = new HashMap<>();
            for (long idFlightInstance1 : idFlightInstance) {
                Optional<ReservationLine> reservationLineOpt = this.reservationLineDAO.findByFlightInstanceIdAndReservationId(idFlightInstance1, idReservation);
                if (reservationLineOpt.isEmpty()) 
                    continue;
                map.put(reservationLineOpt.get().getFlightInstanceId(), ReservationLineMapper.INSTANCE.entityToDto(reservationLineOpt.get()));
            }
            return map;
    }
    
}
