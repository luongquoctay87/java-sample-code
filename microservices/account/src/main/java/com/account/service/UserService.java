package com.account.service;

import com.account.dto.request.UserCreationRequest;
import com.account.dto.request.UserUpdateRequest;
import com.account.dto.response.UserDetailResponse;
import com.account.dto.response.UserListResponse;
import com.account.exception.ResourceNotFoundException;
import com.account.model.Address;
import com.account.model.User;
import com.account.repository.SearchRepository;
import com.account.repository.UserRepository;
import com.account.repository.criteria.SearchCriteria;
import com.account.repository.specification.UserSpecificationsBuilder;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.account.util.Constant.Regex.SEARCH_OPERATOR;
import static com.account.util.Constant.Regex.SORT_OPERATOR;
import static com.account.util.SearchOperation.SIMPLE_OPERATION_SET;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

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

    }

    public void changeStatus(long id, String status) {
        log.info("Changing status user, status={}", status);

    }

    public void deleteUser(long id) {
        log.info("Processing delete user ...");
        userRepository.deleteById(id);
    }

    public UserDetailResponse getUser(int userId) {
        log.info("Processing get user ...");

        User user = get(userId);

        return UserDetailResponse.builder()
                .id(userId)
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    /**
     * Get user list has been paged
     *
     * @param pageable include pageNo, pageSize, sort
     * @return list of users
     */
    public List<UserDetailResponse> getUsers(Pageable pageable) {
        log.info("Processing get user list with pageable");
        Page<User> users = userRepository.findAll(pageable);

        return users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    /**
     * Get user list has sorted and paged
     * @param pageNo page number
     * @param pageSize size of page
     * @param sort sort by fields
     * @return list of users
     */
    public UserListResponse getUsers(int pageNo, int pageSize, String... sort) {
        log.info("Processing get user list with pageable and sorting");

        int currentPage = pageNo;
        if (pageNo > 0) currentPage = pageNo - 1;

        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.length > 0) {
            Pattern pattern = Pattern.compile(SORT_OPERATOR);
            for (String s : sort) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    if (matcher.group(3).equalsIgnoreCase("asc"))
                        sorts.add(new Sort.Order(ASC, matcher.group(1)));
                    else
                        sorts.add(new Sort.Order(DESC, matcher.group(1)));
                }
            }
        }

        Page<User> users = userRepository.findAll(PageRequest.of(currentPage, pageSize, Sort.by(sorts)));
        int totalPage = users.getTotalPages();


        List<UserDetailResponse> userDetailResponses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();

        return UserListResponse.builder()
                .users(userDetailResponses)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(totalPage)
                .build();
    }

    public List<UserDetailResponse> searchWithCriteria(String... search) {
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

        return users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    public List<UserDetailResponse> findAllBySpecification(String... search) {
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

        return users.stream().map(user -> UserDetailResponse.builder()
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
