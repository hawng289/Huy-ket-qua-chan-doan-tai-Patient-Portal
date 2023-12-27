package vn.com.itechcorp.ris.dto;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v27.segment.PID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PatientDTO extends Dto {

    private Long id;

    @NotNull
    private String pid;

    @NotNull
    private String fullname;

    private String gender;

    private String birthDate;

    private String phone;

    private String email;

    private String address;

    private String initialSecret;

    private String companyName;


    public PatientDTO(PID pidSegment) throws Exception {

        String pid = pidSegment.getPid3_PatientIdentifierList(0).getCx1_IDNumber().getValue();
        if (pid == null || pid.isEmpty()) throw new HL7Exception("Invalid null {pid}");

        String firstName = pidSegment.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().getValue();
        String lastName = pidSegment.getPid5_PatientName(0).getXpn2_GivenName().getValue();
        String birthDate = pidSegment.getPid7_DateTimeOfBirth().getValue();
        String gender = pidSegment.getPid8_AdministrativeSex().getCwe1_Identifier().getValue();
        String address = pidSegment.getPid11_PatientAddress(0).getStreetAddress().getSad1_StreetOrMailingAddress().getValue();

        // Adding company name
        String company = pidSegment.getPid11_PatientAddress(0).getStreetAddress().getSad2_StreetName().getValue();


        String email = pidSegment.getPid13_PhoneNumberHome(0).getXtn4_CommunicationAddress().getValue();
        String phone = pidSegment.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().getValue();

        this.setPid(pid);
        this.setFullname(lastName == null || lastName.isEmpty() ? firstName : firstName + " " + lastName);
        this.setGender(gender);
        this.setBirthDate(birthDate);
        this.setAddress(address);
        this.setPhone(phone);
        this.setEmail(email);
        this.setCompanyName(company);
    }

}
