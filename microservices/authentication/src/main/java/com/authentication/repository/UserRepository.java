package com.authentication.repository;

import com.authentication.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Repository
@Slf4j(topic = "SAMPLE-USER-REPOSITORY")
public class UserRepository {

    /**
     * Find sample user by email
     *
     * @param email
     * @return SampleUser
     */
    public User findByEmail(String email) {
        return User.builder()
                .id(1L)
                .fistName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .phone("0123456789")
                .username("admin")
                .password("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6")
                .build();
    }

}
