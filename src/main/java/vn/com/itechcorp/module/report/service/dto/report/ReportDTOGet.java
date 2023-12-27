package vn.com.itechcorp.module.report.service.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.report.persitance.Report;

@Setter
@Getter
@NoArgsConstructor
public class ReportDTOGet extends DtoGet<Report, Long> {

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

    private Integer numOfRetries;

    public ReportDTOGet(Report report) {
        super(report);
    }

    @Override
    public void parse(Report report) {
        this.accessionNumber = report.getAccessionNumber();
        this.orderNumber = report.getOrderNumber();
        this.patient = report.getPatient();
        this.studyIUID = report.getStudyIUID();
        this.orderDatetime = report.getOrderDatetime();
        this.modalityRoom = report.getModalityRoom();
        this.requestNumber = report.getRequestNumber();
        this.procedureCode = report.getProcedureCode();
        this.procedureName = report.getProcedureName();
        this.modality = report.getModality();
        this.modalityCode = report.getModalityCode();
        this.creator = report.getCreator();
        this.approver = report.getApprover();
        this.operators = report.getOperators();
        this.bodyHTML = report.getBodyHTML();
        this.conclusionHTML = report.getConclusionHTML();
        this.note = report.getNote();
        this.createdDatetime = report.getCreatedDatetime();
        this.approvedDatetime = report.getApprovedDatetime();
        this.operationDatetime = report.getOperationDatetime();
        this.consumables = report.getConsumables();
        this.isCreate = report.getIsCreate();
        this.numOfRetries = report.getNumOfRetries();
    }
}
