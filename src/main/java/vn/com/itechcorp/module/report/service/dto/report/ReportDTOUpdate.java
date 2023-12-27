package vn.com.itechcorp.module.report.service.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoUpdate;
import vn.com.itechcorp.module.report.persitance.Report;

@Getter
@Setter
@NoArgsConstructor
public class ReportDTOUpdate extends DtoUpdate<Report, Long> {

    private Boolean reCall;

    private Boolean hisStatus;

    private Integer numOfRetry;

    @Override
    public boolean apply(Report report) {
        boolean modified = false;
        if (reCall != null && reCall != report.isReCall()) {
            report.setReCall(reCall);
            modified = true;
        }
        if (hisStatus != null) {
            report.setHisStatus(hisStatus);
            modified = true;
        }
        if (numOfRetry != null){
            report.setNumOfRetries(numOfRetry);
            modified = true;
        }
        return modified;
    }
}
