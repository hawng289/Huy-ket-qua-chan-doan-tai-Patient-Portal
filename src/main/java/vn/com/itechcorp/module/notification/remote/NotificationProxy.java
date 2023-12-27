package vn.com.itechcorp.module.notification.remote;


import feign.Feign;
import feign.Logger;
import feign.form.FormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.com.itechcorp.module.notification.service.dto.PushNotificationRequest;

@FeignClient(name = "notificationProxy", url = "${notification.url}", configuration = NotificationProxy.ClientConfiguration.class)
public interface NotificationProxy {
    @PostMapping("/api/OneSignal/HIS/PUSH?location=HIS")
    feign.Response pushNotification(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody PushNotificationRequest request);

    class ClientConfiguration {

        @Bean
        public Feign.Builder feignBuilder() {
            return Feign.builder()
                    .encoder(new FormEncoder())
                    .logLevel(Logger.Level.FULL);
        }

    }
}
