package vn.com.itechcorp.module.local.service;

import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.entity.PatientPortalReport;

@Service("patientPortalReportService")
public interface PatientPortalReportService {
    PatientPortalReport findByAccessionNumberAndProcedureCode(String accessionNumber, String procedureCode);


}
