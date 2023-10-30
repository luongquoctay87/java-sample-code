package com.account.service;

import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserResponse;
import com.account.exception.ResourceNotFoundException;
import com.account.model.Address;
import com.account.model.User;
import com.account.repository.SearchRepository;
import com.account.repository.UserRepository;
import com.account.repository.criteria.SearchCriteria;
import com.account.repository.specification.UserSpecificationsBuilder;
import com.account.util.Constant;
import com.account.util.SearchOperation;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.account.util.Constant.Regex.SEARCH_OPERATOR;
import static com.account.util.SearchOperation.SIMPLE_OPERATION_SET;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    public long addUser(UserCreationRequest req) {
        log.info("Processing add user ...");

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setGender(req.getGender());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        req.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())

        );

        User result = userRepository.save(user);

        log.info("User added successfully");

        return result.getId();
    }

    public void updateUser(UserUpdateRequest req) {
        log.info("Processing update user ...");
        if (req.getId() != 1) {
            //User user = new User();
        }
    }

    public void deleteUser(long id) {
        log.info("Processing delete user ...");
        userRepository.deleteById(id);
    }

    public UserResponse getUser(int userId) {
        log.info("Processing get user ...");

        User user = get(userId);

        return UserResponse.builder()
                .id(userId)
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    public List<UserResponse> getUsers() {
        log.info("Processing get user list ...");
        List<User> users = (List<User>) userRepository.findAll();

        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    public List<UserResponse> searchWithCriteria(String... search) {
        List<SearchCriteria> params = new ArrayList<>();

        if (search.length > 0) {
            Pattern pattern = Pattern.compile(SEARCH_OPERATOR);
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        List<User> users = searchRepository.searchUserByCriteria(params);

        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    public List<UserResponse> findAllBySpecification(String ... search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operationSetExper = Joiner.on("|").join(SIMPLE_OPERATION_SET);

        if (search.length > 0) {
            Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(.*)(\\p{Punct}?)");
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
                }
            }
        }

        Specification<User> spec = builder.build();
        List<User> users = userRepository.findAll(spec);

        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    private User get(long id) {
        log.info("Retrieving User from database");
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found user"));
    }

}
