# Cache Data In Redis Cluster

### 1. Setting up a Redis Cluster
[Setting up a Redis Cluster for scalability and high availability](https://aws.amazon.com/getting-started/hands-on/setting-up-a-redis-cluster-with-amazon-elasticache/)

### 2. Connect Redis Cluster In Java

- Add dependency

```
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.9.0</version>
    <optional>true</optional>
</dependency>
```

- Config Redis Cluster in `application.yml`

```
redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
```

- Connect Redis Cluster Locally in `RedisLocallyConfig.java` for DEV environment

```
@Configuration
@EnableRedisRepositories
@Slf4j(topic = "REDIS-LOCALLY-CONFIG")
@Profile({"dev"})
public class RedisLocallyConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        log.info("Connecting Redis Cluster Locally [{}:{}]", redisHost, redisPort);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        log.info("Redis Cluster Locally connected!");
        return template;
    }
}
```

- Connect Amazon Redis Cluster in `RedisClusterConfig.java` for PRODUCT environment

```
@Configuration
@EnableRedisRepositories
@Slf4j(topic = "REDIS-CLUSTER")
@Profile({"prod"})
public class RedisClusterConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        log.info("Connecting Amazon Redis Cluster [{}:{}]", redisHost, redisPort);

        String ipAddress = "";
        try {
            InetAddress inetAddress = InetAddress.getByName(redisHost);
            ipAddress = inetAddress.getHostAddress();
        } catch (Exception e) {
            log.info("Error connection, message={}", e.getMessage(), e);
        }
        log.info("Ip address : {} ", ipAddress);
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        List<RedisNode> redisNodes = List.of(new RedisNode(ipAddress, redisPort));
        redisClusterConfiguration.setClusterNodes(redisNodes);
        redisClusterConfiguration.setMaxRedirects(3);
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(20);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxTotal(50);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
        jedisConnectionFactory.afterPropertiesSet();

        log.info("Amazon Redis Cluster connected!");
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

- Create model

```
@RedisHash("Token")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token implements Serializable {
    private String id;
    private String accessToken;
    private String refreshToken;
    private Date expiryAccessDate;
    private Date expiryRefreshDate;
}
```

- Create repository

```
@Repository
public interface TokenRepository extends CrudRepository<Token,String> {
}
```

- Create service

```
@Service
public record TokenService(TokenRepository tokenRepository) {

    public void save(Token token) {
        tokenRepository.save(token);
    }

    public Token getById(String id) {
        Optional<Token> token = tokenRepository.findById(id);
        return token.orElse(null);
    }

    public void delete(String id) {
        tokenRepository.deleteById(id);
    }
}
```