package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOCreate;
import vn.com.itechcorp.util.JsonUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ReportDTO extends Dto {

    private Long id;

    private PatientDTO patient;

    @NotNull
    private String orderNumber;

    @NotNull
    private String accessionNumber;

    private String orderDatetime;

    private String requestNumber;

    @NotNull
    private String procedureCode;

    private String procedureName;

    private ModalityDTO modality;

    private UserDTO creator;

    @NotNull
    private UserDTO approver;

    private List<UserDTO> operators;

    private String bodyHTML;

    private String conclusionHTML;

    private String note;

    private String createdDatetime;

    private String approvedDatetime;

    private String operationDatetime;

    private List<FileDTO> keyImages;

    private List<ConsumableDTO> consumables;

    private String studyInstanceUID;

    private Map<String,String> attributes;

    public ReportDTOCreate toReportDTOCreate() {
        ReportDTOCreate dtoCreate = new ReportDTOCreate();
        dtoCreate.setRisReportID(this.id);
        dtoCreate.setAccessionNumber(this.accessionNumber);
        dtoCreate.setOrderNumber(this.orderNumber);
        dtoCreate.setPatient(JsonUtils.getInstance().toJsonString(patient));
        dtoCreate.setStudyIUID(this.studyInstanceUID);
        dtoCreate.setOrderDatetime(this.orderDatetime);
        dtoCreate.setRequestNumber(this.requestNumber);
        dtoCreate.setProcedureCode(this.procedureCode);
        dtoCreate.setProcedureName(this.procedureName);
        dtoCreate.setModality(this.getModality() == null ? null : this.getModality().getModalityType());
        dtoCreate.setModalityCode(this.getModality() == null ? null : this.getModality().getCode());
        dtoCreate.setModalityRoom(this.getModality() == null ? null : this.getModality().getRoomCode());
        dtoCreate.setCreator(JsonUtils.getInstance().toJsonString(creator));
        dtoCreate.setApprover(JsonUtils.getInstance().toJsonString(approver));
        dtoCreate.setOperators(JsonUtils.getInstance().toJsonString(operators));
        dtoCreate.setBodyHTML(this.bodyHTML);
        dtoCreate.setConclusionHTML(this.conclusionHTML);
        dtoCreate.setNote(this.note);
        dtoCreate.setCreatedDatetime(this.createdDatetime);
        dtoCreate.setApprovedDatetime(this.approvedDatetime);
        dtoCreate.setOperationDatetime(this.operationDatetime);
        dtoCreate.setConsumables(JsonUtils.getInstance().toJsonString(this.consumables));
        dtoCreate.setKeyImages(JsonUtils.getInstance().toJsonString(this.keyImages));
        dtoCreate.setIsCreate(true);
        return dtoCreate;
    }
}
