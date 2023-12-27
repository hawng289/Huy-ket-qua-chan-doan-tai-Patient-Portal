package vn.com.itechcorp.module.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ApplicationRouter;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.impl.ApplicationRouterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.module.hl7.util.RegisterHelper;

import javax.annotation.PostConstruct;
import java.util.Map;


@Component("myApplicationRouter")
public class MyApplicationRouter extends ApplicationRouterImpl {

    @Autowired
    private RegisterHelper registerHelper;

    public MyApplicationRouter(DefaultHapiContext theContext) {
        super(theContext);
    }

    @PostConstruct
    public void init() {
        Map<ApplicationRouter.AppRoutingData, ReceivingApplication<? extends Message>> handlers = this.registerHelper.getHandlers();
        for (Map.Entry<ApplicationRouter.AppRoutingData, ReceivingApplication<? extends Message>> handler : handlers.entrySet()) {
            this.bindApplication(handler.getKey(), handler.getValue());
        }
    }
}
