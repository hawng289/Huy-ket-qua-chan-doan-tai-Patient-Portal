package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProcedureDTO extends Dto {
    private String procedureCode;
    private String procedureName;
    private String modalityType;
    private boolean activated;

    public ProcedureDTO(String procedureCode, String procedureName, String modalityType, boolean activated) {
        this.procedureCode = procedureCode;
        this.procedureName = procedureName;
        this.modalityType = modalityType;
        this.activated = activated;
    }
}
