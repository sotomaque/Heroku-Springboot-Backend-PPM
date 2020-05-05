package com.sotomaque.ppmtool.services;

import com.sotomaque.ppmtool.domain.User;
import com.sotomaque.ppmtool.exceptions.UsernameAlreadyRegisteredException;
import com.sotomaque.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ensure we don't store anything that's not encrypted
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            // ensure username has to be unique: (custom exception)
            newUser.setUsername(newUser.getUsername());
            // ensure password and confirm password match
            // don't persist / show confirm password
            newUser.setConfirmPassword("");
            return userRepository.save(newUser);
        } catch (Exception e){
            throw new UsernameAlreadyRegisteredException("Username: " + newUser.getUsername() + " is Already Registered");
        }

    }


}
