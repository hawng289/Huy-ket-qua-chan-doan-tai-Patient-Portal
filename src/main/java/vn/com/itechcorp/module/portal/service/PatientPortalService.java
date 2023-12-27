package vn.com.itechcorp.module.portal.service;

import feign.Response;
import org.springframework.stereotype.Service;

@Service("patientPortalService")
public interface PatientPortalService {

    Response deleteByReportId(Long id);
}
