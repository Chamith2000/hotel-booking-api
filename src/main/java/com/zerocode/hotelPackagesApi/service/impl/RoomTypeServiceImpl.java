package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.dto.RoomTypeDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateRoomTypeRequest;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.exception.RoomTypeNotCreatedException;
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelRoomType;
import com.zerocode.hotelPackagesApi.model.RoomType;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.repository.HotelRoomTypeRepository;
import com.zerocode.hotelPackagesApi.repository.RoomTypeRepository;
import com.zerocode.hotelPackagesApi.service.RoomTypeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    HotelRepository hotelRepository;
    RoomTypeRepository roomTypeRepository;
    HotelRoomTypeRepository hotelRoomTypeRepository;

    @Override
    @Transactional
    public void createRoomType(Long hotelId, CreateRoomTypeRequest createRoomTypeRequest) throws HotelNotFoundException, RoomTypeNotCreatedException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        for (RoomTypeDTO roomTypeDTO : createRoomTypeRequest.getRoomTypes()) {
            RoomType roomType;

            Optional<RoomType> existingRoomType = roomTypeRepository.findByType(roomTypeDTO.getRoomType().getType());

            if (existingRoomType.isPresent()) {
                roomType = existingRoomType.get();
            } else {
                RoomType newRoomType = new RoomType();
                newRoomType.setType(roomTypeDTO.getRoomType().getType());
                newRoomType.setPrice(roomTypeDTO.getPrice());
                roomType = roomTypeRepository.save(newRoomType);
            }

            Optional<HotelRoomType> existingHotelRoomType = hotelRoomTypeRepository
                    .findByHotelAndRoomType(hotel, roomType);

            if (existingHotelRoomType.isPresent()) {
                throw new RoomTypeNotCreatedException("Room type " + roomType.getType() +
                        " is already associated with this hotel");
            }

            HotelRoomType hotelRoomType = new HotelRoomType();
            hotelRoomType.setHotel(hotel);
            hotelRoomType.setRoomType(roomType);
            hotelRoomType.setPrice(roomTypeDTO.getPrice());
            hotelRoomTypeRepository.save(hotelRoomType);
        }
    }

    @Override
    @Transactional
    public void updateRoomTypePrice(Long hotelId, Long roomTypeId, UpdateRoomTypeRequest updateRoomTypeRequest) throws HotelNotFoundException, RoomTypeNotCreatedException {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RoomTypeNotCreatedException("Room type not found with ID: " + roomTypeId));

        Optional<HotelRoomType> existingHotelRoomType = hotelRoomTypeRepository
                .findByHotelAndRoomType(hotel, roomType);

        if (existingHotelRoomType.isPresent()) {
            HotelRoomType hotelRoomType = existingHotelRoomType.get();
            hotelRoomType.setPrice(updateRoomTypeRequest.getPrice());
            hotelRoomTypeRepository.save(hotelRoomType);
        } else {
            throw new RoomTypeNotCreatedException("Room type is not associated with this hotel");
        }
    }

    @Override
    @Transactional
    public void deleteRoomTypeFromHotel(Long hotelId, Long roomTypeId) throws HotelNotFoundException, RoomTypeNotCreatedException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RoomTypeNotCreatedException("Room type not found with ID: " + roomTypeId));

        Optional<HotelRoomType> existingHotelRoomType = hotelRoomTypeRepository
                .findByHotelAndRoomType(hotel, roomType);

        if (existingHotelRoomType.isPresent()) {
            hotelRoomTypeRepository.delete(existingHotelRoomType.get());
        } else {
            throw new RoomTypeNotCreatedException("Room type is not associated with this hotel");
        }
    }

    @Override
    public List<RoomTypeDTO> getAllRoomTypesByHotelId(Long hotelId) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findByHotel(hotel);

        List<RoomTypeDTO> roomTypeDTOs = new ArrayList<>();
        for (HotelRoomType hotelRoomType : hotelRoomTypes) {
            RoomTypeDTO roomTypeDTO = new RoomTypeDTO();
            roomTypeDTO.setRoomType(hotelRoomType.getRoomType());
            roomTypeDTO.setPrice(hotelRoomType.getPrice());

            roomTypeDTOs.add(roomTypeDTO);
        }
        return roomTypeDTOs;
    }
}
