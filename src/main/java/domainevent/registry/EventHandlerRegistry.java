package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import business.saga.creationreservation.qualifier.CreateReservationBeginQualifier;
import business.saga.updatereservation.qualifier.UpdateReservationBeginQualifier;
import domainevent.command.handler.CommandHandler;

import msa.commons.event.EventId;

import msa.commons.microservices.reservationairline.qualifier.CreateReservationCommitQualifier;
import msa.commons.microservices.reservationairline.qualifier.CreateReservationRollbackQualifier;
import msa.commons.microservices.reservationairline.updatereservation.qualifier.UpdateReservationByModifyReservationCommit;
import msa.commons.microservices.reservationairline.updatereservation.qualifier.UpdateReservationByModifyReservationRollback;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, CommandHandler> handlers = new EnumMap<>(EventId.class);
    private CommandHandler createReservationCommitEvent;
    private CommandHandler createReservationRollbackEvent;
    private CommandHandler createReservationBeginEvent;
    private CommandHandler updateReservationBeginEvent;
    private CommandHandler updateReservationCommitEvent;
    private CommandHandler updateReservationRollbackEvent;
    
    @PostConstruct
    public void init(){
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, this.createReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, this.createReservationRollbackEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_BEGIN_SAGA, this.createReservationBeginEvent);        
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_BEGIN_SAGA, this.updateReservationBeginEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_COMMIT_SAGA, this.updateReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, this.updateReservationRollbackEvent);
    }

    public CommandHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

    @Inject
    public void setCreateReservationCommitEvent(@CreateReservationCommitQualifier CommandHandler createReservationCommitEvent) {
        this.createReservationCommitEvent = createReservationCommitEvent;
    }

    @Inject
    public void setCreateReservationRollbackEvent(@CreateReservationRollbackQualifier CommandHandler createReservationRollbackEvent) {
        this.createReservationRollbackEvent = createReservationRollbackEvent;
    }

    @Inject
    public void setCreateReservationBeginEvent(@CreateReservationBeginQualifier CommandHandler createReservationBeginEvent) {
        this.createReservationBeginEvent = createReservationBeginEvent;
    }

    @Inject
    public void setUpdateReservationBeginEvent(@UpdateReservationBeginQualifier CommandHandler updateReservationBeginEvent) {
        this.updateReservationBeginEvent = updateReservationBeginEvent;
    }

    @Inject
    public void setUpdateReservationCommitEvent(@UpdateReservationByModifyReservationCommit CommandHandler updateReservationCommitEvent) {
        this.updateReservationCommitEvent = updateReservationCommitEvent;
    }

    @Inject
    public void setUpdateReservationRollbackEvent(@UpdateReservationByModifyReservationRollback CommandHandler updateReservationRollbackEvent) {
        this.updateReservationRollbackEvent = updateReservationRollbackEvent;
    }

}
