package vn.com.itechcorp.hsm.remote;

import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import vn.com.itechcorp.hsm.dto.SignCloudRequest;
import vn.com.itechcorp.hsm.dto.SignCloudResponse;

@FeignClient(name = "cloudHsmProxy", url = "${cloud.hsm.config.url:https://rssp.mobile-id.vn/eSignCloud/restapi}", configuration = CloudHsmProxy.HsmConfiguration.class)
public interface CloudHsmProxy {

    @PostMapping("/prepareFileForSignCloud")
    ResponseEntity<SignCloudResponse> sign(SignCloudRequest request);

    class HsmConfiguration {
        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .logLevel(Logger.Level.FULL);
        }
    }
}
