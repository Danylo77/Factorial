package com.example.mainserver.service;

import com.example.mainserver.entity.User;
import com.example.mainserver.dto.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);


    List<UserDto> findAllUsers();
}
