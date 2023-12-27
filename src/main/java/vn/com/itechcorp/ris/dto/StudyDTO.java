package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyDTO extends Dto {
    private String accessionNumber;
    private String aeTitle;
    private String birthDate;
    private String bodyPartExamined;
    private String description;
    private String gender;
    private String institutionName;
    private String ipAddress;
    private String manufacturerModelName;
    private String modalityType;
    private Integer numOfImages;
    private Integer numOfSeries;
    private String operatorName;
    private String patientId;
    private String patientName;
    private String referringPhysician;
    private String stationName;
    private String studyDate;
    private String studyInstanceUID;
    private String studyTime;
}
