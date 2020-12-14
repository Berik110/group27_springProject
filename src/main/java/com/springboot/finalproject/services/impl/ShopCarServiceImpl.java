package com.springboot.finalproject.services.impl;

import com.springboot.finalproject.entities.Countries;
import com.springboot.finalproject.entities.Pictures;
import com.springboot.finalproject.entities.ShopCars;
import com.springboot.finalproject.repositories.CountriesRepository;
import com.springboot.finalproject.repositories.PicturesRepository;
import com.springboot.finalproject.repositories.ShopCarRepository;
import com.springboot.finalproject.services.ShopCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopCarServiceImpl implements ShopCarService {

    @Autowired
    private ShopCarRepository shopCarRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private PicturesRepository picturesRepository;



    @Override
    public ShopCars addShopCar(ShopCars shopCar) {
        return shopCarRepository.save(shopCar);
    }

    @Override
    public ShopCars saveShopCar(ShopCars shopCar) {
        return shopCarRepository.save(shopCar);
    }

    @Override
    public List<ShopCars> getAllShopCars() {
        return shopCarRepository.findAllByPriceGreaterThanEqual(0);
    }

    @Override
    public ShopCars getShopCar(Long id) {
        return shopCarRepository.findByPriceGreaterThanEqualAndId(0, id);
    }

    @Override
    public void deleteShopCar(ShopCars shopCar) {
        shopCarRepository.delete(shopCar);
    }

    @Override
    public List<ShopCars> searchShopCars(String name) {
        return shopCarRepository.findAllByNameLikeAndPriceGreaterThanEqualOrderByPriceAsc("%"+name+"%", 0);
    }

    @Override
    public Countries getCountry(Long id) {
        return countriesRepository.getOne(id);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countriesRepository.findAll();
    }

    @Override
    public void deleteCountry(Countries country) {
        countriesRepository.delete(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countriesRepository.save(country);
    }

    @Override
    public Countries addCountry(Countries country) {
        return countriesRepository.save(country);
    }

    @Override
    public List<ShopCars> searchCars(String name, Double priceFrom, Double priceTo, Integer yearFrom, Integer yearTo) {

        Specification specification = (Specification<ShopCars>)(root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%"+name.toUpperCase()+"%");

        if (priceFrom!=null){
            specification = specification.and((Specification<ShopCars>)(root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceFrom));
        }

        if (priceTo!=null){
            specification = specification.and((Specification<ShopCars>)(root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceTo));
        }

        if (yearFrom!=null){
            specification = specification.and((Specification<ShopCars>)(root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("year"), yearFrom));
        }

        if (yearTo!=null){
            specification = specification.and((Specification<ShopCars>)(root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("year"), yearTo));
        }

        return shopCarRepository.findAll(specification);
    }

    @Override
    public List<Pictures> getAllPictures() {
        return picturesRepository.findAll();
    }

    @Override
    public Pictures getPicture(Long id) {
        return picturesRepository.getOne(id);
    }
}
