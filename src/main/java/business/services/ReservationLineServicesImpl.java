package business.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.RuntimeErrorException;
import javax.persistence.EntityManager;

import business.mapper.ReservationLineMapper;
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
    public void updateReservationSagaId(List<Long> idFlightInstance, long idReservation, String sagaId) {
        for (long idFlightInstance1 : idFlightInstance) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", idFlightInstance1)
                .setParameter("reservationId", idReservation)
                .getResultList();
            ReservationLine r = (reservationLine.isEmpty()) ? null : reservationLine.get(0);    
            if (r == null || !r.isActive()) 
                throw new RuntimeException("ReservationLine not found or inactive for idFlightInstance: " + idFlightInstance1 + " and idReservation: " + idReservation);
            r.setSagaId(sagaId);
        }
    }

    @Override
    public boolean validateSagaId(List<Long> idFlightInstance, long idReservation, String sagaId) {
        for (long idF : idFlightInstance) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", idF)
                .setParameter("reservationId", idReservation)
                .getResultList();
            ReservationLine r = (reservationLine.isEmpty()) ? null : reservationLine.get(0);    
            if (r == null || !r.isActive() || !r.getSagaId().equals(sagaId)) 
                return false;
        }
        return true;
    }

    @Override
    public void updateReservationLines(List<ReservationLIneDTO> reservationLines) {
        for (ReservationLIneDTO rL : reservationLines) {
            List<ReservationLine> reservationLine = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", rL.getFlightInstanceId())
                .setParameter("reservationId", rL.getIdReservation())
                .getResultList();
            ReservationLine r = (reservationLine.isEmpty()) ? null : reservationLine.get(0);
            if(r != null)    
                r.setSagaId(rL.getSagaId());
        }

    }
    
}
