package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.ADT_A06;
import org.dcm4che3.data.Attributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.WorkListMessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageType;
import vn.com.itechcorp.ris.dto.PatientDTO;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.util.Pair;
import vn.com.itechcorp.worklist.dto.MwlItemFilter;
import vn.com.itechcorp.worklist.dto.SPSStatus;
import vn.com.itechcorp.worklist.service.WorklistService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service("patientHandler")
public class PatientHandler extends BaseHandler {

    private final Hl7MessageService hl7Service;

    private final RisService risService;

    private final boolean risEnabled;

    private final boolean wlEnable;

    private final RisMessageService risMessageService;

    private final WorklistService worklistService;

    private final WorkListMessageService workListMessageService;

    public PatientHandler(Hl7MessageService hl7Service,
                          @Value("${ris.enabled:true}") boolean risEnabled,
                          @Value("${worklist.server.enabled:true}") boolean wlEnable,
                          RisService risService,
                          RisMessageService risMessageService,
                          WorklistService worklistService,
                          WorkListMessageService workListMessageService) {
        this.hl7Service = hl7Service;
        this.risService = risService;
        this.risEnabled = risEnabled;
        this.wlEnable = wlEnable;
        this.risMessageService = risMessageService;
        this.worklistService = worklistService;
        this.workListMessageService = workListMessageService;
    }

    @Override
    public Hl7MessageService getHl7MessageService() {
        return hl7Service;
    }

    @Override
    public Message process(String messageId, Message message, Map<String, Object> map) throws Exception {

        Hl7MessageType messageType = getMessageType(message);

        Pair<Boolean, String> isSendSucceed = null;
        if (Objects.requireNonNull(messageType) == Hl7MessageType.PATIENT_UPDATE) {
            isSendSucceed = this.updatePatient(messageId, message);

        }

        Message ack = (isSendSucceed == null || !isSendSucceed.getFirst()) ? message.generateACK(AcknowledgmentCode.AE, new HL7Exception(isSendSucceed == null ? "Application Error" : isSendSucceed.getSecond())) : message.generateACK();
        getLogger().info("MsgID-{}] Return ACK:\n{}", messageId, ack);
        return ack;
    }

    private Pair<Boolean, String> updatePatient(String messageId, Message message) throws Exception {
        PatientDTO patientDTO = new PatientDTO(((ADT_A06) message).getPID());
        if (risEnabled) {
            // gui yeu cau cap nhat benh nhan den ris
            RisResponse risResponse = this.risService.updatePatient(patientDTO);

            // luu response ben ris
//            this.risMessageService.createOrUpdateAsync(messageId, risResponse,);

            if (risResponse.getHeader() != null && risResponse.getHeader().getCode() == 200) {
                return new Pair<>(true, "");
            }
            return new Pair<>(false, risResponse.getHeader() == null ? risResponse.getBody().toString() : risResponse.getHeader().getMessage());
        }
        if (wlEnable) {
            // Tim kiem thong tin benh nhan tren worklist (luu y  la chua chup)
            MwlItemFilter filter = new MwlItemFilter();
            filter.setPatientID(patientDTO.getPid());
            filter.setStatus(SPSStatus.SCHEDULED);
            List<Attributes> response = this.worklistService.getWorkList(filter);
            if (response == null || response.isEmpty()) {
                getLogger().error("Pid-{} Can not found patient in work list", patientDTO.getPid());
            }
            // Luu lai log
        }
        return null;
    }

    private void createPatient(String messageId, Message message) throws Exception {
        PatientDTO patientDTO = new PatientDTO(((ADT_A06) message).getPID());
        if (risEnabled) {
            // gui yeu cau cap nhat benh nhan den ris
            RisResponse risResponse = this.risService.createPatient(patientDTO);

            // luu response ben ris
//            this.risMessageService.createOrUpdateAsync(messageId, risResponse);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        return false;
    }

}
