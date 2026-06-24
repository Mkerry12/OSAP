package com.mqq.controller.admin;

import com.mqq.result.PageResult;
import com.mqq.result.Result;
import com.mqq.service.FillService;
import com.mqq.vo.ResponseDetailVO;
import com.mqq.vo.ResponseListItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminFillController")
@RequestMapping("admin/fill")
public class FillController {

    @Autowired
    private FillService fillService;


    @GetMapping("/surveys/{surveyId}/responses")
    public Result<PageResult<ResponseListItemVO>> getResponseList(
            @PathVariable("surveyId") Long surveyId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("获取答卷列表: surveyId={}, page={}, size={}", surveyId, page, size);
        PageResult<ResponseListItemVO> pageResult = fillService.getResponseList(surveyId, page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/surveys/{surveyId}/responses/{responseId}")
    public Result<ResponseDetailVO> getResponseDetail(@PathVariable("surveyId") Long surveyId,
                                                      @PathVariable("responseId") Long responseId) {
        log.info("获取答卷详情: surveyId={}, responseId={}", surveyId, responseId);
        return fillService.getResponseDetail(surveyId, responseId);
    }

}
