package domainevent.command.handler;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.google.gson.Gson;

import business.services.ReservationLineServices;
import business.services.ReservationServices;
import domainevent.publisher.IJMSEventPublisher;

public abstract class BaseHandler implements CommandHandler {
    protected ReservationServices reservationServices;
    protected ReservationLineServices reservationLineServices;
    protected IJMSEventPublisher jmsEventPublisher;
    protected Gson gson;
    @EJB
    public void setTypeUserServices(ReservationServices reservationServices) {
        this.reservationServices = reservationServices;
    }
    
    @EJB
    public void setReservationLineServices(ReservationLineServices reservationLineServices) {
        this.reservationLineServices = reservationLineServices;
    }

    @EJB
    public void setJmsEventPublisher(IJMSEventPublisher jmsEventPublisher) {
        this.jmsEventPublisher = jmsEventPublisher;
    }

    @Inject
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
