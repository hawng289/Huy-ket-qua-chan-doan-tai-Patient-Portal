package vn.com.itechcorp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;

public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);

    public static final ThreadLocal<SimpleDateFormat> yyyyMMddHHmmss = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

    public static final ThreadLocal<SimpleDateFormat> yyyyMMdd = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    public static final ThreadLocal<SimpleDateFormat> yyyy = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy"));

    synchronized public static String getEpochMilli() {
        try {
            Thread.sleep(1);
            return Long.toString(Instant.now().toEpochMilli()).substring(1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
