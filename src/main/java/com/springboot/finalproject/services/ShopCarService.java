package com.springboot.finalproject.services;

import com.springboot.finalproject.entities.Countries;
import com.springboot.finalproject.entities.ShopCars;

import java.util.List;

public interface ShopCarService {

    ShopCars addShopCar(ShopCars shopCar);
    ShopCars saveShopCar(ShopCars shopCar);
    List<ShopCars> getAllShopCars();
    ShopCars getShopCar(Long id);
    void deleteShopCar(ShopCars shopCar);
    List<ShopCars> searchShopCars(String name);

    Countries getCountry(Long id);
    List<Countries> getAllCountries();
    void deleteCountry(Countries country);
    Countries saveCountry(Countries country);
    Countries addCountry(Countries country);

    List<ShopCars> searchCars(String name, Double priceFrom, Double priceTo, Integer yearFrom, Integer yearTo);
}
