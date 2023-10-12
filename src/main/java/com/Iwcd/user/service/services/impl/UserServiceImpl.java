package com.Iwcd.user.service.services.impl;

import com.Iwcd.user.service.entities.User;
import com.Iwcd.user.service.exceptions.ResourceNotFoundException;
import com.Iwcd.user.service.repositories.UserRepository;
import com.Iwcd.user.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user)
    {
        //generate unique userid
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser()
    {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId)
    {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user with given id is not found on server!! : "+userId));
    }
}
