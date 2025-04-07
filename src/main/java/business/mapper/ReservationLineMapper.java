package business.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import business.reservationline.ReservationLIneDTO;
import business.reservationline.ReservationLine;


@Mapper
public interface ReservationLineMapper {
    ReservationLineMapper INSTANCE = Mappers.getMapper(ReservationLineMapper.class);

    default ReservationLIneDTO entityToDto(ReservationLine reservationLine) {
        if (reservationLine == null) return null;

        ReservationLIneDTO dto = new ReservationLIneDTO();
        dto.setIdReservation(reservationLine.getReservationId().getId());
        dto.setFlightInstanceId(reservationLine.getFlightInstanceId());
        dto.setActive(reservationLine.isActive());
        dto.setPassengers(reservationLine.getPassengers());
        dto.setPrice(reservationLine.getPrice());
        return dto;
    }


    List<ReservationLIneDTO> entityToDto(Set<ReservationLine> reservationLines);
}
