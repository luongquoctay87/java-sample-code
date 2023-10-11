package com.account.service;

import com.account.dto.Address;
import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserResponse;
import com.account.repository.UserRepository;
import com.library.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public record UserService(UserRepository userRepository) {

    public long addUser(UserCreationRequest req) {
        log.info("Processing add user ...");
        User user = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .password(req.getPassword())
                .build();
        User result = userRepository.save(user);
        return result.getId();
    }

    public void updateUser(UserUpdateRequest req) {
        log.info("Processing update user ...");
        if (req.getId() != 1) {
            User user = new User();
        }
    }

    public void deleteUser(int userId) {
        log.info("Processing delete user ...");
    }

    public UserResponse getUser(int userId) {
        log.info("Processing get user ...");
        return UserResponse.builder()
                .id(userId)
                .fullName("John Doe")
                .email("someone@email.com")
                .phone("0975-118-228")
                .address(Address.builder().street("Pham Van Dong").city("Hanoi").state("Hanoi").zipCode("100000").build())
                .build();
    }

    public List<UserResponse> getUsers() {
        log.info("Processing get user list ...");
        List<UserResponse> responses = new ArrayList<>();
        responses.add(UserResponse.builder()
                .id(1)
                .fullName("John Doe")
                .email("someone@email.com")
                .phone("0987-111-222")
                .address(Address.builder().street("1 Pham Van Dong").city("Hanoi").state("Hanoi").zipCode("100000").build())
                .build());
        responses.add(UserResponse.builder()
                .id(2)
                .fullName("Joe Biden")
                .email("joebiden@email.com")
                .phone("0987-112-113")
                .address(Address.builder().street("2 Pham Van Dong").city("Hanoi").state("Hanoi").zipCode("100000").build())
                .build());

        return responses;
    }
}
