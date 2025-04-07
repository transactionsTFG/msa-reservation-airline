package domainevent.consumer;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.transaction.Transactional;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import domainevent.command.handler.EventHandler;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.consts.JMSQueueNames;
import msa.commons.event.Event;


@MessageDriven(mappedName = JMSQueueNames.AIRLINE_RESERVATION_QUEUE)
public class DomainEventConsumerReservationService implements MessageListener{
    
    private Gson gson;
    private EventHandlerRegistry eventHandlerRegistry;
    private static final Logger LOGGER = LogManager.getLogger(DomainEventConsumerReservationService.class);

    @Override
    @Transactional 
    public void onMessage(Message msg) {
        try {
            if(msg instanceof TextMessage m) {
                Event event = this.gson.fromJson(m.getText(), Event.class);
                LOGGER.info("Recibido en Cola {}, Evento Id: {}, EventResponse: {}", JMSQueueNames.AIRLINE_RESERVATION_QUEUE, event.getEventId(), event.getData());
                EventHandler commandHandler = this.eventHandlerRegistry.getHandler(event.getEventId());
                if(commandHandler != null)
                    commandHandler.handleCommand(event.getData());
            }
        } catch (Exception e) {
            LOGGER.error("Error al recibir el mensaje: {}", e.getMessage());

        }
    }
    
    @Inject
    public void setGson(Gson gson) { this.gson = gson; }
    @EJB
    public void setCommandHandlerRegistry(EventHandlerRegistry commandHandlerRegistry) { this.eventHandlerRegistry = commandHandlerRegistry; }
}
