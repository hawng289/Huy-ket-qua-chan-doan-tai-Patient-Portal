package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
public class HisReportStatusUpdate extends Dto{

    @NotNull
    private HisReportStatus hisReportStatus;

    @NotNull
    private String hisReportStatusTime;

    private String hisMessage;
}
