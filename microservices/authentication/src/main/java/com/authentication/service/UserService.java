package com.authentication.service;

import com.authentication.model.User;
import com.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @param username
     * @return user detail
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            log.warn("User not found in the database");
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority(username));
    }

    private List<SimpleGrantedAuthority> getAuthority(String username) {
        return List.of(new SimpleGrantedAuthority("admin"), new SimpleGrantedAuthority("manager"));
    }
}
