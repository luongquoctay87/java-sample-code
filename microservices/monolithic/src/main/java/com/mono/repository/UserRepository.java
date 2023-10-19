package com.mono.repository;

import com.mono.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Query(value = "FROM User u WHERE LOWER(u.username) LIKE LOWER(:username) AND u.enabled=true ORDER BY u.id")
    Page<User> searchByUsername(String username, Pageable pageable);
}
