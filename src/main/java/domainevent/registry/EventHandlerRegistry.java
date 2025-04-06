package domainevent.registry;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import domainevent.command.handler.EventHandler;

import msa.commons.event.EventId;
import msa.commons.microservices.aircraft.qualifier.GetAircraftByIdQualifier;

@Singleton
@Startup
public class EventHandlerRegistry {
    private Map<EventId, EventHandler> handlers = new EnumMap<>(EventId.class);

    @PostConstruct
    public void init(){
        
    }

    public EventHandler getHandler(EventId eventId) {
        return this.handlers.get(eventId);
    }

}
