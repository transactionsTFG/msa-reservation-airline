package controller;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.reservation.ReservationWithLinesDTO;
import business.services.ReservationServices;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {
    private static final Logger LOGGER = LogManager.getLogger(ReservationController.class);
    private ReservationServices reservationServices;

    @GET
    @Path("/{idReservation}")
    public ReservationWithLinesDTO getReservation(@PathParam("idReservation") long idReservation) {
        LOGGER.info("Fetching reservation with ID: {}", idReservation);
        return this.reservationServices.getReservationWithLinesById(idReservation);
    }

	@EJB
	public void setReservationServices(ReservationServices reservationServices) {
		this.reservationServices = reservationServices;
	}
    
}

