package vn.com.itechcorp.worklist.proxy;

import feign.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@FeignClient(name = "worklistProxy", url = "url-placeholder", configuration = WorklistProxy.ClientConfiguration.class)
public interface WorklistProxy {

    @GetMapping("/patients")
    feign.Response getPatients(URI uri,
                               @SpringQueryMap Map<String, Object> filter);

    @PostMapping("/patients")
    feign.Response createPatient(URI uri,
                                 @RequestBody Map<String, Object> body);

    @PostMapping("/mwlitems")
    feign.Response createWorklist(URI uri,
                                  @RequestBody Map<String, Object> body);

    @GetMapping("/mwlitems")
    feign.Response getWorklists(URI uri,
                                @SpringQueryMap Map<String, Object> filter);

    @DeleteMapping("/mwlitems/{studyInstanceUID}/{requestedProcedureID}")
    feign.Response deleteWorklist(URI uri,
                                  @PathVariable("studyInstanceUID") String studyInstanceUID,
                                  @PathVariable("requestedProcedureID") String requestedProcedureID);

    class ClientConfiguration {
        @Bean
        Logger.Level feignLoggerLevel() {
            return Logger.Level.FULL;
        }
    }

}
