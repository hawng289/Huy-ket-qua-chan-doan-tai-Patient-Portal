package vn.com.itechcorp.his.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.his.service.HisService;
import vn.com.itechcorp.ris.service.RisService;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Api(value = "his-api", tags = "his-api")
@RestController
public class HisHl7Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RisService risService;

    @Autowired
    private HisService hisService;

    @ApiOperation(value = "Api will exec all message type")
    @PostMapping("/hl7")
    public HisResponse processMessage(@RequestBody HisRequest object) {
        return this.hisService.processMessage(object);
    }

    @GetMapping("/viewer")
    public void getViewerUrl(
            HttpServletResponse response,
            @RequestParam(name = "accessionNumber") String accessionNumber) {
        try {
            String viewerUrl = risService.getViewerUrl(accessionNumber);
            response.sendRedirect(viewerUrl);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getSignedPdf(@RequestParam(name = "accessionNumber") String accessionNumber) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UUID.randomUUID() + ".pdf\"")
                    .body(risService.getSignedPDF(accessionNumber));
        } catch (Exception ex) {
            logger.error("[ServiceID-{}] Errors", accessionNumber, ex);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
