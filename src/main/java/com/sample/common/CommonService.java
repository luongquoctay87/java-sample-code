package com.sample.common;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sample.dto.CountryDTO;
import com.sample.exception.NotFoundException;
import com.sample.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j(topic = "COMMON-SERVICE")
public class CommonService {

    @Autowired
    private PDFGenerator pdfGenerator;
    @Autowired
    private S3Client s3Client;
    @Value("${amazon.s3.bucket}")
    private String bucketName;

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
        if (!countries.isEmpty()) {
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

    /**
     * Generate QR code image
     *
     * @param barcodeText
     * @return QR image
     * @throws Exception
     */
    public BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Generate Barcode image
     *
     * @param barcodeText
     * @return Barcode image
     * @throws Exception
     */
    public BufferedImage generateBarCodeImage(String barcodeText) throws Exception {
        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Write to excel
     *
     * @param response
     * @throws IOException
     */
    public void writeExcel(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("SampleUser");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        // Setting headers
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.DIAMONDS);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Age");
        headerCell.setCellStyle(headerStyle);

        // write the content of the table in a different style
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        Row row = sheet.createRow(2);
        Cell cell = row.createCell(0);
        cell.setCellValue("John Smith");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(20);
        cell.setCellStyle(style);

        // save to application folder
        // File currDir = new File(".");
        // String path = currDir.getAbsolutePath();
        // String fileLocation = path.substring(0, path.length() - 1) + "template.xlsx";
        // FileOutputStream outputStream = new FileOutputStream(fileLocation);

        // Response via api
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

        log.info("Export file successful");
    }

    /**
     * Write to PDF
     *
     * @param response file .pdf
     */
    public void writePDF(HttpServletResponse response) {
        pdfGenerator.generatePdfReport(response);
    }

    /**
     * Upload file to S3
     *
     * @param file
     */
    public String upload(MultipartFile file) {
        if (!file.isEmpty()) {
            return s3Client.putObject(bucketName, file, true);
        } else {
            throw new IllegalArgumentException("File can not blank");
        }
    }
}
