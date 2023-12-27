package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDTOGet extends Dto {

    private PatientDTO patient;

    private String modalityType;

    private String encounterNumber;

    private String accessionNumber;

    private String clinicalDiagnosis;

    private String priority;

    private String instructions;

    private String description;

    private Date orderDate;

    private String requestDepartmentCode;

    private String requestDepartmentName;

    private String referringPhysicianCode;

    private String referringPhysicianName;

    private String procedureStepStatus;

    private String diagnosisStepStatus;

    private List<OrderProcedureDTOGet> services;
}
