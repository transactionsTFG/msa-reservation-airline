package controller;

import javax.ws.rs.Path;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.ReservationDTO;
import business.servicesevent.ReservationEventServices;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {
    private static final Logger LOGGER = LogManager.getLogger(ReservationController.class);
    private ReservationEventServices reservationEventServices;

    @POST
    @Transactional
    public Response createUser(ReservationDTO reservation) {
        LOGGER.info("Iniciando creacion de reserva: {}", reservation);
        boolean success = this.reservationEventServices.beginCreateReservationEvent(reservation);
        if (success) 
            return Response.status(Response.Status.CREATED).entity("La creacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al crear la reserva").build();
    }

    @EJB
    public void setReservationEventServices(ReservationEventServices reservationEventServices) {
        this.reservationEventServices = reservationEventServices;
    }
}
