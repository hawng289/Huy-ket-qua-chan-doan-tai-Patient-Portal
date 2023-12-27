package vn.com.itechcorp.module.report.service.dto.reportsent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.report.persitance.ReportSent;

@Getter
@Setter
@NoArgsConstructor
public class ReportSendDTOCreate extends SerialIDDtoCreate<ReportSent> {

    private String accessionNumber;

    private Long reportId;

    private Long fileId;

    private boolean hisStatus;

    private String errorDetail;

    private String request;

    @Override
    public ReportSent toEntry() {
        ReportSent entry = new ReportSent();
        entry.setAccessionNumber(accessionNumber);
        entry.setReportId(reportId);
        entry.setFileId(fileId);
        entry.setRequest(request);
        entry.setHisStatus(hisStatus);
        entry.setErrorDetail(errorDetail);
        return entry;
    }
}
