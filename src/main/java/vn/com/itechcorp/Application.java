package vn.com.itechcorp;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoAutoConfiguration.class
})
@EnableScheduling
@EnableEurekaClient
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    DefaultHapiContext defaultHapiContext() {
        DefaultHapiContext context = new DefaultHapiContext();
        context.setValidationContext(ValidationContextFactory.noValidation());
        MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
        mllp.setCharset("UTF-8");
        context.setLowerLayerProtocol(mllp);

        return context;
    }

}
