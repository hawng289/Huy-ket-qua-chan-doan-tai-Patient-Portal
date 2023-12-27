package vn.com.itechcorp.ris.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.com.itechcorp.ris.dto.PdfReportDTO;
import vn.com.itechcorp.ris.dto.ReportDTO;
import vn.com.itechcorp.ris.dto.RisMWLResponse;
import vn.com.itechcorp.ris.service.RisService;

import javax.validation.Valid;

@Api(value = "ris-it-api", tags = "ris-it-api")
@RestController
public class RisAPI {

    @Autowired
    private RisService risService;

    @ApiOperation(value = "Receive report")
    @PostMapping("/report")
    public RisMWLResponse createReport(@RequestBody @Valid ReportDTO object) {
        try {
            return risService.createReport(object);
        } catch (Exception ex) {
            return new RisMWLResponse(false, ex.getMessage());
        }
    }

    @ApiOperation(value = "Receive report pdf")
    @PostMapping("/pdf")
    public RisMWLResponse createPdfReport(@RequestBody @Valid PdfReportDTO object) {
        try {
            return risService.createPdfReport(object);
        } catch (Exception ex) {
            return new RisMWLResponse(false, ex.getMessage());
        }
    }

    @GetMapping("/lock/{orderNumber}")
    public RisMWLResponse lockOrder(@PathVariable String orderNumber){
        try {
            return risService.lockOrder(orderNumber);
        } catch (Exception ex){
            return new RisMWLResponse(false,ex.getMessage());
        }
    }


    @ApiOperation(value = "Remove report")
    @DeleteMapping("/report/{accessionNumber}/procedure/{procedureCode}")
    public RisMWLResponse removeReport(
            @PathVariable("accessionNumber") String accessionNumber,
            @PathVariable("procedureCode") String procedureCode) {
        try {
            return risService.removeReport(accessionNumber, procedureCode);
        } catch (Exception ex) {
            return new RisMWLResponse(false, ex.getMessage());
        }
    }

}
