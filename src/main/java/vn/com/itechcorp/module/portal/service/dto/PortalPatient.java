package vn.com.itechcorp.module.portal.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import vn.com.itechcorp.base.service.dto.Dto;
import vn.com.itechcorp.ris.dto.PatientDTO;

@Getter
@Setter
@NoArgsConstructor
public class PortalPatient extends Dto {

    private Long id;

    private String initialSecret;

    private String pid;

    private String fullname;

    private String gender;

    private String birthDate;

    private String phone;

    private String email;

    private String address;

    public PortalPatient(PatientDTO object) {
        this.pid = object.getPid();
        this.fullname = object.getFullname();
        this.birthDate = object.getBirthDate();
        this.initialSecret = object.getInitialSecret();
        this.gender = object.getGender();
        this.email = object.getEmail();
        this.phone = object.getPhone();
        this.address = object.getAddress();
    }

}