package a.miracle.lib_zxing.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by A Miracle on 2016/3/14.
 */
public class IOUtils {

    /** 关闭流 */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
