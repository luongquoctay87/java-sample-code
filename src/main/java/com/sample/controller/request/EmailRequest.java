package com.sample.controller.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmailRequest implements Serializable {
    @NotBlank(message = "from must be not blank")
    private String from;

    @NotBlank(message = "recipients must be not blank")
    private String recipients;

    @NotBlank(message = "subject must be not blank")
    private String subject;

    @NotBlank(message = "content must be not blank")
    private String content;

    private MultipartFile[] files;
}
