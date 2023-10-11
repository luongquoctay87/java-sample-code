# Generate Barcode and QR code

### 1. Add dependency `ZXing Library`

```
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.3.0</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.3.0</version>
</dependency>
```

### 2. Init @Bean `BufferedImageHttpMessageConverter`

```
@Configuration
public class AppConfig {

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
```

### 3. Generate Barcode & QR Code Image
- Generate QR code
```
@Service
@Slf4j
public class CommonService {
    ...
    public BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    ...
}
```

- Generate Barcodes
```
@Service
@Slf4j
public class CommonService {
    ...
    public BufferedImage generateBarCodeImage(String barcodeText) throws Exception {
        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    ...
}
```

### 4. Building a REST API

```
@RestController
@RequestMapping("/common")
@Slf4j(topic = "COMMON-CONTROLLER")
public record CommonController(CommonService commonService) {
    ...
    @PostMapping(path = "/barcodes/qrCode", headers = apiKey, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> generateQRCodeImage(@RequestParam String barcodeText) throws Exception {
        return ResponseEntity.ok(commonService.generateQRCodeImage(barcodeText));
    }

    @GetMapping(path = "/barcodes/barcode", headers = apiKey, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> generateBarcode(@RequestParam String barcodeText) throws Exception {
        return ResponseEntity.ok(commonService.generateBarCodeImage(barcodeText));
    }
    ...
}
```

### 5. Test
Finally, we can use Postman or a browser to view the generated barcodes.

- Test Barcodes

```
curl --location --request POST 'http://localhost:8181/common/barcodes/qrCode?barcodeText=QRcode' \
--header 'apiKey: sample'
```

- Test QR code

```
curl --location 'http://localhost:8181/common/barcodes/barcode?barcodeText=1234567890128' \
--header 'Accept-Language: en-US' \
--header 'apiKey: sample'
```

- ***Reference source:*** [Generating Barcodes and QR Codes in Java](https://www.baeldung.com/java-generating-barcodes-qr-codes)

