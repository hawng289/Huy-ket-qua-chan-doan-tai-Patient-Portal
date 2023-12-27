package vn.com.itechcorp.module.hl7;

import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionListener;

public class CustomConnectionListener implements ConnectionListener {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomConnectionListener.class);

    @Override
    public void connectionReceived(Connection connection) {
        logger.info("received connection {}", connection);
    }

    @Override
    public void connectionDiscarded(Connection connection) {
        logger.info("discarded connection {}", connection);
    }


}
