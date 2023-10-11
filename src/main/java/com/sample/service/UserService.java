package com.sample.service;

import com.sample.model.SampleUser;
import com.sample.repository.SampleUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserService implements UserDetailsService {

    private final SampleUserRepository userRepository;

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SampleUser user = userRepository.findByEmail(username);
        if (user == null) {
            log.warn("User not found in the database");
            throw new UsernameNotFoundException("User not found");
        }

        return new User(user.getEmail(), user.getPassword(), getAuthority(username));
    }

    private List<SimpleGrantedAuthority> getAuthority(String username) {
        // Todo: get authorities from database
        return List.of(new SimpleGrantedAuthority("Admin"));
    }
}
