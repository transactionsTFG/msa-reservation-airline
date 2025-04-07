package business.saga.creationreservation.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.dto.CustomerDTO;
import msa.commons.microservices.reservationairline.commandevent.model.CustomerInfo;

@Mapper
public interface CreationReservationMapper {
    CreationReservationMapper INSTANCE = Mappers.getMapper(CreationReservationMapper.class);
    CustomerInfo dtoToCustomerInfo(CustomerDTO dto);
}
