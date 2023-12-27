package vn.com.itechcorp.module.report.service.dto.reportsent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.report.persitance.ReportSent;

@Getter
@Setter
@NoArgsConstructor
public class ReportSendDTOGet extends DtoGet<ReportSent, Long> {

    private String accessionNumber;

    private Long reportId;

    private Long fileId;

    private boolean hisStatus;

    private String errorDetail;

    @Override
    public void parse(ReportSent reportSent) {
        this.setAccessionNumber(reportSent.getAccessionNumber());
        this.setReportId(reportSent.getReportId());
        this.setFileId(reportSent.getFileId());
        this.setHisStatus(reportSent.isHisStatus());
        this.setErrorDetail(reportSent.getErrorDetail());
    }

    public ReportSendDTOGet(ReportSent reportSent) {
        super(reportSent);
    }
}
