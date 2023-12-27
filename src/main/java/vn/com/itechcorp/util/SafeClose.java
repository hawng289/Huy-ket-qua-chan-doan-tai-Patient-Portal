package vn.com.itechcorp.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * his-connector
 *
 * @author linh.vu@itechcorp.com.vn
 * @since 11/19/19 9:09 AM
 */
public class SafeClose {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SafeClose.class);

    public static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
