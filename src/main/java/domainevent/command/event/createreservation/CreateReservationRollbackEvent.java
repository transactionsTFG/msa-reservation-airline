package domainevent.command.event.createreservation;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.CommandHandler;
import msa.commons.event.EventData;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationRollbackQualifier;

@Stateless
@CreateReservationRollbackQualifier
@Local(CommandHandler.class)
public class CreateReservationRollbackEvent extends BaseHandler {
    private static final Logger LOGGER = LogManager.getLogger(CreateReservationRollbackEvent.class);
    @Override
    public void commandPublisher(String json) {
        LOGGER.info("***** INICIAMOS ROLLBACK SAGA CREACION DE RESERVA *****");
        EventData eventData = EventData.fromJson(json, CreateReservationCommand.class);
        CreateReservationCommand c = (CreateReservationCommand) eventData.getData();
        this.reservationServices.removeReservation(c.getIdReservation());
        LOGGER.info("***** ROLLBACK TERMINADO CON EXITO EN SAGA CREACION DE RESERVA *****");
    }
    
}
