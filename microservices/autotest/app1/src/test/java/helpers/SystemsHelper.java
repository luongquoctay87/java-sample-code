package helpers;

import java.io.File;

public class SystemsHelper {

    //Hàm lấy đường dẫn tuyệt đối của project tính từ ổ cứng máy tính
    public static String getCurrentDir() {
        String current = System.getProperty("user.dir") + File.separator;
        return current;
    }

}
