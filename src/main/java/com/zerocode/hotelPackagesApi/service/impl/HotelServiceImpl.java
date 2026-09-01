package com.zerocode.hotelPackagesApi.service.impl;

import com.zerocode.hotelPackagesApi.controller.dto.HotelContactNumberDTO;
import com.zerocode.hotelPackagesApi.controller.dto.HotelImageDTO;
import com.zerocode.hotelPackagesApi.controller.request.CreateHotelRequest;
import com.zerocode.hotelPackagesApi.controller.request.UpdateHotelRequest;
import com.zerocode.hotelPackagesApi.exception.HotelApprovalException;
import com.zerocode.hotelPackagesApi.exception.HotelNotCreatedException;
import com.zerocode.hotelPackagesApi.exception.HotelNotFoundException;
import com.zerocode.hotelPackagesApi.model.*;
import com.zerocode.hotelPackagesApi.repository.HotelRepository;
import com.zerocode.hotelPackagesApi.service.HotelService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zerocode.hotelPackagesApi.model.ApprovalStatus.*;

@AllArgsConstructor
@Service
public class HotelServiceImpl implements HotelService {

    private HotelRepository hotelRepository;

    @Override
    @Transactional
    public void createHotel(CreateHotelRequest createHotelRequest) throws HotelNotCreatedException {
        Optional<Hotel> hotelOptional = hotelRepository.findByEmail(createHotelRequest.getEmail());
        if (hotelOptional.isPresent()) {
            throw new HotelNotCreatedException("Hotel is already registered with email " +createHotelRequest.getEmail());
        }
        Hotel hotel = new Hotel();
        hotel.setName(createHotelRequest.getName());
        hotel.setEmail(createHotelRequest.getEmail());
        hotel.setPassword(createHotelRequest.getPassword());
        hotel.setAddress(createHotelRequest.getAddress());
        hotel.setLogo(createHotelRequest.getLogo());

        List<HotelContactNumber> contactNumbers = createHotelRequest.getContactNumbers().stream()
                .map(dto -> {
                    HotelContactNumber contact = new HotelContactNumber();
                    contact.setNumber(dto.getNumber());
                    contact.setHotel(hotel);
                    return contact;
                }).collect(Collectors.toList());
        hotel.setContactNumbers(contactNumbers);

        List<HotelImage> hotelImages = createHotelRequest.getHotelPhotos().stream()
                .map(dto -> {
                    HotelImage image = new HotelImage();
                    image.setUrl(dto.getUrl());
                    image.setHotel(hotel);
                    return image;
                }).collect(Collectors.toList());

        hotel.setHotelPhotos(hotelImages);
        hotel.setBoostPackageLimit(createHotelRequest.getBoostPackageLimit());
        hotel.setSuperDealLimit(createHotelRequest.getSuperDealLimit());
        hotel.setStatus(createHotelRequest.getStatus());
        hotel.setApprovalStatus(PENDING);

        Authority hotelAuthority = new Authority();
        hotelAuthority.setAuthority("ROLE_USER");
        hotelAuthority.setHotel(hotel);
        hotelAuthority.setRole(Role.HOTEL);
        hotelRepository.save(hotel);

        hotel.setAuthorities(new java.util.ArrayList<>(java.util.Collections.singletonList(hotelAuthority)));
        hotelRepository.save(hotel);

    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel findById(Long hotelId) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new HotelNotFoundException("Hotel not found with id " + hotelId));
        return hotel;
    }

    @Override
    public void updateHotel(Long hotelId, UpdateHotelRequest updateHotelRequest) throws HotelNotFoundException {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException("Hotel with id " + hotelId + " not found"));
        hotel.setPassword(updateHotelRequest.getPassword());
        hotel.setBoostPackageLimit(updateHotelRequest.getBoostPackageLimit());

        List<HotelImageDTO> imageDTOs = updateHotelRequest.getHotelImageDTO();
        if (imageDTOs != null && !imageDTOs.isEmpty()) {
            List<HotelImage> images = imageDTOs.stream()
                    .map(dto -> {
                        HotelImage image = new HotelImage();
                        image.setUrl(dto.getUrl());
                        image.setHotel(hotel);
                        return image;
                    })
                    .collect(Collectors.toList());

            hotel.setHotelPhotos(images);
        }

        List<HotelContactNumberDTO> contactNumberDTOs = updateHotelRequest.getHotelContactNumberDTO();
        if (contactNumberDTOs != null && !contactNumberDTOs.isEmpty()) {
            List<HotelContactNumber> contactNumbers = contactNumberDTOs.stream()
                    .map(dto -> {
                        HotelContactNumber contact = new HotelContactNumber();
                        contact.setNumber(dto.getNumber());
                        contact.setHotel(hotel);
                        return contact;
                    })
                    .collect(Collectors.toList());

            hotel.setContactNumbers(contactNumbers);
        }

        hotel.setSuperDealLimit(updateHotelRequest.getSuperDealLimit());
        hotel.setStatus(updateHotelRequest.getStatus());
        hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(Long hotelId) throws HotelNotFoundException {
        Hotel hotelOptional = hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException("Hotel with id " + hotelId + " not found"));
        hotelRepository.deleteById(hotelId);
    }

    @Override
    public List<Hotel> findByStatus(Boolean status) {
        List<Hotel> hotels = hotelRepository.findByStatus(status);
        return hotels;
    }

    @Override
    public List<Hotel> getPendingHotels() {
        return hotelRepository.findByApprovalStatus(PENDING);
    }

    @Override
    public void approveHotel(Long hotelId) throws HotelNotFoundException, HotelApprovalException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        if (hotel.getApprovalStatus() == APPROVED) {
            throw new HotelApprovalException("Hotel is already approved");
        }
        if (hotel.getApprovalStatus() == REJECTED) {
            throw new HotelApprovalException("Cannot approve a rejected hotel");
        }

        hotel.setApprovalStatus(APPROVED);
        hotelRepository.save(hotel);
    }

    @Override
    public void rejectHotel(Long hotelId) throws HotelNotFoundException, HotelApprovalException {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));

        if (hotel.getApprovalStatus() == REJECTED) {
            throw new HotelApprovalException("Hotel is already rejected");
        }
        if (hotel.getApprovalStatus() == APPROVED) {
            throw new HotelApprovalException("Cannot reject an approved hotel");
        }

        hotel.setApprovalStatus(REJECTED);
        hotelRepository.save(hotel);
    }

}
