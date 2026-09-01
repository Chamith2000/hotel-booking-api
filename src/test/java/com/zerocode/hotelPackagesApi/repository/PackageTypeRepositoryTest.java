
package com.zerocode.hotelPackagesApi.repository;

import com.zerocode.hotelPackagesApi.model.PackageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PackageTypeRepositoryTest {

    @Autowired
    private PackageTypeRepository packageTypeRepository;

    @BeforeEach
    public void clearDB() {
        packageTypeRepository.deleteAll();
    }

    @Test
    public void testGetPackageTypeByExitingName() {
        String name = "Couple outing";

        PackageType packageType = new PackageType();
        packageType.setName(name);

        PackageType packageTypeCreate = packageTypeRepository.save(packageType);

        Assertions.assertNotNull(packageTypeCreate);

        PackageType packageTypeFound = packageTypeRepository.findByName(name).orElse(null);
        Assertions.assertNotNull(packageTypeFound);

        Assertions.assertNotNull(packageTypeCreate.getName(), packageTypeFound.getName());
    }

    @Test
    public void testGetPackageTypeNotExitingName(){

        String name = "Couple outing";

        PackageType packageType = new PackageType();
        packageType.setName(name);

        PackageType packageTypeCreate = packageTypeRepository.save(packageType);

        Assertions.assertNotNull(packageTypeCreate);
        Assertions.assertNotNull(packageTypeCreate.getId());

        Optional<PackageType> packageTypeOptional = packageTypeRepository.findByName("bjhhijikjg");
        Assertions.assertFalse(packageTypeOptional.isPresent());
    }
}

