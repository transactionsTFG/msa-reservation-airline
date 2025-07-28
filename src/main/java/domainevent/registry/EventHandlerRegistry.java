package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import business.qualifier.createreservation.CreateReservationCommitQualifier;
import business.qualifier.createreservation.CreateReservationRollbackQualifier;
import business.qualifier.modifyreservation.UpdateReservationByModifyReservationCommit;
import business.qualifier.modifyreservation.UpdateReservationByModifyReservationRollback;
import business.qualifier.removereservation.RemoveReservationByCommitQualifier;
import business.qualifier.removereservation.RemoveReservationByRollbackQualifier;
import business.saga.creationreservation.qualifier.CreateReservationBeginQualifier;
import business.saga.deletereservation.qualifier.RemoveReservationBeginQualifier;
import business.saga.updatereservation.qualifier.UpdateReservationBeginQualifier;
import domainevent.command.handler.CommandHandler;

import msa.commons.event.EventId;



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
    private CommandHandler removeReservationBeginEvent;
    private CommandHandler removeReservationCommitEvent;
    private CommandHandler removeReservationRollbackEvent;
    
    @PostConstruct
    public void init(){
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_COMMIT_SAGA, this.createReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_CREATE_RESERVATION_ROLLBACK_SAGA, this.createReservationRollbackEvent);
        this.handlers.put(EventId.CREATE_RESERVATION_TRAVEL, this.createReservationBeginEvent);        
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_BEGIN_SAGA, this.updateReservationBeginEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_COMMIT_SAGA, this.updateReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_MODIFY_RESERVATION_ROLLBACK_SAGA, this.updateReservationRollbackEvent);
        this.handlers.put(EventId.REMOVE_RESERVATION_TRAVEL, this.removeReservationBeginEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_COMMIT_SAGA, this.removeReservationCommitEvent);
        this.handlers.put(EventId.RESERVATION_AIRLINE_REMOVE_RESERVATION_ROLLBACK_SAGA, this.removeReservationRollbackEvent);
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

    @Inject
    public void setRemoveReservationBeginEvent(@RemoveReservationBeginQualifier CommandHandler removeReservationBeginEvent) {
        this.removeReservationBeginEvent = removeReservationBeginEvent;
    }

    @Inject
    public void setRemoveReservationCommitEvent(@RemoveReservationByCommitQualifier CommandHandler removeReservationCommitEvent) {
        this.removeReservationCommitEvent = removeReservationCommitEvent;
    }

    @Inject
    public void setRemoveReservationRollbackEvent(@RemoveReservationByRollbackQualifier CommandHandler removeReservationRollbackEvent) {
        this.removeReservationRollbackEvent = removeReservationRollbackEvent;
    }


}
