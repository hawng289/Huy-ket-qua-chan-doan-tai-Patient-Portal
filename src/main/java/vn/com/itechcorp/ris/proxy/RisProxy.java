package vn.com.itechcorp.ris.proxy;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.itechcorp.ris.dto.*;
import vn.com.itechcorp.util.FeignErrorDecoder;

import javax.validation.Valid;

@FeignClient(name = "risITProxy", url = "${ris.url}", configuration = RisProxy.ClientConfiguration.class)
public interface RisProxy {

    @PostMapping("/order")
    feign.Response sendOrder(@Valid @RequestBody OrderDTO entity);

    @GetMapping("/order/{accessionNumber}")
    ResponseEntity<OrderDTOGet> getOrder(@Valid @PathVariable String accessionNumber);

    @DeleteMapping("/order/{accessionNumber}")
    feign.Response removeOrder(@Valid @PathVariable String accessionNumber);

    @GetMapping("/viewer/{accessionNumber}")
    String getViewerURL(@Valid @PathVariable String accessionNumber);

    @GetMapping("/pdf/{serviceID}")
    ResponseEntity<byte[]> getSignedPDF(@Valid @PathVariable Long serviceID);

    @PostMapping("/patient")
    feign.Response createPatient(@RequestBody PatientDTO patient);

    @PutMapping("/patient")
    feign.Response updatePatient(@RequestBody PatientDTO patient);

    @PostMapping("/study")
    feign.Response createStudy(@Valid @RequestBody StudyDTO study);

    @PostMapping("/order/{orderNumber}/procedure/{procedureCode}")
    feign.Response updateHisReportStatus(@PathVariable String orderNumber,
                                         @PathVariable String procedureCode,
                                         @RequestBody HisReportStatusUpdate object);

    class ClientConfiguration {

        @Value("${ris.username}")
        private String username;

        @Value("${ris.password}")
        private String password;

        @Bean
        public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
            return new BasicAuthRequestInterceptor(username, password);
        }

        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .logLevel(Logger.Level.BASIC)
                    .errorDecoder(new FeignErrorDecoder());
        }
    }
}
