package vn.com.itechcorp.module.hl7.util;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ApplicationRouter;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.impl.AppRoutingDataImpl;
import lombok.Getter;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.module.hl7.handler.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Component("registerHelper")
public class RegisterHelper {
    Map<ApplicationRouter.AppRoutingData, ReceivingApplication<? extends Message>> handlers = new LinkedHashMap<>();

    public RegisterHelper(LoggingHandler loggingHandler,
                          OrderHandler orderHandler,
                          PatientHandler patientHandler,
                          DepartmentHandler departmentHandler,
                          ProcedureHandler procedureHandler) {
        handlers.put(new AppRoutingDataImpl(Hl7MessageType.OMI.name(), Hl7TriggerEvent.O23.name(), "*", "*"), orderHandler);
        handlers.put(new AppRoutingDataImpl(Hl7MessageType.ADT.name(), Hl7TriggerEvent.A06.name(), "*", "*"), patientHandler);
        handlers.put(new AppRoutingDataImpl(Hl7MessageType.MFN.name(), Hl7TriggerEvent.M05.name(), "*", "*"), departmentHandler);
        handlers.put(new AppRoutingDataImpl(Hl7MessageType.MFN.name(), Hl7TriggerEvent.M09.name(), "*", "*"), procedureHandler);
        handlers.put(new AppRoutingDataImpl("*", "*", "*", "*"), loggingHandler);
    }

}
