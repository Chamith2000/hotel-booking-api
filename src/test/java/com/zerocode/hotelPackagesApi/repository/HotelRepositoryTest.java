package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.Address; // Assuming Address is a model class
import com.zerocode.hotelPackagesApi.model.Hotel;
import com.zerocode.hotelPackagesApi.model.HotelContactNumber;
import com.zerocode.hotelPackagesApi.model.HotelImage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void clearDB() {
        entityManager.getEntityManager().createQuery("DELETE FROM SuperDeal").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM HotelPackage").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM HotelContactNumber").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM HotelImage").executeUpdate();
        hotelRepository.deleteAll();
    }

    @Test
    public void testSaveHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setEmail("test@hotel.com");
        hotel.setPassword("password123");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);

        Address address = new Address();
        address.setStreet("123 Test St");
        hotel.setAddress(address);

        hotel.setLogo("logo.png");
        hotel.setBoostPackageLimit("5");
        hotel.setSuperDealLimit("3");
        hotel.setStatus(true);

        List<HotelContactNumber> contactNumbers = new ArrayList<>();
        HotelContactNumber contact = new HotelContactNumber();
        contact.setNumber("123-456-7890");
        contact.setHotel(hotel);
        contactNumbers.add(contact);
        hotel.setContactNumbers(contactNumbers);

        List<HotelImage> images = new ArrayList<>();
        HotelImage image = new HotelImage();
        image.setUrl("image1.jpg");
        image.setHotel(hotel);
        images.add(image);
        hotel.setHotelPhotos(images);

        Hotel savedHotel = hotelRepository.save(hotel);

        Assertions.assertNotNull(savedHotel);
        Assertions.assertNotNull(savedHotel.getId());
        Assertions.assertEquals("Test Hotel", savedHotel.getName());
        Assertions.assertEquals("test@hotel.com", savedHotel.getEmail());
        Assertions.assertEquals("123 Test St", savedHotel.getAddress().getStreet());
        Assertions.assertEquals("5", savedHotel.getBoostPackageLimit());
        Assertions.assertEquals("3", savedHotel.getSuperDealLimit());
        Assertions.assertEquals(1, savedHotel.getContactNumbers().size());
        Assertions.assertEquals("123-456-7890", savedHotel.getContactNumbers().get(0).getNumber());
        Assertions.assertEquals(1, savedHotel.getHotelPhotos().size());
        Assertions.assertEquals("image1.jpg", savedHotel.getHotelPhotos().get(0).getUrl());
    }

    @Test
    public void testGetHotelById() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setEmail("test@hotel.com");
        hotel.setPassword("password123");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address = new Address();
        address.setStreet("123 Test St");
        hotel.setAddress(address);
        Hotel savedHotel = hotelRepository.save(hotel);

        Assertions.assertNotNull(savedHotel);
        Assertions.assertNotNull(savedHotel.getId());

        Optional<Hotel> foundHotel = hotelRepository.findById(savedHotel.getId());
        Assertions.assertTrue(foundHotel.isPresent());
        Assertions.assertEquals(savedHotel.getId(), foundHotel.get().getId());
        Assertions.assertEquals("Test Hotel", foundHotel.get().getName());
        Assertions.assertEquals("test@hotel.com", foundHotel.get().getEmail());
        Assertions.assertEquals("123 Test St", foundHotel.get().getAddress().getStreet());
    }

    @Test
    public void testGetHotelByNonExistingId() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setEmail("test@hotel.com");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address = new Address();
        address.setStreet("123 Test St");
        hotel.setAddress(address);
        Hotel savedHotel = hotelRepository.save(hotel);

        Assertions.assertNotNull(savedHotel);
        Assertions.assertNotNull(savedHotel.getId());

        Optional<Hotel> foundHotel = hotelRepository.findById(999L);
        Assertions.assertFalse(foundHotel.isPresent());
    }

    @Test
    public void testFindAllHotels() {
        Hotel hotel1 = new Hotel();
        hotel1.setName("Hotel 1");
        hotel1.setEmail("hotel1@hotel.com");
        hotel1.setPassword("pass1");
        hotel1.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address1 = new Address();
        address1.setStreet("1 Hotel St");
        hotel1.setAddress(address1);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setName("Hotel 2");
        hotel2.setEmail("hotel2@hotel.com");
        hotel2.setPassword("pass2");
        hotel2.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address2 = new Address();
        address2.setStreet("2 Hotel St");
        hotel2.setAddress(address2);
        hotelRepository.save(hotel2);

        List<Hotel> allHotels = hotelRepository.findAll();

        Assertions.assertEquals(2, allHotels.size());
        Assertions.assertTrue(allHotels.stream().anyMatch(h -> h.getName().equals("Hotel 1")));
        Assertions.assertTrue(allHotels.stream().anyMatch(h -> h.getName().equals("Hotel 2")));
    }

    @Test
    public void testDeleteHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setEmail("test@hotel.com");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address = new Address();
        address.setStreet("123 Test St");
        hotel.setAddress(address);
        Hotel savedHotel = hotelRepository.save(hotel);

        Assertions.assertNotNull(savedHotel);
        Assertions.assertNotNull(savedHotel.getId());

        hotelRepository.deleteById(savedHotel.getId());

        Optional<Hotel> foundHotel = hotelRepository.findById(savedHotel.getId());
        Assertions.assertFalse(foundHotel.isPresent());
    }

    @Test
    public void testFindByEmail() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setEmail("test@hotel.com");
        hotel.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address = new Address();
        address.setStreet("123 Test St");
        hotel.setAddress(address);
        Hotel savedHotel = hotelRepository.save(hotel);

        Assertions.assertNotNull(savedHotel);
        Assertions.assertNotNull(savedHotel.getId());

        Optional<Hotel> foundHotel = hotelRepository.findByEmail("test@hotel.com");
        Assertions.assertTrue(foundHotel.isPresent());
        Assertions.assertEquals(savedHotel.getId(), foundHotel.get().getId());
        Assertions.assertEquals("Test Hotel", foundHotel.get().getName());

        Optional<Hotel> notFoundHotel = hotelRepository.findByEmail("nonexistent@hotel.com");
        Assertions.assertFalse(notFoundHotel.isPresent());
    }

    @Test
    public void testFindByStatus() {
        Hotel hotel1 = new Hotel();
        hotel1.setName("Active Hotel");
        hotel1.setEmail("active@hotel.com");
        hotel1.setStatus(true);
        hotel1.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address1 = new Address();
        address1.setStreet("1 Active St");
        hotel1.setAddress(address1);
        hotelRepository.save(hotel1);

        Hotel hotel2 = new Hotel();
        hotel2.setName("Inactive Hotel");
        hotel2.setEmail("inactive@hotel.com");
        hotel2.setStatus(false);
        hotel2.setApprovalStatus(com.zerocode.hotelPackagesApi.model.ApprovalStatus.APPROVED);
        Address address2 = new Address();
        address2.setStreet("2 Inactive St");
        hotel2.setAddress(address2);
        hotelRepository.save(hotel2);

        List<Hotel> activeHotels = hotelRepository.findByStatus(true);
        Assertions.assertEquals(1, activeHotels.size());
        Assertions.assertEquals("Active Hotel", activeHotels.get(0).getName());

        List<Hotel> inactiveHotels = hotelRepository.findByStatus(false);
        Assertions.assertEquals(1, inactiveHotels.size());
        Assertions.assertEquals("Inactive Hotel", inactiveHotels.get(0).getName());
    }
}