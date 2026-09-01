package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelMenu;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class HotelMenuRepositoryTest {

    @Autowired
    private HotelMenuRepository hotelMenuRepository;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel;

    @BeforeEach
    public void setUp() {

        hotelMenuRepository.deleteAll();
        hotelRepository.deleteAll();

        hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        hotelRepository.save(hotel);
    }

    @Test
    public void testFindByHotelId() {

        HotelMenu menu1 = new HotelMenu();
        menu1.setType("Breakfast");
        menu1.setHotel(hotel);
        hotelMenuRepository.save(menu1);

        HotelMenu menu2 = new HotelMenu();
        menu2.setType("Lunch");
        menu2.setHotel(hotel);
        hotelMenuRepository.save(menu2);

        List<HotelMenu> menus = hotelMenuRepository.findByHotel_Id(hotel.getId());

        Assertions.assertNotNull(menus);
        Assertions.assertEquals(2, menus.size());
        Assertions.assertTrue(menus.stream().anyMatch(menu -> menu.getType().equals("Breakfast")));
        Assertions.assertTrue(menus.stream().anyMatch(menu -> menu.getType().equals("Lunch")));
    }

    @Test
    public void testFindByHotelIdNoMenu() {

        Hotel newHotel = new Hotel();
        newHotel.setName("New Hotel");
        newHotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        hotelRepository.save(newHotel);

        List<HotelMenu> menus = hotelMenuRepository.findByHotel_Id(newHotel.getId());

        Assertions.assertNotNull(menus);
        Assertions.assertTrue(menus.isEmpty());
    }
}
