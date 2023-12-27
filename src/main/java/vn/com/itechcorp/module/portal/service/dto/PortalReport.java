package vn.com.itechcorp.module.portal.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.Dto;
import vn.com.itechcorp.ris.dto.ReportDTO;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
public class PortalReport extends Dto {
    private Long id;

    private Long patientID;

    private PortalPatient patient;

    @NotNull
    private String orderNumber;

    @NotNull
    private String accessionNumber;


    private String orderDatetime;

    @NotNull
    private String procedureCode;

    private String procedureName;

    private String modalityType;

    @NotNull
    private PortalUser approver;

    private String bodyHTML;

    private String conclusionHTML;

    private String note;


    private String createdDatetime;


    private String approvedDatetime;

    private String studyInstanceUID;

    public PortalReport(ReportDTO report) {
        id = report.getId();
        patient = new PortalPatient(report.getPatient());
        patientID = report.getPatient().getId();
        orderNumber = report.getOrderNumber();
        accessionNumber = report.getAccessionNumber();
        orderDatetime = report.getOrderDatetime();
        modalityType = report.getModality().getModalityType();
        procedureCode = report.getProcedureCode();
        procedureName = report.getProcedureName();
        approver = new PortalUser(report.getApprover());
        approvedDatetime = report.getApprovedDatetime();
        bodyHTML = report.getBodyHTML();
        conclusionHTML = report.getConclusionHTML();
        note = report.getNote();
        createdDatetime = report.getCreatedDatetime();
        studyInstanceUID = report.getStudyInstanceUID();
    }
}