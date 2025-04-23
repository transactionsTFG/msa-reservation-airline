package controller;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ejb.EJB;

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
import business.usecase.cancelreservation.CancelReservationUseCase;
import business.usecase.createreservation.CreateReservationUseCase;
import business.usecase.modifyreservation.ModifyReservationUseCase;
import msa.commons.utils.UseCaseResult;

@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationController {
    private static final Logger LOGGER = LogManager.getLogger(ReservationController.class);
    private CreateReservationUseCase createReservationUseCase;
    private ModifyReservationUseCase modifyReservationUseCase;
    private CancelReservationUseCase cancelReservationUseCase;

    @POST
    public Response createReservation(ReservationRequestDTO reservation) {
        LOGGER.info("Iniciando creacion de reserva: {}", reservation);
        UseCaseResult<Boolean> createResult = this.createReservationUseCase.create(reservation);
        if (createResult.isPresent()) 
            return Response.status(Response.Status.CREATED).entity("La creacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(createResult.getMessage()).build();
    }

    @POST
    @Path("/modify")
    public Response modifyReservation(UpdateResevationDTO resevation){
        LOGGER.info("Iniciando modificacion de reserva: {}", resevation);
        UseCaseResult<Boolean> modifyResult = this.modifyReservationUseCase.modify(resevation);
        if (modifyResult.isPresent()) 
            return Response.status(Response.Status.OK).entity("La modificacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(modifyResult.getMessage()).build();
    }

    @DELETE
    @Path("/{idReservation}")
    public Response cancelReservation(@PathParam("idReservation") long idReservation) {
        LOGGER.info("Iniciando cancelacion de reserva: {}", idReservation);
        UseCaseResult<Boolean> cancelResult = this.cancelReservationUseCase.cancel(idReservation);
        if (cancelResult.isPresent()) 
            return Response.status(Response.Status.OK).entity("La cancelacion de la reserva se ha inicializado").build();
        else 
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(cancelResult.getMessage()).build();
    }

    
    @EJB public void setCancelReservationUseCase(CancelReservationUseCase cancelReservationUseCase) { this.cancelReservationUseCase = cancelReservationUseCase; }
    @EJB public void setCreateReservationUseCase(CreateReservationUseCase createReservationUseCase) { this.createReservationUseCase = createReservationUseCase; }
    @EJB public void setModifyReservationUseCase(ModifyReservationUseCase modifyReservationUseCase) { this.modifyReservationUseCase = modifyReservationUseCase; }
}
