package integration.dao.reservationline;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import business.reservationline.ReservationLine;

@Stateless
public class ReservationLineDAOImpl implements ReservationLineDAO {
    private EntityManager entityManager;
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}

    @Override
    public Optional<ReservationLine> findByFlightInstanceIdAndReservationId(long flightInstanceId, long reservationId) {
        List<ReservationLine> result  = this.entityManager.createNamedQuery("ReservationLine.findByFlightInstanceIdAndReservationId", ReservationLine.class)
                .setParameter("flightInstanceId", flightInstanceId)
                .setParameter("reservationId", reservationId)
                .getResultList();
        return result.stream().findFirst();  
    }

}
