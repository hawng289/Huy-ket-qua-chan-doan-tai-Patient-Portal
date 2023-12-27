package vn.com.itechcorp.ris.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO extends Dto {

    @NotNull
    private String orderNumber;

    @NotNull
    private String accessionNumber;

    private String requestedDepartmentCode;

    private String requestedDepartmentName;

    private String referringPhysicianCode;

    private String referringPhysicianName;

    private String clinicalDiagnosis;

    private Boolean urgent;

    private String instructions;

    private String orderDatetime;

    private String modalityType;

    @NotNull
    private PatientDTO patient;

    private Map<String, String> attributes;

    @NotNull
    @NotEmpty
    private List<OrderProcedureDTO> services;

    private String encounterNumber;

    private Boolean insuranceApplied;

    private String insuranceNumber;

    private String insuranceIssuedDate;

    private String insuranceExpiredDate;

    @JsonIgnore
    private boolean add = true;

}

