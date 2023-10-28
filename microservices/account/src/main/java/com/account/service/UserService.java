package com.account.service;

import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserResponse;
import com.account.exception.ResourceNotFoundException;
import com.account.model.Address;
import com.account.model.User;
import com.account.repository.UserRepository;
import com.account.repository.criteria.SearchCriteria;
import com.account.repository.criteria.UserSearchQueryCriteriaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public long addUser(UserCreationRequest req) {
        log.info("Processing add user ...");

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setDateOfBirth(req.getDateOfBirth());
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
            User user = new User();
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

    public List<User> getUserByCriteria(List<SearchCriteria> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root r = query.from(User.class);

        Predicate predicate = builder.conjunction();

        UserSearchQueryCriteriaConsumer searchConsumer =
                new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.stream().forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        List<User> result = entityManager.createQuery(query).getResultList();
        return result;
    }

    private User get(long id) {
        log.info("Retrieving User from database");
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found user"));
    }

}
