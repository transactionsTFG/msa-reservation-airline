package controller;

import javax.ws.rs.Path;

import javax.ejb.EJB;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import business.dto.ReservationRequestDTO;
import business.dto.modifyreservation.UpdateResevationDTO;
import business.services.ReservationServices;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {
    private static final Logger LOGGER = LogManager.getLogger(ReservationController.class);
    private ReservationServices reservationServices;

    @POST
    @Transactional
    public Response createReservation(ReservationRequestDTO reservation) {
        LOGGER.info("Iniciando creacion de reserva: {}", reservation);
        boolean success = this.reservationServices.creationReservationAsync(reservation);
        if (success) 
            return Response.status(Response.Status.CREATED).entity("La creacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No ha superado las reglas de negocio").build();
    }

    @POST
    @Path("/modify")
    @Transactional
    public Response modifyReservation(UpdateResevationDTO resevation){
        LOGGER.info("Iniciando modificacion de reserva: {}", resevation);
        boolean success = this.reservationServices.modifyReservationAsync(resevation);
        if (success) 
            return Response.status(Response.Status.OK).entity("La modificacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No ha superado las reglas de negocio").build();
    }

    @DELETE
    @Transactional
    @Path("/{idReservation}")
    public Response cancelReservation(long idReservation) {
        LOGGER.info("Iniciando cancelacion de reserva: {}", idReservation);
        boolean success = this.reservationServices.cancelReservationAsync(idReservation);
        if (success) 
            return Response.status(Response.Status.OK).entity("La cancelacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("No ha superado las reglas de negocio").build();
    }

    @EJB
    public void setReservationEventServices(ReservationServices reservationServices) {
        this.reservationServices = reservationServices;
    }
}
