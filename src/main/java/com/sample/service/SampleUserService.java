package com.sample.service;

import com.sample.config.Translator;
import com.sample.controller.request.MobileSignUpRequest;
import com.sample.controller.request.SampleUserRequest;
import com.sample.controller.response.LoadingPageResponse;
import com.sample.exception.DuplicatedException;
import com.sample.exception.NotAcceptableException;
import com.sample.exception.NotFoundException;
import com.sample.model.SampleUser;
import com.sample.repository.SampleUserRepository;
import com.sample.utils.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import static com.sample.utils.Constant.REGEX.PHONE;
import static com.sample.utils.Constant.TOPIC.WELCOME_TOPIC;

@Service
@Slf4j(topic = "SAMPLE-USER-SERVICE")
public record SampleUserService(SampleUserRepository repository, KafkaTemplate<String, String> kafkaTemplate, PasswordEncoder passwordEncoder) {

    /**
     * Add new sample user
     *
     * @param request
     * @return
     */
    public String saveSampleUser(SampleUserRequest request) {
        log.info("Saving sample user ...");

        String searchKeys = String.format("%s %s, %s, %s, %s", request.getFirstName(), request.getLastName(), request.getPhone(), request.getEmail(), request.getAddress());
        SampleUser object = SampleUser.builder()
                .hashKey("uuid")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder().encode(request.getPassword()))
                .address(request.getAddress())
                .status(UserStatus.NONE)
                .isFirstLogin(true)
                .searchKeys(searchKeys)
                .build();

        SampleUser response = repository.save(object);
        if (response.getEmail() != null) {
            kafkaTemplate.send(WELCOME_TOPIC, response.getEmail());
        }

        return response.getRangeKey();
    }

    /**
     * Add new sample user from mobile
     *
     * @param request
     * @return
     */
    public String saveSampleUser(MobileSignUpRequest request) {
        log.info("Saving sample user ...");

        SampleUser object = SampleUser.builder()
                .hashKey("mobile_uuid")
                .phone(request.getPhone())
                .password(passwordEncoder().encode(request.getPassword()))
                .status(UserStatus.NONE)
                .isFirstLogin(true)
                .build();

        SampleUser response = repository.save(object);

        return response.getRangeKey();
    }

    /**
     * Update sample user by ID
     *
     * @param object
     * @param request
     */
    public void updateSampleUser(SampleUser object, SampleUserRequest request) {
        log.info("Updating sample user ...");

        String searchKeys = String.format("%s %s, %s, %s, %s", request.getFirstName(), request.getLastName(), request.getPhone(), request.getEmail(), request.getAddress());
        if (StringUtils.hasLength(request.getFirstName())) {
            object.setFirstName(request.getFirstName());
        }

        if (StringUtils.hasLength(request.getLastName())) {
            object.setLastName(request.getLastName());
        }

        if (StringUtils.hasLength(request.getPhone())) {
            object.setPhone(request.getPhone());
        }

        if (StringUtils.hasLength(request.getEmail())) {
            object.setEmail(request.getEmail());
        }

        if (StringUtils.hasLength(request.getPassword())) {
            object.setPassword(passwordEncoder().encode(request.getPassword()));
        }

        if (!ObjectUtils.isEmpty(request.getAddress())) {
            object.setAddress(request.getAddress());
        }

        object.setSearchKeys(searchKeys);

        repository.save(object);
    }

    /**
     * Deactivate sample user
     *
     * @param hashKey
     * @param rangeKey
     */
    public void deactivateSampleUser(String hashKey, String rangeKey) {
        log.info("Deactivating sample user ...");

        SampleUser object = repository.findByCompositeKey(hashKey, rangeKey);
        if (object == null) throw new NotFoundException("Sample user not found");
        object.setStatus(UserStatus.INACTIVE);
        repository.save(object);
    }

    /**
     * Delete permanent sample user
     *
     * @param hashKey
     * @param rangeKey
     */
    public void deleteSampleUser(String hashKey, String rangeKey) {
        log.info("Deleting sample user ...");
        repository.delete(hashKey, rangeKey);
    }

    /**
     * Get sample user by composite key
     *
     * @param hashKey
     * @param rangeKey
     * @return
     */
    public SampleUser get(String hashKey, String rangeKey) {
        log.info("Getting sample user ...");

        SampleUser object = repository.findByCompositeKey(hashKey, rangeKey);
        if (object == null) throw new NotFoundException("Sample user not found");
        return object;
    }

    /**
     * Get sample user list
     *
     * @param hashKey
     * @param search
     * @param firstName
     * @param lastName
     * @param phone
     * @param email
     * @param address
     * @param status
     * @param nextKey
     * @param pageSize
     * @return
     */
    public LoadingPageResponse getAll(String hashKey, String search, String firstName, String lastName, String phone, String email, String address, String status, String orderBy, String nextKey, int pageSize) {
        log.info("Getting list of sample user ...");

        return repository.findAll(hashKey, search, firstName, lastName, phone, email, address, status, orderBy, nextKey, pageSize);
    }

    /**
     * Check phone exists or not
     *
     * @param phone
     */
    public void isPhoneValid(String phone) {
        log.info("Validating phone number={}", phone);

        if (!phone.matches(PHONE)) throw new NotAcceptableException("Phone number invalid format");

        SampleUser object = repository.findByPhone(phone);
        if (object != null) {
            log.warn("Phone number={} exists", phone);
            throw new DuplicatedException(Translator.toLocale("msg-user-phone-invalid"));
        }
    }

    /**
     * Check phone exists or not
     *
     * @param email
     */
    public void isEmailValid(String email) {
        log.info("Validating email address={}", email);

        SampleUser object = repository.findByEmail(email);
        if (object != null) {
            log.warn("Email address={} exists", email);
            throw new DuplicatedException(Translator.toLocale("msg-user-email-invalid"));
        }
    }
}
