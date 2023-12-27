package vn.com.itechcorp.worklist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dcm4che3.util.UIDUtils;
import vn.com.itechcorp.ris.dto.OrderDTO;
import vn.com.itechcorp.util.TextUtil;
import vn.com.itechcorp.util.Util;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class WorklistDTO implements Serializable {

    private String pid;

    private String patientName;

    private String patientGender;

    private String patientBirthDate;

    private String modalityType;

    private String orderDate;

    private String orderTime;

    private String requestedProcedureID;

    private String requestedProcedureDescription;

    private String procedureStepStatus;

    private String studyInstanceUID;

    private String orderNumber;

    private String accessionNumber;

    private String requestDepartmentID;

    private String requestDepartmentName;

    private String referDoctorID;

    private String referDoctorName;

    public WorklistDTO(OrderDTO object) {
        pid = TextUtil.getInstance().toCleanText(object.getPatient().getPid());
        patientBirthDate = TextUtil.getInstance().toCleanText(object.getPatient().getBirthDate());
        patientGender = TextUtil.getInstance().toCleanText(object.getPatient().getGender());
        modalityType = TextUtil.getInstance().toCleanText(object.getModalityType());
        patientName = TextUtil.getInstance().toASCII(object.getPatient().getFullname(), false) + " " + (patientBirthDate.trim().length() > 4 ? patientBirthDate.substring(0, 4) : "");
        if (modalityType.equals("DR")) {
            modalityType = "DX";
        }

        String orderDatetime = object.getOrderDatetime();
        if (orderDatetime == null) orderDatetime = Util.yyyyMMddHHmmss.get().format(new Date());
        setOrderDate(TextUtil.getInstance().toCleanText(orderDatetime.substring(0, 8)));
        setOrderTime(TextUtil.getInstance().toCleanText(orderDatetime.substring(8)));


        requestedProcedureID = TextUtil.getInstance().toCleanText(object.getAccessionNumber());
        if (object.getServices() != null && !object.getServices().isEmpty()) {
            requestedProcedureDescription = TextUtil.getInstance().toASCII(object.getServices().get(0).getRequestedProcedureName(), false);
            if (requestedProcedureDescription != null && requestedProcedureDescription.length() > 64)
                requestedProcedureDescription = TextUtil.getInstance().toCleanText(requestedProcedureDescription.substring(0, 64));

        }
        procedureStepStatus = "SCHEDULED";
        studyInstanceUID = UIDUtils.createUID();
        orderNumber = TextUtil.getInstance().toCleanText(object.getAccessionNumber());
        accessionNumber = TextUtil.getInstance().toCleanText(object.getAccessionNumber());
        requestDepartmentID = TextUtil.getInstance().toCleanText(object.getRequestedDepartmentCode());

        requestDepartmentName = TextUtil.getInstance().toASCII(object.getRequestedDepartmentName(), false);
        if (requestDepartmentName != null && requestDepartmentName.length() > 64)
            requestDepartmentName = requestDepartmentName.substring(0, 64);

        referDoctorID = TextUtil.getInstance().toCleanText(object.getReferringPhysicianCode());
        referDoctorName = TextUtil.getInstance().toASCII(object.getReferringPhysicianName(), false);
    }
}
