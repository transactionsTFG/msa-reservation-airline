package business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.reservation.Reservation;
import business.reservation.ReservationDTO;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    ReservationDTO entityToDto(Reservation reservation);
}
