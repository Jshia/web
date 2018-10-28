package org.example.service.serviceIpml;

import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //清空user下缓存
    //@CacheEvict(value = "user", allEntries = true)
    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    //@Cacheable(value = "user", key = "'selectByName('+#name+')'")
    @Override
    public User selectByName(String name) {
        return userMapper.selectByName(name);
    }

    //@Cacheable(value = "user", key = "'selectAll'")
    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }
}
