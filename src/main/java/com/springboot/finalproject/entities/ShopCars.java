package com.springboot.finalproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_cars")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "car_name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "year")
    private int year;

    @Column(name = "tel")
    private String tel;

    @Column(name = "img_url")
    private String imgUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    private Countries country;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Pictures> pictures;

}
