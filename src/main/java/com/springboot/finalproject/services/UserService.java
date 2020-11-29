package com.springboot.finalproject.services;

import com.springboot.finalproject.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    boolean addUser(Users user);
    Users updateUser(Users user);
}
