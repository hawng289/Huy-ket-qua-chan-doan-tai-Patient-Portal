package vn.com.itechcorp;


import org.apache.commons.codec.binary.Hex;
import org.dcm4che3.util.StreamUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import vn.com.itechcorp.util.HtmlUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.MessageFormat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TestClass {


//    @Test
//    public void testStream() throws Exception {
//        FileInputStream inputStream = new FileInputStream("/home/owen/Downloads/anydesk_535.exe.zip");
//        byte[] byteArray = IOUtils.toByteArray(inputStream);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        for (int i = 0; i < 1000; i++) {
//            outputStream.writeTo(byteArray);
////            System.out.println(outputStream.);
//            System.out.println(byteArray.length);
//            Thread.sleep(5000);
//        }
//        outputStream.flush();
//
//    }

    private HttpURLConnection open(String url) throws Exception {
        long t1, t2;
        t1 = System.currentTimeMillis();
        URLConnection urlConnection = new URL(url).openConnection();
        final HttpURLConnection connection = (HttpURLConnection) urlConnection;
        t2 = System.currentTimeMillis();
        System.out.println("..");
        return connection;
    }

    @Test
    public void test1() throws Exception {
        HttpURLConnection connection = open("http://192.168.1.37:18080/dcm4chee-arc/aets/DCM4CHEE/rs/studies");
        // Size = 100MB
        File tmpFile = new File("/home/owen/Downloads/image.png");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", String.valueOf(tmpFile.length()));
        OutputStream out = connection.getOutputStream();
        System.out.println("ABC");
        for (int i = 0; i < 5; i++) {
            BufferedOutputStream bff = new BufferedOutputStream(out);
            RandomAccessFile ra = new RandomAccessFile("/sadasd","rw");
            ra.close();
            try {
                StreamUtils.copy(Files.newInputStream(tmpFile.toPath()), bff);
                bff.write(("\r\n--" + "myboundary" + "--\r\n").getBytes());
                bff.flush();
            } finally {
                out.close();
                bff.close();
            }
            System.out.println("Closed out");
        }
        /**
         * Start: out.size =0. Ram = 17.1 (dong 58)
         * ------------------
         * Lan i=0: Sau StreamUtils.copy:  out.size = 104857600 (0x6400000) va Ram = 17.6
         * Sau khi write them ("\r\n--" + "myboundary" + "--\r\n") => Ram = 17.5-17.6
         * Sau khi close: Ram = 17.6
         * -----------------
         * Lan i=1: Sau StreamUtils.copy:  out.size = 104857618 va Ram = 17.8
         * Sau khi write them ("\r\n--" + "myboundary" + "--\r\n") => Ram = 17.8-17.9
         * Sau khi close: Ram = 17.8
         * -----------------
         * Lan i=2: Sau StreamUtils.copy:  out.size = 104857618 va Ram = Len 18-18.1 xong lai ve 17.8-17.9
         * Sau khi write them ("\r\n--" + "myboundary" + "--\r\n") => Ram = 17.8-17.9
         * Sau khi close: Ram = 17.8
         * -----------------
         *
         */
    }

    @Test
    void test13(){
        String requestNum = "32623348";
        String s = Hex.encodeHexString(requestNum.getBytes());
        assertThat(s).isEqualTo("3332363233333438");
    }

    @Test
    void testParseHtml(){
        String html = "<html>\n" +
                "<body>\n" +
                "\n" +
                "<p>This is a paragraph.</p>\n" +
                "<p>This is a paragraph.</p>\n" +
                "<p>This is a paragraph.</p>\n" +
                "&nbsp abc"+
                "\n" +
                "</body>\n" +
                "</html>";

        String s = HtmlUtil.getInstance().convertToText(html);

        System.out.println("abc");
    }
}
