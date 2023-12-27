package vn.com.itechcorp.module.orthanc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.dto.StudyDTO;
import vn.com.itechcorp.ris.service.RisService;

@Service("orthancService")
public class OrthancService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RisService risService;

    public RisResponse createStudy(StudyDTO study) {
        logger.info("[Orthanc-SendStudy] study-{}", study);
        try {
            return this.risService.createStudy(study);
        } catch (Exception ex) {
            logger.error("[Orthanc-SendStudy] FAILED", ex);
            return new RisResponse(null, ex.getMessage());
        }
    }
}
