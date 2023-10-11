package com.sample.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.sample.controller.response.LoadingPageResponse;
import com.sample.dto.SampleDTO;
import com.sample.model.SampleUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.sample.utils.Constant.INDEX.FIRST_NAME;

@Repository
@Slf4j(topic = "SAMPLE-USER-REPOSITORY")
public class SampleUserRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    /**
     * Save sample user to dynamodb
     *
     * @param object
     * @return
     */
    public SampleUser save(SampleUser object) {
        log.info("Saving sample user, object={}",object);

        dynamoDBMapper.save(object);
        log.info("Sample user has saved");
        return object;
    }

    /**
     * Update sample user to dynamodb
     *
     * @param object
     * @param hashKey
     * @return
     */
    public String update(SampleUser object, String hashKey) {
        log.info("Updating sample user by composite key hashKey={}, rangeKey={}", hashKey, object.getRangeKey());

        dynamoDBMapper.save(object,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("hashKey",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(hashKey)
                                )));
        log.info("Update sample user by composite key hashKey={}, rangeKey={}", hashKey, object.getRangeKey());
        return hashKey;
    }

    /**
     * Delete sample user from dynamodb
     *
     * @param hashKey
     * @param rangeKey
     * @return
     */
    public String delete(String hashKey, String rangeKey) {
        log.info("Deleting sample user by composite key hashKey={}, rangeKey={}", hashKey, rangeKey);

        dynamoDBMapper.delete(SampleUser.builder()
                .hashKey(hashKey)
                .rangeKey(rangeKey).build());
        return rangeKey;
    }

    /**
     * Find sample user from to dynamodb
     *
     * @param hashKey
     * @param rangeKey
     * @return SampleUser
     */
    public SampleUser findByCompositeKey(String hashKey, String rangeKey) {
        log.info("Finding sample user by composite key hashKey={}, rangeKey={}", hashKey, rangeKey);

        return dynamoDBMapper.load(SampleUser.class, hashKey, rangeKey);
    }

    /**
     * Find sample user by phone
     *
     * @param phone
     * @return SampleUser
     */
    public SampleUser findByPhone(String phone) {
        log.info("Finding sample user by phone ...");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":phone", new AttributeValue().withS(phone));

        DynamoDBQueryExpression<SampleUser> queryExpression = new DynamoDBQueryExpression<SampleUser>()
                .withIndexName("phone-index")
                .withKeyConditionExpression("phone = :phone")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);
        PaginatedQueryList<SampleUser> result = dynamoDBMapper.query(SampleUser.class, queryExpression);

        if (!result.isEmpty()) {
            log.info("Found out sample user, phone={} in DynamoDB", phone);
            return result.get(0);
        }
        return null;
    }

    /**
     * Find sample user by email
     *
     * @param email
     * @return SampleUser
     */
    public SampleUser findByEmail(String email) {
        log.info("Finding sample user by email={}", email);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":email", new AttributeValue().withS(email));

        DynamoDBQueryExpression<SampleUser> queryExpression = new DynamoDBQueryExpression<SampleUser>()
                .withIndexName("email-index")
                .withKeyConditionExpression("email = :email")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);
        PaginatedQueryList<SampleUser> result = dynamoDBMapper.query(SampleUser.class, queryExpression);

        if (!result.isEmpty()) {
            log.info("Found out sample user, email={} in DynamoDB", email);
            return result.get(0);
        }
        return null;
    }

    /**
     * Find all sample user by multiple conditions
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
    public LoadingPageResponse findAll(String hashKey, String search, String firstName, String lastName, String phone, String email, String address, String status, String orderBy, String nextKey, int pageSize) {
        log.info("Finding sample user from DynamoBD ...");

        DynamoDBQueryExpression<SampleUser> queryExpression = new DynamoDBQueryExpression<SampleUser>()
                .withHashKeyValues(SampleUser.builder().hashKey(hashKey).build())
                .withConsistentRead(false);

        Map<String, String> ean = new HashMap<>();
        Map<String, AttributeValue> eav = new HashMap<>();
        StringBuilder filterExpression = new StringBuilder();

        if (StringUtils.hasLength(search)) { // search free text
            eav.put(":keyword", new AttributeValue().withS(search));
            filterExpression.append("contains(searchKeys, :keyword)");
            log.info("search free text into field searchKey");
            log.info("Finding sample user with keyword={}", search);
        } else { // search advance
            if (StringUtils.hasLength(firstName)) {
                eav.put(":firstName", new AttributeValue().withS(firstName));
                filterExpression.append("firstName = :firstName ");
            }

            if (StringUtils.hasLength(lastName)) {
                if (StringUtils.hasLength(filterExpression)) {
                    filterExpression.append("AND ");
                }
                eav.put(":lastName", new AttributeValue().withS(lastName));
                filterExpression.append("lastName = :lastName ");
            }

            if (StringUtils.hasLength(phone)) {
                if (StringUtils.hasLength(filterExpression)) {
                    filterExpression.append("AND ");
                }
                eav.put(":phone", new AttributeValue().withS(phone));
                filterExpression.append("phone = :phone ");
            }

            if (StringUtils.hasLength(email)) {
                if (StringUtils.hasLength(filterExpression)) {
                    filterExpression.append("AND ");
                }
                eav.put(":email", new AttributeValue().withS(email));
                filterExpression.append("email = :email ");
            }

            if (StringUtils.hasLength(address)) {
                eav.put(":address", new AttributeValue().withS(address));
                filterExpression.append("(address.text = :address) ");
            }

            if (StringUtils.hasLength(status)) {
                if (StringUtils.hasLength(status)) {
                    eav.put(":status", new AttributeValue().withS(status.toUpperCase()));
                    filterExpression.append("(status = :status) ");
                }
            }
        }

        if (!filterExpression.isEmpty()) {
            queryExpression.withFilterExpression(filterExpression.toString());
            queryExpression.withExpressionAttributeValues(eav);
        }

        if (StringUtils.hasLength(nextKey)) {
            Map<String, AttributeValue> keys = new LinkedHashMap<>();
            AttributeValue hashValue = new AttributeValue(hashKey);
            AttributeValue rangeValue = new AttributeValue(nextKey);
            keys.put("hashKey", hashValue);
            keys.put("rangeKey", rangeValue);
            queryExpression.setExclusiveStartKey(keys);
        }

        // sort by firstName-index desc
        if (StringUtils.hasLength(orderBy)) {
            queryExpression.withIndexName(FIRST_NAME).withScanIndexForward(false);
        }

        List<SampleUser> result = new ArrayList<>();
        QueryResultPage<SampleUser> resultPage;

        // process case: result < expected result
        int limit = 100;
        do {
            queryExpression.withLimit(limit);
            resultPage = dynamoDBMapper.queryPage(SampleUser.class, queryExpression);
            queryExpression.setExclusiveStartKey(resultPage.getLastEvaluatedKey());
            result.addAll(resultPage.getResults());
            limit = 300000;

            log.info("queryExpression: {}", queryExpression);
        } while (result.size() < pageSize && resultPage.getLastEvaluatedKey() != null);

        if (pageSize > result.size()) pageSize = result.size() - 1;

        result = result.subList(0, pageSize);

        String rangeKey = "";
        if (!result.isEmpty()) {
            rangeKey = result.get(pageSize - 1).getRangeKey();
        }

        List<SampleDTO> items = result.stream().map(
                x -> SampleDTO.builder()
                        .id(x.getRangeKey())
                        .firstName(x.getFirstName())
                        .lastName(x.getLastName())
                        .phone(x.getPhone())
                        .email(x.getEmail())
                        .address(x.getAddress())
                        .build()
        ).toList();

        return LoadingPageResponse.builder()
                .nextKey(rangeKey)
                .items(items)
                .build();
    }
}
