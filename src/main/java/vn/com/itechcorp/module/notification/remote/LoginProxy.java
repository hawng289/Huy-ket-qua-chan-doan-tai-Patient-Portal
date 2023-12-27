package vn.com.itechcorp.module.notification.remote;

import feign.Feign;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@FeignClient(name = "loginProxy", url = "${notification.token.url}", configuration = LoginProxy.ClientConfiguration.class)
public interface LoginProxy {

    @PostMapping(value = "/token",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    feign.Response login(Map<String,?> request);

    @Component
    class ClientConfiguration {
        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        @Scope(SCOPE_PROTOTYPE)
        Encoder feignFormEncoder() {
            return new FormEncoder(new SpringEncoder(this.messageConverters));
        }

        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .encoder(new FormEncoder())
                    .logLevel(Logger.Level.FULL);
        }

    }
}
