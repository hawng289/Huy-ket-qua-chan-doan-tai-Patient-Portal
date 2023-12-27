package vn.com.itechcorp.his.proxy;

import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.his.dto.HisUpdateStatusRequest;

import javax.validation.Valid;

@FeignClient(name = "hisProxy", url = "${his.url}", configuration = HisProxy.ClientConfiguration.class)
public interface HisProxy {

    @PostMapping("/UpdateResult")
    feign.Response sendReport(@Valid @RequestBody HisRequest request);

    @PostMapping("/CapNhatTrangThai")
    feign.Response updateStatus(@Valid @RequestBody HisUpdateStatusRequest request);

    @PostMapping("/DeleteResult")
    feign.Response deleteReport(@Valid @RequestBody HisRequest request);

    class ClientConfiguration {
        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .logLevel(Logger.Level.BASIC);
        }

    }
}
