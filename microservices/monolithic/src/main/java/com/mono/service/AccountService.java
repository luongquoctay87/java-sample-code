package com.mono.service;

import com.mono.dto.request.UserCreationRequest;
import com.mono.dto.request.UserUpdateRequest;
import com.mono.dto.response.UserDetailResponse;
import com.mono.dto.response.UserListResponse;
import com.mono.exception.ResourceNotFoundException;
import com.mono.model.User;
import com.mono.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public record AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    /**
     * Add user
     *
     * @param req
     * @return
     */
    public long addUser(UserCreationRequest req) {
        log.info("Processing save user ...");

        User user = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        User result = userRepository.save(user);
        log.info("User saved successfully");
        return result.getId();
    }

    /**
     * Update user
     *
     * @param req
     */
    public void updateUser(UserUpdateRequest req) {
        log.info("Processing update user ...");

        if (req.getId() != 1) {
            User user = get(req.getId());
            if (StringUtils.hasLength(req.getUsername())) {
                user.setUsername(req.getUsername());
            }
            if (StringUtils.hasLength(req.getPassword())) {
                user.setPassword(passwordEncoder.encode(req.getPassword()));
            }
            if (req.getEnabled() != null) {
                user.setEnabled(req.getEnabled());
            }

            userRepository.save(user);
            log.info("User updated successfully");
        }
    }

    /**
     * Permit user by id
     *
     * @param userId
     */
    public void permitUser(long userId, boolean status) {
        log.info("Processing disable user ...");

        User user = get(userId);
        user.setEnabled(status);
        userRepository.delete(user);
        log.info("User disabled successfully");
    }

    /**
     * Delete user permanent
     *
     * @param userId
     */
    public void deleteUser(long userId) {
        log.info("Processing delete user ...");

        User user = get(userId);
        userRepository.delete(user);
        log.info("User deleted successfully");
    }

    /**
     * Get user detail
     *
     * @param userId
     * @return
     */
    public UserDetailResponse getUser(long userId) {
        log.info("Processing get user detail ...");

        User user = get(userId);
        log.info("Retrieved User successfully");
        return UserDetailResponse.builder()
                .id(userId)
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    /**
     * Get all of users
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public UserListResponse getAll(int pageNo, int pageSize) {
        log.info("Processing get user list ...");

        int page = 0;
        if (pageNo > 0) page = pageNo - 1;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> responses = users.getContent().stream().map(x -> UserDetailResponse.builder()
                .id(x.getId())
                .email(x.getEmail())
                .username(x.getUsername())
                .build()).toList();

        UserListResponse response = new UserListResponse();
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotal(users.getTotalElements());
        response.setData(responses);
        log.info("Retrieved users successfully");
        return response;
    }

    /**
     * Search by username
     *
     * @param username
     * @param pageNo
     * @param pageSize
     * @return
     */
    public UserListResponse search(String username, int pageNo, int pageSize) {
        log.info("Searching user by username={}", username);

        int page = 0;
        if (pageNo > 0) page = pageNo - 1;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> users = userRepository.searchByUsername(username, pageable);

        List<UserDetailResponse> responses = users.getContent().stream().map(x -> UserDetailResponse.builder()
                .email(x.getEmail())
                .username(x.getUsername())
                .build()).toList();

        UserListResponse response = new UserListResponse();
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotal(users.getTotalElements());
        response.setData(responses);

        log.info("Retrieved users successfully");

        return response;
    }

    /**
     * Get user
     *
     * @param userId
     * @return
     */
    private User get(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
