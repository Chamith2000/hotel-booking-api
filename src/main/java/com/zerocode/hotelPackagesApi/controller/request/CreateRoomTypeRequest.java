package com.zerocode.hotelPackagesApi.controller.request;

import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateRoomTypeRequest {
    private List<RoomTypeDTO> roomTypes;

}
