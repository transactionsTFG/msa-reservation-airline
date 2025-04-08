package domainevent.command;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationRollbackQualifier;

@Stateless
@CreateReservationRollbackQualifier
@Local(EventHandler.class)
public class CreateReservationRollbackEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CreateReservationRollbackEvent.class);
    @Override
    public void handleCommand(String json) {
        LOGGER.info("***** INICIAMOS ROLLBACK SAGA CREACION DE RESERVA *****");
        CreateReservationCommand c = this.gson.fromJson(json, CreateReservationCommand.class);
        this.reservationServices.removeReservation(c.getIdReservation());
    }
    
}
