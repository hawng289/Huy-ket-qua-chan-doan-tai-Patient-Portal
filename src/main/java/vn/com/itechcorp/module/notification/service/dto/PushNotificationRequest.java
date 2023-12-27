package vn.com.itechcorp.module.notification.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import vn.com.itechcorp.ris.dto.Dto;

import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class PushNotificationRequest extends Dto {

    @JsonIgnore
    @Value("${notification.hospital.id:507302}")
    private String hospitalID;

    private int Id = 0;

    private int Category = 3;

    private String Description; // khách hàng + tenbenhnhan+ đã có kết quả cận lâm sàng - tendichvucls

    private String CreatedUserId = "";

    private String UpdatedUserId= "";

    private String Title = "Kết quả CLS khám bệnh";

    private String Content; // khách hàng + tenbenhnhan+ đã có kết quả cls khám bệnh

    private String Image = "";

    private Date CreatedDate = new Date(); // Now

    private Date UpdatedDate = new Date(); // Now

    private boolean IsPushed = true;

    private Date CreatedPush = new Date(); // Now

    private String MedicineCode; // PID

    private String path; // PDF path

    public PushNotificationRequest(String patientName, String procedureName, String pid, String path) {
        Description = "Khách hàng "+ patientName+ " đã có kết quả cls-"+procedureName;
        Content = "Khách hàng "+ patientName+ " đã có kết quả cls khám bệnh";
        MedicineCode = hospitalID+"."+pid;
        this.path = path;
    }
}
