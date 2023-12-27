package vn.com.itechcorp.module.notification.service;

import vn.com.itechcorp.base.service.BaseDtoService;
import vn.com.itechcorp.module.notification.persistance.Notification;
import vn.com.itechcorp.module.notification.service.dto.NotificationDTOGet;

public interface PushNotificationService extends BaseDtoService<NotificationDTOGet, Notification,Long> {
    boolean pushNotification(String patientName, String procedureName, String pid, String path);
}
