# Read Json File

### 1. Add dependency
```
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>
```

### 2. Add countries.json to folder `resources/template`
```
{
  "countries": [
    {
      "code": "AF",
      "name": "Afghanistan",
      "currency": "AFN",
      "population": "29121286",
      "capital": "Kabul",
      "continent": "Asia"
    },
    {
      "code": "AL",
      "name": "Albania",
      "currency": "ALL",
      "population": "2986952",
      "capital": "Tirana",
      "continent": "Europe"
    },
    ...
  ]
}
```

***Reference Source:***
- [countries.json](https://gist.github.com/tiagodealmeida/0b97ccf117252d742dddf098bc6cc58a)
- [country.json - lipis/flag-icons](https://github.com/lipis/flag-icons/blob/main/country.json)


### 3. Add new model Country.java
```
@Data
public class Country implements Serializable {

    private String code;

    private String name;

    private String currency;

    @JsonIgnoreProperties
    private Integer population;

    @JsonIgnoreProperties
    private String capital;

    private String continent;
}
```

### 4. Add new dto CountryDTO.java
```
@Data
@Builder
public class CountryDTO implements Serializable {
    private String countryCode;
    private String countryName;
    private String currencyCode;
}
```


### 5. Add new service `CommonService.java`

```
@Service
@Slf4j
public class CommonService {

    /**
     * Get list of countries
     *
     * @return countries
     */
    public List<CountryDTO> getCountryList() {
        log.info("Get country list");

        List<Country> countryList = readCountryJsonFile();
        return countryList.stream().map(country -> CountryDTO.builder()
                .countryCode(country.getCode())
                .countryName(country.getName())
                .currencyCode(country.getCurrency())
                .build()).toList();
    }

    /**
     * Get country name by code
     *
     * @return country name
     */
    public CountryDTO getCountryByCode(String code) {
        log.info("Finding country by code");

        List<Country> countryList = readCountryJsonFile();
        List<Country> countries = countryList.stream().filter(country -> country.getCode().equals(code)).toList();
        if (!countries.isEmpty()){
            Country country = countries.get(0);
            return CountryDTO.builder()
                    .countryCode(country.getCode())
                    .countryName(country.getName())
                    .currencyCode(country.getCurrency())
                    .build();
        }

        throw new NotFoundException(String.format("Not found country with countryCode={}", code));
    }

    /**
     * Read json file countries.json
     *
     * @return country list
     */
    private List<Country> readCountryJsonFile() {
        log.info("Processing read json file");

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream file = classloader.getResourceAsStream("template/countries.json");
            assert file != null;
            Reader reader = new InputStreamReader(file);

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);

            String countries = jsonObject.get("countries").toString();

            ObjectMapper mapper = new ObjectMapper();

            return Arrays.asList(mapper.readValue(countries, Country[].class));
        } catch (Exception e) {
            log.error("Can not read json file, message={}", e.getMessage(), e);
            throw new NotFoundException("Get country list was failure");
        }
    }
}
```


### 6. Add new controller `CommonController.java`
```
@RestController
@RequestMapping("/common")
@Slf4j(topic = "COMMON-CONTROLLER")
public record CommonController(CommonService commonService) {

    @GetMapping(path = "/countries", headers = apiKey)
    public SuccessResponse getCountries() {
        log.info("Request POST /common/countries");

        try {
            List<CountryDTO> countryList = commonService.getCountryList();
            return new SuccessResponse(OK, "countries", countryList);
        } catch (NotFoundException e) {
            log.error("Get country list was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(path = "/countries/country/{countryCode}", headers = apiKey)
    public SuccessResponse getCountry(@PathVariable String countryCode) {
        log.info("Request POST /countries/country/{}", countryCode);

        try {
            CountryDTO response = commonService.getCountryByCode(countryCode);
            return new SuccessResponse(OK, "country", response);
        } catch (NotFoundException e) {
            log.error("Get country was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, e.getMessage());
        }
    }
}
```


### 7. Test API
- Get all countries
```
curl --location 'http://localhost:8181/common/countries' \
--header 'Accept-Language: en-US' \
--header 'apiKey: sample'
```

- Get country by countryCode
```
curl --location 'http://localhost:8181/common/countries/country/AF' \
--header 'Accept-Language: en-US' \
--header 'apiKey: sample'
```


***Github Source:***
- []()

***Reference Source:***
- [Read and Write JSON](https://howtodoinjava.com/java/library/json-simple-read-write-json-examples/)
