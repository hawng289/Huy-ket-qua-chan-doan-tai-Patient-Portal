package vn.com.itechcorp.module.portal.service;

import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.itechcorp.module.portal.proxy.PatientPortalProxy;


public class PatientPortalServiceImpl implements PatientPortalService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PatientPortalProxy patientPortalProxy;


    @Override
    public Response deleteByReportId(Long id) {
        return patientPortalProxy.deleteReport(id);
    }
}
