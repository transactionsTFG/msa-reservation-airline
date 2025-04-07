package business.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import business.reservationline.ReservationLIneDTO;
import business.reservationline.ReservationLine;


@Mapper
public interface ReservationLineMapper {
    ReservationLineMapper INSTANCE = Mappers.getMapper(ReservationLineMapper.class);
    @Mapping(target = "idReservation", source = "reservationId.id")
    ReservationLIneDTO entityToDto(ReservationLine reservationLine);
    List<ReservationLIneDTO> entityToDto(Set<ReservationLine> reservationLines);
}
