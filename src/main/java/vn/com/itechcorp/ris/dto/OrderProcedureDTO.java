package vn.com.itechcorp.ris.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderProcedureDTO extends Dto {

    private String icdCode;

    private String requestedDatetime;

    private String requestedModalityType;

    private String requestedNumber;

    private String requestedProcedureCode;

    private String requestedProcedureName;
}

