package testcases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import setup.BaseSetup;
import helpers.ExcelHelpers;

public class ReadExcel extends BaseSetup {

    public static ExcelHelpers excelHelpers;

    @BeforeClass
    @Override
    public void setupBrowser() {
        super.setupBrowser();
        excelHelpers = new ExcelHelpers();
    }

    @Test
    public void read() throws Exception {
        excelHelpers.setExcelFile("src/test/resources/Book1.xlsx", "Sheet1");
        System.out.printf("username: %s\n", excelHelpers.getCellData("username", 1));
        System.out.printf("password: %s", excelHelpers.getCellData("username", 1));
    }
}
