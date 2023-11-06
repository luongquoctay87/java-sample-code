### Search - Sort - Paging by Creteria

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

#### 3. Create repository `SearchRepository`

```
@Component
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Search user by criteria
     *
     * @param params list of filter conditions
     * @return list of users
     */
    public Page<User> findAllUsersByCriteria(Pageable pageable, List<SearchCriteria> params) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> r = query.from(User.class);

        Predicate predicate = builder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        // This query fetches the Users as per the Page Limit
         List<User> userList = entityManager.createQuery(query)
                 .setFirstResult((int) pageable.getOffset())
                 .setMaxResults(pageable.getPageSize())
                 .getResultList();

        // Create count query
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<User> usersRootCount = countQuery.from(User.class);
        countQuery.select(builder.count(usersRootCount)).where(predicate);

        // Fetches the count of all User as per given criteria
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(userList, pageable, count);
    }
}
```

#### 4. Create service `UserService`

- UserService
```
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final SearchRepository searchRepository;

    /**
     * Get user list has been page, sorted and filtered.
     *
     * @param pageable includes page, size and sort
     * @param search   array of filters
     * @return list of users
     */
    public UserListResponse getUsersByCriteria(Pageable pageable, String... search) {
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

        Page<User> users = searchRepository.findAllUsersByCriteria(pageable, params);

        return toUserList(users);
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


- SearchCriteria

```
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
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

    @Operation(summary = "Search user with criteria", description = "Return list of users")
    @GetMapping(path = "/search-with-criteria", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public UserListResponse searchWithCriteria(Pageable pageable, @RequestParam String... search) {
        return userService.getUsersByCriteria(pageable, search);
    }
}
```

#### 5. Test

```
curl --location 'http://localhost:7750/users/search-with-criteria?search=firstName%3AT%C3%A2y%2ClastName%3AL%C6%B0%C6%A1ng%20Qu%E1%BB%91c&page=0&size=20&sort=firstName%3Aasc'
```


---
***Source Reference:***
- [REST Query Language with Spring and JPA Criteria](https://www.baeldung.com/rest-search-language-spring-jpa-criteria)
- [Advanced Search and Filtering using Spring Data JPA Specification and Criteria API](https://medium.com/@cmmapada/advanced-search-and-filtering-using-spring-data-jpa-specification-and-criteria-api-b6e8f891f2bf)
