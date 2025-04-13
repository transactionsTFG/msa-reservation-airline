package domainevent.publisher;

import msa.commons.event.EventData;
import msa.commons.event.EventId;

public interface IJMSEventPublisher {
    void publish(EventId event, EventData data);    
}
