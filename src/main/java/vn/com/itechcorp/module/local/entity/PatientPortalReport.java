package vn.com.itechcorp.module.local.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.BaseDbEntry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@Entity
@Table(name = "patient_portal_report")
@NoArgsConstructor
public class PatientPortalReport extends BaseDbEntry<Long> {
    @NotNull
    String accessionNumber;
    String orderNumber;
    Long reportId;
    @NotNull
    String procedureCode;
    String request;
    String availableAt;
    @NotNull
    PatientPortalReportStatus status = PatientPortalReportStatus.PENDING;
    String response;
    @NotNull
    int numOfRetries = 0;
    @NotNull
    String createdTime;
    @NotNull
    Integer creatorId;
    String lastUpdatedTime;
    Integer  lastUpdatedId;

    boolean voided = false;
    String voidedTime;
    String voidedBy;
    String voidReason;


}
