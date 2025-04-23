package business.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.mapper.ReservationLineMapper;
import business.reservation.Reservation;
import business.reservationline.ReservationLIneDTO;
import business.reservationline.ReservationLine;

@Stateless
public class ReservationLineServicesImpl implements ReservationLineServices {
    private EntityManager entityManager;
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}

    @Override
    public boolean existsById(List<Long> ids, long idReservation) {
        for(long idFlightInstance: ids) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", idFlightInstance)
                .setParameter("reservationId", idReservation)
                .getResultList();
            if (reservationLine.isEmpty() || !reservationLine.get(0).isActive()) 
                return false;
        }
        return true;
    }

    @Override
    public List<ReservationLIneDTO> findByIdReservation(List<Long> idFlightInstance, long idReservation) {
        List<ReservationLIneDTO> reservationLIneDTOs = new ArrayList<>(); 
        for (long idFlightInstance1 : idFlightInstance) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", idFlightInstance1)
                .setParameter("reservationId", idReservation)
                .getResultList();
            if (reservationLine.isEmpty() || !reservationLine.get(0).isActive()) 
                continue;
            reservationLIneDTOs.add(ReservationLineMapper.INSTANCE.entityToDto(reservationLine.get(0)));
        }
        return reservationLIneDTOs;
    }

    @Override
    public Map<Long, ReservationLIneDTO> findByIdReservationToMap(List<Long> idFlightInstance, long idReservation) {
        Map<Long, ReservationLIneDTO> map = new HashMap<>();
        for (long idFlightInstance1 : idFlightInstance) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", idFlightInstance1)
                .setParameter("reservationId", idReservation)
                .getResultList();
            ReservationLine r = (reservationLine.isEmpty()) ? null : reservationLine.get(0);    
            if (r == null || !r.isActive()) 
                continue;
            map.put(r.getFlightInstanceId(), ReservationLineMapper.INSTANCE.entityToDto(r));
        }
        return map;
    }


    @Override
    public boolean removeReservation(long idReservation) {
        Reservation r = this.entityManager.find(Reservation.class, idReservation, LockModeType.OPTIMISTIC);
        if (r == null) 
            return false;
        r.setActive(false);
        for (ReservationLine instance : r.getReservationLine()) {
            instance.setActive(false);
            this.entityManager.merge(instance);
        }
        return true;
    }
    
}
