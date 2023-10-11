package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static final ExtentReports extentReports = new ExtentReports();

    public synchronized static ExtentReports getExtentReports() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("src/main/resources/static/index.html");
        reporter.config().setReportName("Extent Report");
        extentReports.attachReporter(reporter);
//        extentReports.setSystemInfo("Framework Name", "Selenium WebDriver Java");
//        extentReports.setSystemInfo("Author", "TayLQ");
        return extentReports;
    }

}
