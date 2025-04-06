package domainevent.command.handler;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.google.gson.Gson;

import business.services.ReservationServices;
import domainevent.publisher.IJMSEventPublisher;

public abstract class BaseHandler implements EventHandler {
    protected ReservationServices reservationServices;
    protected IJMSEventPublisher jmsEventPublisher;
    protected Gson gson;
    @EJB
    public void setTypeUserServices(ReservationServices reservationServices) {
        this.reservationServices = reservationServices;
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
