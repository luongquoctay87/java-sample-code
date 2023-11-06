## Search - Sort - Paging by 

### 1. Add dependency
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

### 2. Create model `User`

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

### 3. Create repository `SearchRepository`

```
@Component
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find users by customize JPA query
     *
     * @param firstName
     * @param lastName
     * @param gender
     * @param pageNo
     * @param pageSize
     * @return list of users
     */
    public Page<UserDetailResponse> findAllUsersByCustomizeQuery(String firstName, String lastName, Integer gender, int pageNo, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");

        if (StringUtils.hasLength(firstName)) {
            where.append(" AND u.firstName=:firstName");
        }
        if (StringUtils.hasLength(lastName)) {
            where.append(" AND u.lastName=:lastName");
        }
        if (null != gender) {
            where.append(" AND u.gender=:gender");
        }

        // Get list of users
        Query selectQuery = entityManager.createQuery(String.format("SELECT new com.account.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) FROM User u %s ORDER BY u.id DESC", where));
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (where.toString().contains("AND")) {
            if (StringUtils.hasLength(firstName)) {
                selectQuery.setParameter(":firstName", firstName);
            }
            if (StringUtils.hasLength(lastName)) {
                selectQuery.setParameter(":lastName", lastName);
            }
            if (null != gender) {
                selectQuery.setParameter(":gender", gender);
            }
        }
        List<UserDetailResponse> userList = selectQuery.getResultList();

        // Count users
        Long count = (Long) entityManager.createQuery(String.format("SELECT COUNT(*) FROM User u %s", where)).getSingleResult();

        return new PageImpl<>(userList, PageRequest.of(pageNo,pageSize), count);
    }
}
```

### 4. Create service `UserService`

```
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final SearchRepository searchRepository;

    /**
     * Get list of users by
     * @param firstName
     * @param lastName
     * @param gender
     * @param pageNo
     * @param pageSize
     * @return
     */
    public UserListResponse getUsersByCustomizeQuery(String firstName, String lastName, Integer gender, int pageNo, int pageSize) {
        Page<UserDetailResponse> users = searchRepository.findAllUsersByCustomizeQuery(firstName, lastName, gender, pageNo, pageSize);
        return UserListResponse.builder()
                .users(users.stream().toList())
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalPage(users.getTotalPages())
                .build();
    }
}
```

### 5. Create controller `AccountController`

```
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AccountController {

    private final UserService userService;

    @Operation(summary = "Get user list has been sorted and paged by customize query", description = "Return list of users")
    @GetMapping(path = "/list-sorted-paged-by-customize-query", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse getUsersByCustomizeQuery(@RequestParam(required = false) String firstName,
                                                  @RequestParam(required = false) String lastName,
                                                  @RequestParam(required = false) Integer gender,
                                                  @RequestParam(defaultValue = "0") int pageNo,
                                                  @RequestParam(defaultValue = "20") int pageSize                                                  ) {
        return userService.getUsersByCustomizeQuery(firstName, lastName, gender, pageNo, pageSize);
    }
}
```