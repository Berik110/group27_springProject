package com.springboot.finalproject.repositories;

import com.springboot.finalproject.entities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PicturesRepository extends JpaRepository<Pictures, Long> {
}
