package domainevent.command;

import javax.ejb.Local;
import javax.ejb.Stateless;

import domainevent.command.handler.BaseHandler;
import domainevent.command.handler.EventHandler;
import msa.commons.microservices.reservationairline.commandevent.CreateReservationCommand;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationRollbackQualifier;

@Stateless
@CreateReservationRollbackQualifier
@Local(EventHandler.class)
public class CreateReservationRollbackEvent extends BaseHandler {

    @Override
    public void handleCommand(Object data) {
        CreateReservationCommand c = this.gson.fromJson(data.toString(), CreateReservationCommand.class);
        this.reservationServices.removeReservation(c.getIdReservation());
    }
    
}
