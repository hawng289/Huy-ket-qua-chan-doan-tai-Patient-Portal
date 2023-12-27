package vn.com.itechcorp.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    private static final DateFormat formatter = new SimpleDateFormat("yyyy");

    public static File createDirIfNotExist(String url) throws IOException {
        File folder = new File(url);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                if (!folder.setWritable(true))
                    throw new IOException("Could not set Writable permission to folder '" + url + "'");
                if (!folder.setReadable(true))
                    throw new IOException("Could not set Readable permission to folder '{" + url + "}'");
                return folder;
            } else return null;
        }
        return folder;
    }

    public static String createStoredDir(String root, String folderName) throws Exception {
        if (FileUtils.createDirIfNotExist(root) == null) throw new Exception("Error creating pdfPath folder");

        String path = formatter.format(new Date());
        if (FileUtils.createDirIfNotExist(root + "/" + path) == null) throw new Exception("Error creating date folder");

        if (folderName != null) {
            path += "/" + folderName;
            if (FileUtils.createDirIfNotExist(root + "/" + path) == null) throw new Exception("Error creating folder");
        }
        return path;
    }

}
