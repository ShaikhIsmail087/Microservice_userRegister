package com.Iwcd.user.service.services.impl;

import com.Iwcd.user.service.entities.Rating;
import com.Iwcd.user.service.entities.User;
import com.Iwcd.user.service.exceptions.ResourceNotFoundException;
import com.Iwcd.user.service.repositories.UserRepository;
import com.Iwcd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

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

    //get single user
    @Override
    public User getUser(String userId)
    {
        //get user from database with the help of user repository
        User user= userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user with given id is not found on server!! : "+userId));
        //fetch rating of the above user from RATING SERVICE
        //http://localhost:8083/ratings/users/362ca759-afff-4dd0-b3ea-255c01d71daa
        ArrayList<Rating> ratingsOfUser= restTemplate.getForObject("http://localhost:8083/ratings/users/"+user.getUserId(), ArrayList.class);
        logger.info("{}",ratingsOfUser);

        user.setRatings(ratingsOfUser);

        return user;
    }
}
