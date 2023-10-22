package com.Iwcd.user.service.services.impl;

import com.Iwcd.user.service.entities.Hotel;
import com.Iwcd.user.service.entities.Rating;
import com.Iwcd.user.service.entities.User;
import com.Iwcd.user.service.exceptions.ResourceNotFoundException;
import com.Iwcd.user.service.external.services.HotelService;
import com.Iwcd.user.service.repositories.UserRepository;
import com.Iwcd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelService hotelService;

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
        Rating[] ratingsOfUser= restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        logger.info("{}",ratingsOfUser);

        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList= ratings.stream().map(rating -> {
            //api call to hotel service to get the hotel
            //http://localhost:8082/hotels/ba8f1c60-5569-45a9-82d7-399b3287050f
//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//            Hotel hotel = forEntity.getBody();
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
//            logger.info("response status code: {} ",forEntity.getStatusCode());

            //set the hotel to rating
            rating.setHotel(hotel);
            //return the rating
            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);

        return user;
    }
}
