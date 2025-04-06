package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.dto.ReservationDTO;
import msa.commons.microservices.reservationairline.commandevent.CreateCustomerCommand;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "customerInfo", source = "customer")
    @Mapping(target = "flightInstanceInfo", source = "flightInstanceSeats")
    CreateCustomerCommand dtoToCommandCreateCustome(ReservationDTO reservationDTO); 
}
