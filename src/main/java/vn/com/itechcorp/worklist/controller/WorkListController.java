package vn.com.itechcorp.worklist.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.com.itechcorp.worklist.dto.MwlItemFilter;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

@Api(value = "wl-it-api", tags = "wl-it-api")
@RestController
@RequestMapping("/mwl")
public class WorkListController {

    @Autowired
    private WorklistService worklistService;


    @PostMapping("/search")
    @ApiOperation(value = "Search worklist")
    public String searchWorkList(@RequestBody MwlItemFilter filter) {
        return this.worklistService.searchWorkList(filter);
    }

    @PostMapping
    @ApiOperation(value = "Create worklist")
    public WorklistResponse createSPS(@RequestBody WorklistDTO worklist) {
        return this.worklistService.sendWorklist(worklist);
    }

    @DeleteMapping("/{accessionNumber}")
    @ApiOperation(value = "Delete worklist")
    public WorklistResponse deleteSPS(@PathVariable String accessionNumber) {
        return this.worklistService.removeWorklist(accessionNumber);
    }

}
