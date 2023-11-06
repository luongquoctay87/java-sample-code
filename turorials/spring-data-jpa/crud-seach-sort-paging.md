### Working with spring data JPA

#### 1. Add dependency
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 2. Create model `User`

```
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

//    @Column(name = "status")
//    private UserStatus status;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

    public void saveAddress(Address address) {

        if (address != null) {
            if (addresses == null) {
                addresses = new HashSet<>();
            }
            addresses.add(address);
            address.setUser(this); // save user_id
        }
    }
}
```

#### 3. Create repository `UserRepository`

- UserRepository

```
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    User findByEmail(String email);
}
```


#### 3. Create service `UserService`

```
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    /**
     * Create new user
     *
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
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

    /**
     * Update a user
     *
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest req) {
        log.info("Processing update user ...");

    }

    /**
     * Change user active or inactive
     *
     * @param id
     * @param status
     */
    public void changeStatus(long id, String status) {
        log.info("Changing status user, status={}", status);

    }

    /**
     * Delete user permanently
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(long id) {
        log.info("Processing delete user ...");
        userRepository.deleteById(id);
    }

    public UserDetailResponse getUser(int userId) {
        log.info("Processing get user ...");

        User user = get(userId);

        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName() + " " + user.getLastName())
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
                .firstName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();
    }

    /**
     * Get user list has been sorted and paged
     *
     * @param pageNo   page number
     * @param pageSize size of page
     * @param sort     sort by fields
     * @return list of users
     */
    public UserListResponse getUsers(int pageNo, int pageSize, String... sort) {
        log.info("Getting user list with pageable and sorting");

        int currentPage = pageNo;
        if (pageNo > 0) currentPage = pageNo - 1;

        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.length > 0) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
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

        return toUserList(users);
    }

    /**
     * Get user by ID
     *
     * @param id
     * @return
     */
    private User get(long id) {
        log.info("Retrieving User from database");
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found user"));
    }

    /**
     * Covert to UserListResponse
     *
     * @param users
     * @return
     */
    private UserListResponse toUserList(Page<User> users) {
        List<UserDetailResponse> list = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()
        ).toList();

        return UserListResponse.builder()
                .users(list)
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalPage(users.getTotalPages())
                .build();
    }
}
```


#### 4. Create controller `AccountController`

```
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountController {

    private final UserService userService;

    @Operation(summary = "Add new user", description = "Return user ID")
    @PostMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public long createUser(@Valid @RequestBody UserCreationRequest request) {
        return userService.addUser(request);
    }

    @Operation(summary = "Update user", description = "Return message")
    @PutMapping(path = "/", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void updateUser(@Valid @RequestBody UserUpdateRequest request) {
        try {
            userService.updateUser(request);
        } catch (Exception e) {
            throw new InvalidDataException("Update user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Change user status", description = "Return message")
    @PatchMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void changeStatus(@PathVariable long id,
                             @RequestParam @ValueOfEnum(message = "status must be any of enum (ACTIVE,INACTIVE,NONE)", enumClass = UserStatus.class) String status) {
        try {
            userService.changeStatus(id, status);
        } catch (Exception e) {
            throw new InvalidDataException("Change status unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Delete user", description = "Return no content")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@PathVariable("id") @Min(1) long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new InvalidDataException("Delete user unsuccessful, Please try again");
        }
    }

    @Operation(summary = "Get user detail", description = "Return user detail")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserDetailResponse getUser(@PathVariable("id") @Min(1) int id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Get user list has been paged", description = "Return list of users")
    @GetMapping(path = "/list", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<UserDetailResponse> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @Operation(summary = "Get user list has been sorted and paged", description = "Return list of users")
    @GetMapping(path = "/list-sorted-paged", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUsers(@RequestParam(defaultValue = "0") int pageNo,
                                     @RequestParam(defaultValue = "20") int pageSize,
                                     @RequestParam(required = false) String... sort) {
        return userService.getUsers(pageNo, pageSize, sort);
    }
}
```

---
***Source Reference:***
- [Spring Boot Pagination & Filter example | Spring JPA, Pageable](https://www.bezkoder.com/spring-boot-pagination-filter-jpa-pageable/)
- [Pagination and Sorting using Spring Data JPA](https://www.baeldung.com/spring-data-jpa-pagination-sorting)
