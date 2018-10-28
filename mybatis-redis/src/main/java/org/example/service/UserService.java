package org.example.service;

import org.example.model.User;

import java.util.List;

public interface UserService {
    int insert(User user);
    List<User> selectAll();
    User selectByName(String name);
}
