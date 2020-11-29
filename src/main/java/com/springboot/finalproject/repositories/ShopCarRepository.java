package com.springboot.finalproject.repositories;

import com.springboot.finalproject.entities.ShopCars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ShopCarRepository extends JpaRepository<ShopCars, Long>, JpaSpecificationExecutor<ShopCars> {

    List<ShopCars> findAllByPriceGreaterThanEqual(double price);

    ShopCars findByPriceGreaterThanEqualAndId(double price, Long id);

    List<ShopCars> findAllByNameLikeAndPriceGreaterThanEqualOrderByPriceAsc(String name, double price);
}
