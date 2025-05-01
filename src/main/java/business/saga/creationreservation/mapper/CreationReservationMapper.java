package business.saga.creationreservation.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.dto.CustomerDTO;
import msa.commons.commands.createreservation.model.CustomerInfo;

@Mapper
public interface CreationReservationMapper {
    CreationReservationMapper INSTANCE = Mappers.getMapper(CreationReservationMapper.class);
    
    default CustomerInfo dtoToCustomerInfo(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setDni(dto.getDni());
        customerInfo.setEmail(dto.getEmail());
        customerInfo.setName(dto.getName());
        customerInfo.setPhone(dto.getPhone());
        return customerInfo;
    }
}
