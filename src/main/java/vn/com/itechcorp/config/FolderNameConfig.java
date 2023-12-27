package vn.com.itechcorp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.com.itechcorp.util.FolderNameFactory;

import java.util.List;

@Configuration
public class FolderNameConfig {

    @Value("#{'${setting.signed.pdf.folder.map}'.split(',')}")
    List<String> folderNameMap;

    @Bean
    public FolderNameFactory getFolderNameFactory() {
        FolderNameFactory factory = new FolderNameFactory();

        for (String item : folderNameMap) {
            String[] split = item.split(":");
            factory.add(split[0], split[1]);
        }
        return factory;
    }
}
