package vn.com.itechcorp.module.hl7.service;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v27.datatype.ST;
import ca.uhn.hl7v2.model.v27.message.ORU_R01;
import ca.uhn.hl7v2.model.v27.segment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.module.report.constants.ReportType;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOGet;
import vn.com.itechcorp.ris.dto.ConsumableDTO;
import vn.com.itechcorp.ris.dto.ConsumableType;
import vn.com.itechcorp.ris.dto.PatientDTO;
import vn.com.itechcorp.ris.dto.UserDTO;
import vn.com.itechcorp.util.*;

import java.util.*;

@Service("reportHl7Service")
public class ReportHl7ServiceImpl implements ReportHl7Service {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.url}")
    private String serverUrl;

    private Set<String> containCreator() {
        HashSet<String> strings = new HashSet<>();
        strings.add("DX");
        strings.add("CR");
        strings.add("DR");
        strings.add("CT");
        strings.add("MR");
        return strings;
    }

    private MSH createMSH(MSH msh, String accessionNumber) throws HL7Exception {
        msh.getMsh1_FieldSeparator().setValue("|");
        msh.getMsh2_EncodingCharacters().setValue("^~\\&");
        msh.getMsh3_SendingApplication().getHd1_NamespaceID().setValue("PACS");
        msh.getMsh4_SendingFacility().getHd1_NamespaceID().setValue("0");
        msh.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("HIS");
        String createdTime = Util.yyyyMMddHHmmss.get().format(new Date());
        msh.getMsh7_DateTimeOfMessage().setValue(createdTime);
        msh.getMsh9_MessageType().getMsg1_MessageCode().setValue("ORU");
        msh.getMsh9_MessageType().getMsg2_TriggerEvent().setValue("R01");
        msh.getMsh10_MessageControlID().setValue(accessionNumber + "-" + createdTime);
        msh.getMsh11_ProcessingID().getPt1_ProcessingID().setValue("P");
        msh.getMsh12_VersionID().getVersionID().setValue("2.7");
        msh.getMsh17_CountryCode().setValue("VNM");
        msh.getMsh18_CharacterSet(0).setValue("UTF-8");
        return msh;
    }

    private PID createPID(PID pid, PatientDTO patientDTO) throws HL7Exception {
        pid.getPid1_SetIDPID().setValue("1");

        pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(patientDTO.getPid());
        pid.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue(patientDTO.getFullname());
        pid.getPid7_DateTimeOfBirth().setValue(patientDTO.getBirthDate());
        pid.getPid8_AdministrativeSex().getCwe1_Identifier().setValue(patientDTO.getGender());
        return pid;
    }

    private OBR createOBR(OBR obr, ReportDTOGet report) throws HL7Exception {
        obr.getObr1_SetIDOBR().setValue("1");
        obr.getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().setValue(report.getAccessionNumber());
        obr.getObr4_UniversalServiceIdentifier().getCwe1_Identifier().setValue(report.getProcedureCode());
        obr.getObr4_UniversalServiceIdentifier().getCwe2_Text().setValue(report.getProcedureName());
        obr.getObr6_RequestedDateTime().setValue(report.getCreatedDatetime());
        obr.getObr7_ObservationDateTime().setValue(report.getApprovedDatetime());
        obr.getObr24_DiagnosticServSectID().setValue(report.getModality());

        if (report.getOperators() == null) {
            logger.error("ReportId-{} cannot read operator information", report.getId());
        } else {
            List<UserDTO> operators = JsonUtils.getInstance().jsonToListObject(report.getOperators(), UserDTO.class);
            if (operators == null || operators.isEmpty()) {
                logger.error("ReportId-{} cannot parse operator information", report.getId());
            } else {
                obr.getObr34_Technician(0).getNdl1_Name().getCnn1_IDNumber().setValue(operators.get(0).getCode());
                obr.getObr34_Technician(0).getNdl1_Name().getCnn2_FamilyName().setValue(operators.get(0).getName());

                operators.remove(0);
                // If DX | CR | DR -> Creator = KTV 2
                if (containCreator().contains(report.getModality())) {
                    UserDTO creator = JsonUtils.getInstance().jsonToObject(report.getCreator(), UserDTO.class);
                    if (creator != null) {
                        obr.getObr34_Technician(1).getNdl1_Name().getCnn1_IDNumber().setValue(creator.getCode());
                        obr.getObr34_Technician(1).getNdl1_Name().getCnn2_FamilyName().setValue(creator.getName());
                    }
                }
                // Operator = KTV
                else if (!operators.isEmpty()){
                    obr.getObr34_Technician(1).getNdl1_Name().getCnn1_IDNumber().setValue(operators.get(0).getCode());
                    obr.getObr34_Technician(1).getNdl1_Name().getCnn2_FamilyName().setValue(operators.get(0).getName());
                }
            }
        }
        return obr;
    }

    private OBR createOBR(OBR obr, String accessionNumber, String procedureCode) throws HL7Exception {
        obr.getObr1_SetIDOBR().setValue("1");
        obr.getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().setValue(accessionNumber);
        obr.getObr4_UniversalServiceIdentifier().getCwe1_Identifier().setValue(procedureCode);
        obr.getObr6_RequestedDateTime().setValue(Util.yyyyMMddHHmmss.get().format(new Date()));
        return obr;
    }

    private ORC createORC(ORC orc, ReportType type) throws DataTypeException {

        orc.getOrc1_OrderControl().setValue(type.getType());

        return orc;
    }

    private OBX createOBX(ORU_R01 oruR01, Integer count, ReportDTOGet report, String pdfPath) throws HL7Exception {
        OBX obx = oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(count - 1).getOBX();
        obx.getObx1_SetIDOBX().setValue(count.toString());
        obx.getObx2_ValueType().setValue("TX");
        obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue(report.getModalityCode());
        obx.getObx4_ObservationSubID().setValue(report.getStudyIUID());

        ST st = new ST(oruR01);

        String body = HtmlUtil.getInstance().convertToText(report.getBodyHTML());
        if (body == null) {
            logger.error("ReportId-{} cannot convert from body html to text information", report.getId());
            throw new HL7Exception("cannot convert from body html to text information");
        }
        List<ConsumableDTO> consumables = JsonUtils.getInstance().jsonToListObject(report.getConsumables(), ConsumableDTO.class);
        if (consumables == null || consumables.isEmpty()) {
            // May be null
            logger.info("ReportId-{} not contain consumable information", report.getId());
        }

        switch (count) {
            case 1: {
                st.setValue(body);
                break;
            }
            case 2: {
                String conclusion = HtmlUtil.getInstance().convertToText(report.getConclusionHTML());
                if (conclusion == null) {
                    logger.info("ReportId-{} Not contain conclusion html,cannot convert conclusion html", report.getId());
                }
                st.setValue(conclusion);
                break;
            }
            case 3: {
                String viewerUrl = serverUrl + "/viewer?accessionNumber=" + report.getOrderNumber();
                st.setValue(viewerUrl);
                break;
            }
            case 4: {
                st.setValue(pdfPath);
                break;
            }
            case 5: {
                if (consumables != null) {
                    Optional<ConsumableDTO> film = consumables.stream().filter(consumable -> consumable.getType().equals(ConsumableType.FILM.name())).findFirst();
                    if (film.isPresent()) {
                        String rFilm = film.get().getQuantity() + ";" + film.get().getCode();
                        st.setValue(rFilm);
                    }
                }
                break;
            }
            case 6: {
                if (consumables != null) {
                    Optional<ConsumableDTO> medicine = consumables.stream().filter(consumable -> consumable.getType().equals(ConsumableType.CONTRAST_MEDIA.name())).findFirst();
                    if (medicine.isPresent()) {
                        String m = medicine.get().getQuantity() + ";" + medicine.get().getCode();
                        st.setValue(m);
                    }
                }
                break;
            }
            case 7: {
                String bodyRtf = TextUtil.getInstance().convertToRTF(body);
                if (bodyRtf == null) {
                    logger.error("ReportId-{} cannot create rtf from body html information", report.getId());
                    throw new HL7Exception("cannot create rtf from body html information");
                }
                st.setValue(Base64Util.encode(bodyRtf));
                break;
            }
        }
        obx.getObx5_ObservationValue(0).setData(st);
        obx.getObx11_ObservationResultStatus().setValue("F");
        obx.getObx14_DateTimeOfTheObservation().setValue(report.getApprovedDatetime());

        UserDTO approver = JsonUtils.getInstance().jsonToObject(report.getApprover(), UserDTO.class);

        if (approver == null) {
            logger.error("ReportId-{} cannot parse approver information", report.getId());
            throw new HL7Exception("cannot parse approver information");
        }
        obx.getObx16_ResponsibleObserver(0).getXcn1_PersonIdentifier().setValue(approver.getCode());
        obx.getObx16_ResponsibleObserver(0).getXcn2_FamilyName().getSurname().setValue(approver.getName());

        obx.getObx19_DateTimeOfTheAnalysis().setValue(report.getOperationDatetime());
        obx.getObx23_PerformingOrganizationName().getOrganizationName().setValue(report.getModalityRoom());
        return obx;
    }

    @Override
    public HisRequest createReport(ReportDTOGet object, String pdfPath, ReportType type) throws HL7Exception {
        try {
            ORU_R01 oruR01 = new ORU_R01();
            oruR01.getParser().getParserConfiguration().setValidating(false);

            createMSH(oruR01.getMSH(), object.getAccessionNumber());

            createORC(oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getORC(), type);

            if (object.getPatient() == null) {
                logger.error("ReportId-{} cannot read patient information", object.getId());
                throw new HL7Exception("Cannot read patient information");
            }
            PatientDTO patientDTO = JsonUtils.getInstance().jsonToObject(object.getPatient(), PatientDTO.class);

            if (patientDTO == null) {
                logger.error("ReportId-{} cannot parse patient information to Object", object.getId());
                throw new HL7Exception("Cannot parse patient information");
            }
            createPID(oruR01.getPATIENT_RESULT().getPATIENT().getPID(), patientDTO);
            createOBR(oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR(), object);

            createOBX(oruR01, 1, object, pdfPath);
            createOBX(oruR01, 2, object, pdfPath);
            createOBX(oruR01, 3, object, pdfPath);
            createOBX(oruR01, 4, object, pdfPath);
            createOBX(oruR01, 5, object, pdfPath);
            createOBX(oruR01, 6, object, pdfPath);
            createOBX(oruR01, 7, object, pdfPath);

            String hl7Message = oruR01.toString();

            logger.info("ReportID-{} convert to HL7 message \n{}", object.getId(), hl7Message.replaceAll("\r", "\n"));

            return new HisRequest(Base64Util.encode(hl7Message));

        } catch (Exception ex) {
            logger.error("ReportID-{} cannot create HL7 Message. Error-{}", object.getId(), ex.getMessage());
            return null;
        }
    }

    @Override
    public HisRequest deleteReport(String accessionNumber, String procedureCode) {
        try {
            ORU_R01 oruR01 = new ORU_R01();
            oruR01.getParser().getParserConfiguration().setValidating(false);
            createMSH(oruR01.getMSH(), accessionNumber);
            createORC(oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getORC(), ReportType.DELETE);
            createOBR(oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR(), accessionNumber, procedureCode);

            String hl7Message = oruR01.toString();

            logger.info("[Delete-Report]AccNo-{} convert to HL7 message \n{}", accessionNumber, hl7Message.replaceAll("\r", "\n"));

            return new HisRequest(Base64Util.encode(hl7Message));
        } catch (Exception ex) {
            logger.error("[Delete-Report]AccNo-{} cannot create HL7 Message. Error-{}", accessionNumber, ex.getMessage());
            return null;
        }
    }
}
