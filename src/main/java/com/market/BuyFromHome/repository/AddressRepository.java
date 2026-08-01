package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    boolean existsByUser_UserIdAndStreetAddressIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCase(
            Long userId,
            String streetAddress,
            String city,
            String state,
            String country
    );

    List<Address> findByUser_UserId(Long userId);

}
