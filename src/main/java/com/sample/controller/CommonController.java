package com.sample.controller;

import com.sample.common.CommonService;
import com.sample.common.MailService;
import com.sample.controller.response.FailureResponse;
import com.sample.controller.response.SuccessResponse;
import com.sample.dto.CountryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sample.utils.Constant.REGEX.DD_M_YYYY_HH_MM_SS;
import static com.sample.utils.Constant.apiKey;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/common")
@Slf4j(topic = "COMMON-CONTROLLER")
public record CommonController(CommonService commonService, MailService mailService) {

    @GetMapping(path = "/countries", headers = apiKey)
    public SuccessResponse getCountries() {
        log.info("Request POST /common/countries");

        try {
            List<CountryDTO> countryList = commonService.getCountryList();
            return new SuccessResponse(OK, "countries", countryList);
        } catch (Exception e) {
            log.error("Get country list was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping(path = "/countries/country/{countryCode}", headers = apiKey)
    public SuccessResponse getCountry(@PathVariable String countryCode) {
        log.info("Request POST /common/countries/country/{}", countryCode);

        try {
            CountryDTO response = commonService.getCountryByCode(countryCode);
            return new SuccessResponse(OK, "country", response);
        } catch (Exception e) {
            log.error("Get country was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping(path = "/barcodes/qrCode", headers = apiKey, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> generateQRCodeImage(@RequestParam String barcodeText) throws Exception {
        log.info("Request POST /common/barcodes/qrCode?barcodeText={}", barcodeText);

        return ResponseEntity.ok(commonService.generateQRCodeImage(barcodeText));
    }

    @GetMapping(path = "/barcodes/barcode", headers = apiKey, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> generateBarcode(@RequestParam String barcodeText) throws Exception {
        log.info("Request GET /common/barcodes/barcode?barcodeText={}", barcodeText);

        return ResponseEntity.ok(commonService.generateBarCodeImage(barcodeText));
    }

    @GetMapping(path = "/export/excel", headers = apiKey)
    public void exportToExcel(HttpServletResponse response) throws IOException {
        log.info("Request GET /common/export/excel");

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat(DD_M_YYYY_HH_MM_SS);
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        commonService.writeExcel(response);
    }

    @GetMapping(path = "/export/pdf", headers = apiKey)
    public void exportToPDF(HttpServletResponse response) {
        log.info("Request GET /common/export/pdf");

        commonService.writePDF(response);
    }

    @PostMapping(path = "/upload", headers = apiKey)
    public SuccessResponse upload(@RequestParam("file") MultipartFile file) {
        log.info("Request GET /common/upload");

        try {
            String response = commonService.upload(file);
            return new SuccessResponse(OK, "File has uploaded successfully", response);
        } catch (IllegalArgumentException e) {
            log.error("Uploading file was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/send-email")
    public SuccessResponse sendEmail(@RequestParam String recipients, @RequestParam String subject,
                                     @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Request POST /common/send-email");
        try {
            mailService.sendEmail(recipients, subject, content, files);
            return new SuccessResponse(ACCEPTED, "Email has sent to successfully");
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Sending email was failure, message={}", e.getMessage(), e);
            return new FailureResponse(BAD_REQUEST, "Sending email was failure");
        }
    }
}
