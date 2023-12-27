package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.message.OMI_O23;
import ca.uhn.hl7v2.model.v27.segment.IN1;
import ca.uhn.hl7v2.model.v27.segment.OBR;
import ca.uhn.hl7v2.model.v27.segment.ORC;
import ca.uhn.hl7v2.model.v27.segment.PV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.WorkListMessageService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageType;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.ris.dto.OrderProcedureDTO;
import vn.com.itechcorp.ris.dto.PatientDTO;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.util.Pair;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("orderHandler")
public class OrderHandler extends BaseHandler {

    private final boolean risEnabled;

    private final boolean worklistEnabled;

    private final Hl7MessageService hl7Service;

    private final RisService risService;

    private final WorklistService worklistService;

    private final RisMessageService risMessageService;

    private final WorkListMessageService workListMessageService;


    public OrderHandler(@Value("${ris.enabled:true}") boolean risEnabled,
                        @Value("${worklist.server.enabled:true}") boolean worklistEnabled,
                        Hl7MessageService hl7Service,
                        RisService risService,
                        WorklistService worklistService,
                        RisMessageService risMessageService,
                        WorkListMessageService workListMessageService) {
        this.risEnabled = risEnabled;
        this.worklistEnabled = worklistEnabled;
        this.hl7Service = hl7Service;
        this.risService = risService;
        this.worklistService = worklistService;
        this.risMessageService = risMessageService;
        this.workListMessageService = workListMessageService;
    }

    @Override
    public Hl7MessageService getHl7MessageService() {
        return hl7Service;
    }

    @Override
    public Message process(String messageId, Message message, Map<String, Object> map) throws Exception {

        Hl7MessageType messageType = getMessageType(message);

        Pair<Boolean, String> isSendSucceed;

        switch (messageType) {
            case ORDER_SAVE: {
                isSendSucceed = createOrder(messageId, message);
                break;
            }
            case ORDER_DELETE: {
                isSendSucceed = deleteOrder(messageId, message);
                break;
            }
            default: {
                getLogger().error("[MsgID-{}] Hl7MessageType is not valid for OrderHandler", messageId);
                throw new Exception("Hl7MessageType is not valid for OrderHandler");
            }
        }

        Message ack = (isSendSucceed == null || !isSendSucceed.getFirst()) ? message.generateACK(AcknowledgmentCode.AE, new HL7Exception(isSendSucceed == null ? "Application Error" : isSendSucceed.getSecond())) : message.generateACK();
        getLogger().info("MsgID-{}] Return ACK:\n{}", messageId, ack);
        return ack;
    }

    private Pair<Boolean, String> deleteOrder(String messageId, Message message) throws Exception {
        OrderDTO orderDTO = parse((OMI_O23) message);

        try {
            // goi API sang RIS xoa chi dinh
            if (worklistEnabled) {
                try {
                    WorklistResponse workListResponse = this.worklistService.removeWorklist(orderDTO.getAccessionNumber());
                    // luu response vao wl_message
                    this.workListMessageService.createOrUpdateAsync(messageId, workListResponse,orderDTO);
                } catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
            if (risEnabled) {
                try {
                    RisResponse risResponse = this.risService.removeOrder(orderDTO);

                    // luu response vao ris_message
                    this.risMessageService.createOrUpdateAsync(messageId, risResponse,orderDTO);

                    if (risResponse.getHeader() != null && risResponse.getHeader().getCode() == 200) {
                        return new Pair<>(true, "");
                    }
                    return new Pair<>(false, risResponse.getHeader() == null ? risResponse.getBody().toString() : risResponse.getHeader().getMessage());
                } catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);
                    return new Pair<>(false, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
            return new Pair<>(false, ex.getMessage());
        }
        return null;
    }

    private Pair<Boolean, String> createOrder(String messageId, Message message) throws Exception {
        OrderDTO orderDTO = parse((OMI_O23) message);
        // goi API sang WL
        if (worklistEnabled) {
            try {
                WorklistResponse workListResponse = this.worklistService.sendWorklist(new WorklistDTO(orderDTO));
                // luu response vao wl_message
                this.workListMessageService.createOrUpdateAsync(messageId, workListResponse,orderDTO);
            } catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }

        // goi API sang RIS them chi dinh
        if (risEnabled) {
            try {
                RisResponse risResponse = this.risService.sendOrder(orderDTO);

                // luu response vao ris_message
                this.risMessageService.createOrUpdateAsync(messageId, risResponse,orderDTO);

                if (risResponse.getHeader() != null && risResponse.getHeader().getCode() == 200) {
                    return new Pair<>(true, "");
                }
                return new Pair<>(false, risResponse.getHeader() == null ? risResponse.getBody().toString() : risResponse.getHeader().getMessage());
            } catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
                return new Pair<>(false, ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean canProcess(Message message) {
        return message instanceof OMI_O23;
    }

    public OrderDTO parse(OMI_O23 omiO23) throws Exception {
        Map<String, String> attributes = new HashMap<>();

        PatientDTO patient = new PatientDTO(omiO23.getPATIENT().getPID());

        if (patient.getCompanyName() != null && patient.getCompanyName().trim().length() > 0) {
            attributes.putIfAbsent("patientCompany", patient.getCompanyName());
        }

        PV1 pv1Segment = omiO23.getPATIENT().getPATIENT_VISIT().getPV1();
        String pointOfCare = pv1Segment.getPv13_AssignedPatientLocation().getPl1_PointOfCare().getHd1_NamespaceID().getValue();

        IN1 insuranceSegment = omiO23.getPATIENT().getINSURANCE(0).getIN1();
        String insuranceNumber = insuranceSegment.getIn12_HealthPlanID().getCwe1_Identifier().getValue();
        String insuranceEffectiveDate = insuranceSegment.getIn112_PlanEffectiveDate().getValue();
        String insuranceExpirationDate = insuranceSegment.getIn113_PlanExpirationDate().getValue();

        ORC orcSegment = omiO23.getORDER().getORC();
        String accessionNumber = orcSegment.getOrc2_PlacerOrderNumber().getEi1_EntityIdentifier().getValue();
        if (accessionNumber == null || accessionNumber.isEmpty())
            throw new HL7Exception("Invalid null {accessionNumber}");

        String referringPhysicianCode = orcSegment.getOrc12_OrderingProvider(0).getXcn1_PersonIdentifier().getValue();
        String referringPhysicianName = orcSegment.getOrc12_OrderingProvider(0).getXcn2_FamilyName().getFn1_Surname().getValue();
        String orderDateTime = orcSegment.getOrc9_DateTimeOfTransaction().getValue();

        // get order procedure Information
        OBR obrSegment = omiO23.getORDER().getOBR();
        String requestNumber = obrSegment.getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().getValue();
        String requestedProcedureCode = obrSegment.getObr4_UniversalServiceIdentifier().getCwe1_Identifier().getValue();
        String requestedProcedureName = obrSegment.getObr4_UniversalServiceIdentifier().getCwe2_Text().getValue();
        String urgent = obrSegment.getObr5_Priority().getValue();
        String modalityType = obrSegment.getObr24_DiagnosticServSectID().getValue();
        if (modalityType == null || modalityType.isEmpty())
            throw new HL7Exception("Invalid null {modalityType}");

        // Adding identifier
        String identifier = obrSegment.getObr27_QuantityTiming(0).getValue();
        if (identifier != null && identifier.trim().length() > 0) {
            attributes.putIfAbsent("requestOrdinalExamination", identifier);
        }

        String icdCode = obrSegment.getObr31_ReasonForStudy(0).getCwe1_Identifier().getValue();
        String diagnosis = obrSegment.getObr31_ReasonForStudy(0).getCwe2_Text().getValue();

        String serviceType = obrSegment.getObr47_FillerSupplementalServiceInformation(0).getCwe1_Identifier().getValue();

        OrderDTO entity = new OrderDTO();
        entity.setAccessionNumber(accessionNumber);
        entity.setOrderNumber(accessionNumber);
        if (pointOfCare != null) {
            String[] split = pointOfCare.split(",");
            entity.setRequestedDepartmentCode(split[0]);
            entity.setRequestedDepartmentName(split[1]);
        }
        entity.setReferringPhysicianCode(referringPhysicianCode);
        entity.setReferringPhysicianName(referringPhysicianName);
        entity.setClinicalDiagnosis(diagnosis);
        entity.setUrgent(urgent != null && urgent.equals("Y"));
        entity.setInstructions(null);
        entity.setOrderDatetime(orderDateTime);
        entity.setModalityType(modalityType);
        entity.setPatient(patient);
        entity.setInsuranceApplied(insuranceNumber != null && !insuranceNumber.isEmpty() && serviceType.equals("1"));
        entity.setInsuranceNumber(insuranceNumber);
        entity.setInsuranceIssuedDate(insuranceEffectiveDate);
        entity.setInsuranceExpiredDate(insuranceExpirationDate);
        entity.setServices(new ArrayList<>());
        entity.getServices().add(new OrderProcedureDTO(icdCode, orderDateTime, modalityType, requestNumber, requestedProcedureCode, requestedProcedureName));
        entity.setAdd(orcSegment.getOrc1_OrderControl().getValue().equals("NW") || orcSegment.getOrc1_OrderControl().getValue().equals("XO"));
        entity.setAttributes(attributes);

        return entity;
    }
}
