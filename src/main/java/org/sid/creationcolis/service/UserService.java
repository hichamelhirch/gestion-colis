package org.sid.creationcolis.service;

import org.sid.creationcolis.dtos.UserDTO;
import org.sid.creationcolis.entities.User;

import org.sid.creationcolis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByClientId(Long clientId) {
        return userRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("User not found for client with id " + clientId));
    }
}
