package org.example.service;

import org.example.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

//test中注入调试
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void testListAll(){
        List<User> list=userService.selectAll();
    }

    @Test
    public void testInsert(){
        String name="lucky";
        if(userService.selectByName(name)!=null){
            System.out.println(name+" is exist");
            return;
        }
        User user = new User();
        user.setUserName(name);
        user.setUserPassword("password");
        user.setUserEmail("@qq.com");
        System.out.println(userService.insert(user));
    }
}
