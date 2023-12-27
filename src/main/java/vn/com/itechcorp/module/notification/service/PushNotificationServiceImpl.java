package vn.com.itechcorp.module.notification.service;

import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.service.impl.BaseDtoJpaServiceImpl;
import vn.com.itechcorp.module.notification.persistance.Notification;
import vn.com.itechcorp.module.notification.persistance.NotificationRepository;
import vn.com.itechcorp.module.notification.remote.NotificationProxy;
import vn.com.itechcorp.module.notification.service.dto.NotificationDTOGet;
import vn.com.itechcorp.module.notification.service.dto.PushNotificationRequest;
import vn.com.itechcorp.util.JsonUtils;

@Service("pushNotificationService")
public class PushNotificationServiceImpl extends BaseDtoJpaServiceImpl<NotificationDTOGet, Notification,Long> implements PushNotificationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthHelper authHelper;

    private final NotificationProxy notificationProxy;

    private final NotificationRepository notificationRepository;

    public PushNotificationServiceImpl(AuthHelper authHelper,
                                       NotificationProxy notificationProxy,
                                       NotificationRepository notificationRepository) {
        this.authHelper = authHelper;
        this.notificationProxy = notificationProxy;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationRepository getRepository() {
        return notificationRepository;
    }

    @Override
    public NotificationDTOGet convert(Notification notification) {
        return new NotificationDTOGet(notification);
    }

    @Override
    public boolean pushNotification(String patientName, String procedureName, String pid, String path) {
        if (patientName == null || pid == null || procedureName == null || path == null) return false;

        PushNotificationRequest request = new PushNotificationRequest(patientName, procedureName, pid, path);
        try (Response response = notificationProxy.pushNotification(authHelper.getAccessToken(), request)) {
            logger.info("[PatientID-{}] Push new notification to mobile APP. Response-{}",pid,response.status());
            Boolean isStatus = JsonUtils.getInstance().convertFeignResponse(response, Boolean.class);
            return isStatus != null && isStatus.equals(Boolean.TRUE);
        }
    }
}
