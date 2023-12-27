package vn.com.itechcorp.module.notification.persistance;

import org.springframework.stereotype.Repository;
import vn.com.itechcorp.base.persistence.repository.BaseRepository;

@Repository("notificationRepository")
public interface NotificationRepository extends BaseRepository<Notification, Long> {
}