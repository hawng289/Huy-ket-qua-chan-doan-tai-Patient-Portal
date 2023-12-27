package vn.com.itechcorp.module.portal.proxy;

import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
@FeignClient(name = "hisProxy", url = "${patient.portal.url}", configuration = PatientPortalProxy.ClientConfiguration.class)
public interface PatientPortalProxy {

    @PostMapping("/DeleteResult")
    feign.Response deleteReport(@Valid @RequestParam Long id);

    class ClientConfiguration {
        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .logLevel(Logger.Level.BASIC);
        }

    }
}

