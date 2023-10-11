# Export Excel in Java

## 1. Add dependency

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
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.jxls</groupId>
        <artifactId>jxls-jexcel</artifactId>
        <version>1.0.9</version>
    </dependency>
    <dependency>
        <groupId>org.dhatim</groupId>
        <artifactId>fastexcel-reader</artifactId>
        <version>0.15.3</version>
    </dependency>
    <dependency>
        <groupId>org.dhatim</groupId>
        <artifactId>fastexcel</artifactId>
        <version>0.15.3</version>
    </dependency>
```

## 2. Writing to Excel

```
@Service
@Slf4j
public class CommonService {
    ...
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
    ...
}
```

## 3. Add to controller

```
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        commonService.writeExcel(response);
    }
```

## 4. Test by browser
- http://localhost:8181/common/export/excel


- ***Reference Sources:*** [Working with Microsoft Excel in Java](https://www.baeldung.com/java-microsoft-excel)
- ***Source code:*** [GitHub](https://github.com/luongquoctay87/java-sample-code/tree/generate-export-file)
