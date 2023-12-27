package vn.com.itechcorp.module.report.persitance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.BaseSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
public class Report extends BaseSerialIDEntry {

    @Column(name = "ris_report_id")
    private Long risReportId; // Can remove patient portal

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "patient")
    private String patient;

    @Column(name = "study_IUID")
    private String studyIUID;

    @Column(name = "order_datetime")
    private String orderDatetime;

    @Column(name = "modality_room")
    private String modalityRoom;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "procedure_code")
    private String procedureCode;

    @Column(name = "procedure_name")
    private String procedureName;

    @Column(name = "modality")
    private String modality;

    @Column(name = "modality_code")
    private String modalityCode;

    @Column(name = "creator")
    private String creator;

    @Column(name = "approver")
    private String approver;

    @Column(name = "operators")
    private String operators;

    @Column(name = "body_html")
    private String bodyHTML;

    @Column(name = "conclusion_html")
    private String conclusionHTML;

    @Column(name = "note")
    private String note;

    @Column(name = "created_datetime")
    private String createdDatetime;

    @Column(name = "approved_datetime")
    private String approvedDatetime;

    @Column(name = "operation_datetime")
    private String operationDatetime;

    @Column(name = "consumables")
    private String consumables;

    @Column(name = "key_images")
    private String keyImages;

    @Column(name = "is_create")
    private Boolean isCreate; // Type create report or delete report

    @Column(name = "re_call")
    private boolean reCall; // Voided

    @Column(name = "his_status")
    private Boolean hisStatus = false; //

    @Column(name = "msg_time")
    private Date messageTime; // Now

    @Column(name = "num_of_retries")
    private int numOfRetries; // Num of retries

    @PrePersist
    void prePersist() {
        setMessageTime(new Date());
    }
}
