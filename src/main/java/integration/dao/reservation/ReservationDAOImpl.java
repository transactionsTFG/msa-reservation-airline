package integration.dao.reservation;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import business.reservation.Reservation;
import business.reservation.ReservationDTO;

@Stateless
public class ReservationDAOImpl implements ReservationDAO {
    private EntityManager entityManager;
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}

    @Override
    public Reservation save(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setActive(reservationDTO.isActive());
        reservation.setCustomerId(reservationDTO.getCustomerId());
        reservation.setSagaId(reservationDTO.getSagaId());
        reservation.setStatusSaga(reservationDTO.getStatusSaga());
        reservation.setTotal(reservationDTO.getTotal());
        this.entityManager.persist(reservation);
        this.entityManager.flush();
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return Optional.ofNullable(this.entityManager.find(Reservation.class, id, LockModeType.OPTIMISTIC));
    }

    @Override
    public Optional<Reservation> update(ReservationDTO reservation) {
        return this.findById(reservation.getId())
                .map(r -> {
                    r.setActive(reservation.isActive());
                    r.setCustomerId(reservation.getCustomerId());
                    r.setStatusSaga(reservation.getStatusSaga());
                    return this.entityManager.merge(r);
                });
    }

}
