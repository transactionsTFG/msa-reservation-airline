package business.usecase.createreservation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.google.gson.Gson;

import business.dto.ReservationRequestDTO;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.event.EventId;
import msa.commons.utils.UseCaseResult;
import rules.RulesBusinessCustomer;

@Stateless
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {
    private RulesBusinessCustomer rulesBusinessCustomer;
    private EventHandlerRegistry eventRegistry;
    private Gson gson;
    @Inject public void setRulesBusinessCustomer(RulesBusinessCustomer rulesBusinessCustomer) { this.rulesBusinessCustomer = rulesBusinessCustomer; }
    @EJB public void setCommandHandlerRegistry(EventHandlerRegistry eventRegistry) { this.eventRegistry = eventRegistry; }
    @Inject public void setGson(Gson gson) { this.gson = gson;  }

    @Override
    @Transactional
    public UseCaseResult<Boolean> create(ReservationRequestDTO request) {
        if (!this.rulesBusinessCustomer.isValid(request.getCustomer())) 
            return UseCaseResult.failure("Invalid customer data");
        
        this.eventRegistry.getHandler(EventId.CREATE_RESERVATION_TRAVEL)
                                 .commandPublisher(this.gson.toJson(request));
        return UseCaseResult.success(true);                   
    }
    
}
