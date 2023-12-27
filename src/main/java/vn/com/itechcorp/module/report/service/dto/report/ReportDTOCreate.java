package vn.com.itechcorp.module.report.service.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.report.persitance.Report;

@Setter
@Getter
@NoArgsConstructor
public class ReportDTOCreate extends SerialIDDtoCreate<Report> {
    private Long risReportID;

    private String accessionNumber;

    private String orderNumber;

    private String patient;

    private String studyIUID;

    private String orderDatetime;

    private String modalityRoom;

    private String requestNumber;

    private String procedureCode;

    private String procedureName;

    private String modality;

    private String modalityCode;

    private String creator;

    private String approver;

    private String operators;

    private String bodyHTML;

    private String conclusionHTML;

    private String note;

    private String createdDatetime;

    private String approvedDatetime;

    private String operationDatetime;

    private String consumables;

    private String keyImages;

    private Boolean isCreate;

    public ReportDTOCreate(String accessionNumber, String procedureCode) {
        this.accessionNumber = accessionNumber;
        this.procedureCode = procedureCode;
        this.isCreate = false;
    }

    @Override
    public Report toEntry() {
        Report report = new Report();
        report.setRisReportId(this.risReportID);
        report.setAccessionNumber(this.accessionNumber);
        report.setOrderNumber(this.orderNumber);
        report.setModalityRoom(this.modalityRoom);
        report.setPatient(this.patient);
        report.setStudyIUID(this.studyIUID);
        report.setOrderDatetime(this.orderDatetime);
        report.setRequestNumber(this.requestNumber);
        report.setProcedureCode(this.procedureCode);
        report.setProcedureName(this.procedureName);
        report.setModality(this.modality);
        report.setModalityCode(this.modalityCode);
        report.setCreator(this.creator);
        report.setApprover(this.approver);
        report.setOperators(this.operators);
        report.setBodyHTML(this.bodyHTML);
        report.setConclusionHTML(this.conclusionHTML);
        report.setNote(this.note);
        report.setCreatedDatetime(this.createdDatetime);
        report.setApprovedDatetime(this.approvedDatetime);
        report.setOperationDatetime(this.operationDatetime);
        report.setConsumables(this.consumables);
        report.setKeyImages(this.keyImages);
        report.setIsCreate(this.isCreate != null && this.isCreate);
        report.setNumOfRetries(0);
        return report;
    }
}
