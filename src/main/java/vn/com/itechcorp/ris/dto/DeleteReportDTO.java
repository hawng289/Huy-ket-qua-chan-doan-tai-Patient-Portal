package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class DeleteReportDTO extends Dto {

    private PatientDTO patient;

    @NotNull
    private String orderNumber;

    @NotNull
    private String accessionNumber;

    @NotNull
    private String procedureCode;

    private String procedureName;

    private String modalityType;

    @NotNull
    private UserDTO approver;

    private String createdDatetime;

    private String approvedDatetime;
}
