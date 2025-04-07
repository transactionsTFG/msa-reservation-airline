package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import business.saga.creationreservation.qualifier.CreateReservationBeginQualifier;
import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;

import msa.commons.microservices.reservationairline.qualifier.CreateReservationCommitQualifier;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationRollbackQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);
    private EventHandler createReservationCommitEvent;
    private EventHandler createReservationRollbackEvent;
    private EventHandler createReservationBeginEvent;

    
    @PostConstruct
    public void init(){
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, this.createReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, this.createReservationRollbackEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_BEGIN_SAGA, this.createReservationBeginEvent);        
    }

    public EventHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setCreateReservationCommitEvent(@CreateReservationCommitQualifier EventHandler createReservationCommitEvent) {
        this.createReservationCommitEvent = createReservationCommitEvent;
    }

    @Inject
    public void setCreateReservationRollbackEvent(@CreateReservationRollbackQualifier EventHandler createReservationRollbackEvent) {
        this.createReservationRollbackEvent = createReservationRollbackEvent;
    }

    @Inject
    public void setCreateReservationBeginEvent(@CreateReservationBeginQualifier EventHandler createReservationBeginEvent) {
        this.createReservationBeginEvent = createReservationBeginEvent;
    }

}
