package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProcedureDTOGet extends Dto {

    private Long id;

    private Long approvedReportID;

    private String icdCode;

    private String requestedDatetime;

    private String requestedModalityType;

    private String requestedNumber;

    private String requestedProcedureCode;

    private String requestedProcedureName;

}

