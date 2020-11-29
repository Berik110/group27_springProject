package com.springboot.finalproject.services.impl;

import com.springboot.finalproject.entities.Users;
import com.springboot.finalproject.repositories.UsersRepository;
import com.springboot.finalproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@EnableWebSecurity
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Users myUser = usersRepository.findByEmail(s);

        if (myUser!=null){

            User secUser = new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
            return secUser;

        }

        throw new UsernameNotFoundException("User name not found");
    }

    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public boolean addUser(Users user) {

        Users checkUser = usersRepository.findByEmail(user.getEmail());

        if (checkUser==null){
            usersRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public Users updateUser(Users user) {
        return  usersRepository.save(user);
    }
}
