package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDTO extends Dto {
    private String departmentCode;
    private String departmentName;
    private String departmentDescription;
    private boolean activated;

    public DepartmentDTO(String departmentCode, String departmentName, String departmentDescription, boolean activated) {
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.departmentDescription = departmentDescription;
        this.activated = activated;
    }
}
