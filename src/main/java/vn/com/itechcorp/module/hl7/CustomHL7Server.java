package vn.com.itechcorp.module.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.app.SimpleServer;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ApplicationRouter;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.module.hl7.util.RegisterHelper;
import vn.com.itechcorp.util.SafeClose;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component("customHL7Server")
@ConditionalOnProperty(name = "hl7.server.enableb", havingValue = "true")
public class CustomHL7Server {
    private static final Logger logger = LoggerFactory.getLogger(CustomHL7Server.class);

    private final DefaultHapiContext context;
    private SimpleServer server;

    @Value("${hl7.server.port}")
    private int port;

    private ThreadPoolExecutor executor;
    private final RegisterHelper registerHelper;

    @PostConstruct
    public void init() {
        try {
            executor = new ThreadPoolExecutor(
                    10, 100,
                    30, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            context.setExecutorService(executor);
            context.setValidationContext(ValidationContextFactory.noValidation());

            server = context.newServer(port, false);

            Map<ApplicationRouter.AppRoutingData, ReceivingApplication<? extends Message>> handlers = this.registerHelper.getHandlers();

            for (Map.Entry<ApplicationRouter.AppRoutingData, ReceivingApplication<? extends Message>> handler : handlers.entrySet()) {
                server.registerApplication(handler.getKey(), handler.getValue());
            }
            server.registerConnectionListener(new CustomConnectionListener());

            server.setExceptionHandler(new ExceptionHandler());

            server.start();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            if (server != null)
                server.stop();
            SafeClose.close(context);
            executor.shutdown();
        } catch (Throwable e) {
            logger.error("[HL7-SERVER][ERR-STOP] {}", e.getMessage());
        }
    }
}
