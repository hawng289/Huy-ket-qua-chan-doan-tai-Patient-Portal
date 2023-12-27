package vn.com.itechcorp.worklist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MwlItemFilter {
    private String accessionNumber;

    private String patientID;

    private String patientName;

    private String startDate;

    private String endDate;

    private String modality;

    private String scheduledStationAETitle;

    private SPSStatus status;

    public MwlItemFilter(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }
}
