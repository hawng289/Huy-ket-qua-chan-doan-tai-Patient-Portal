package vn.com.itechcorp.module.local.service;

import vn.com.itechcorp.module.local.entity.PatientPortalReport;
import vn.com.itechcorp.module.local.repository.PatientPortalReportRepository;

import java.util.List;

public class PatientPortalReportServiceImpl implements PatientPortalReportService{
    PatientPortalReportRepository patientPortalReportRepository;


    @Override
    public PatientPortalReport findByAccessionNumberAndProcedureCode(String accessionNumber, String procedureCode) {
        List<PatientPortalReport> patientPortalReport  = patientPortalReportRepository.findByAccessionNumberAndProcedureCode(accessionNumber, procedureCode);
        return patientPortalReport == null ? null: patientPortalReport.get(0);
    }
}
