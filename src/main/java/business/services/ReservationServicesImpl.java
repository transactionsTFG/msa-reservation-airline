package business.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import business.dto.FlightInstanceSeatsDTO;
import business.dto.ReservationDTO;
import business.reservation.Reservation;
import business.reservationline.ReservationLine;
import msa.commons.saga.SagaPhases;


@Stateless
public class ReservationServicesImpl implements ReservationServices {
    private EntityManager entityManager;

    @Override
    public boolean beginCreationReservation(List<FlightInstanceSeatsDTO> flightInstanceSeatsDTO) {
        Reservation reservation = new Reservation();
        reservation.setActive(false);
        reservation.setTotal(0.0);
        reservation.setStatusSaga(SagaPhases.STARTED);
        reservation.setCustomerId(-1L);
        this.entityManager.persist(reservation);
        this.entityManager.flush();
        for(FlightInstanceSeatsDTO f : flightInstanceSeatsDTO) {
            ReservationLine reservationLine = new ReservationLine();
            reservationLine.setActive(false);
            reservationLine.setFlightInstanceId(f.getIdFlightInstance());
            reservationLine.setPassengers(f.getNumberSeats());
            reservationLine.setPrice(0);
            reservationLine.setReservationId(reservation);
            this.entityManager.persist(reservationLine);
        }

        return true;
    }
    

    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
}
