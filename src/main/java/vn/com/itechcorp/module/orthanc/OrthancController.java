package vn.com.itechcorp.module.orthanc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.dto.StudyDTO;

@RestController
@RequestMapping("/ws/rest/orthanc")
public class OrthancController {

    @Autowired
    private OrthancService orthancService;

    @PostMapping
    public RisResponse createStudy(@RequestBody StudyDTO study) {
        return this.orthancService.createStudy(study);
    }
}
