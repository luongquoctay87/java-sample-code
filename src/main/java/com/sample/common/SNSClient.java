package com.sample.common;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j(topic = "SNS-CLIENT")
@Service
public class SNSClient {
    @Autowired
    private AmazonSNSClient snsClient;

    public void sendToSNS(String platformArn, String deviceToken, String message) {
        log.info("Start send SNS message");

        CreatePlatformEndpointRequest createPlatformEndpointRequest = new CreatePlatformEndpointRequest()
                .withPlatformApplicationArn(platformArn)
                .withToken(deviceToken);
        CreatePlatformEndpointResult result = snsClient.createPlatformEndpoint(createPlatformEndpointRequest);
        log.info("Endpoint ARN: {}", result.getEndpointArn());
        try {
            PublishRequest publishRequest = new PublishRequest()
                    .withMessageStructure("json")
                    .withTargetArn(result.getEndpointArn())
                    .withMessage(message);
            PublishResult publishResponse = snsClient.publish(publishRequest);
            log.info("End send SNS message");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // Create the delete request
            DeleteEndpointRequest deleteEndpointRequest = new DeleteEndpointRequest()
                    .withEndpointArn(result.getEndpointArn());
            // Delete the platformArn endpoint
            DeleteEndpointResult deleteEndpointResult = snsClient.deleteEndpoint(deleteEndpointRequest);
            log.info("Endpoint deleted: {}", result.getEndpointArn());
        }
    }
}
