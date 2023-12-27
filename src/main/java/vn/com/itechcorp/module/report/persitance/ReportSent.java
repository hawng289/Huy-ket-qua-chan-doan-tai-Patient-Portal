package vn.com.itechcorp.module.report.persitance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.AuditableSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "report_sent")
@Getter
@Setter
@NoArgsConstructor
public class ReportSent extends AuditableSerialIDEntry {

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "request")
    private String request;

    @Column(name = "his_status")
    private boolean hisStatus;

    @Column(name = "error_detail")
    private String errorDetail;
}
