package vn.com.itechcorp.module.report.persitance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.persistence.model.AuditableSerialIDEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "file_signed")
@Getter
@Setter
@NoArgsConstructor
public class FileSigned extends AuditableSerialIDEntry {

    @Column(name = "accession_number")
    private String accessionNumber;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "procedure_code")
    private String procedureCode;

    @Column(name = "signer")
    private String signer;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "re_call")
    private boolean reCall;

    @Column(name = "temp_path")
    private String tempPath;
}
