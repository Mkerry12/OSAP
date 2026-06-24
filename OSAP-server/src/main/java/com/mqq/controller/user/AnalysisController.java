package com.mqq.controller.user;

import com.mqq.result.Result;
import com.mqq.service.AnalysisService;
import com.mqq.vo.QuestionAnalysisVO;
import com.mqq.vo.SurveyOverviewVO;
import com.mqq.vo.WordCloudVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/user/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/surveys/{surveyId}/overview")
    public Result<SurveyOverviewVO> getOverview(@PathVariable("surveyId") Long surveyId) {
        log.info("获取问卷统计概览: surveyId={}", surveyId);
        return analysisService.getOverview(surveyId);
    }

    @GetMapping("/surveys/{surveyId}/questions")
    public Result<QuestionAnalysisVO> getQuestionAnalysis(@PathVariable("surveyId") Long surveyId) {
        log.info("获取题目统计分析: surveyId={}", surveyId);
        return analysisService.getQuestionAnalysis(surveyId);
    }

    @GetMapping("/surveys/{surveyId}/wordcloud")
    public Result<WordCloudVO> getWordCloud(@PathVariable("surveyId") Long surveyId) {
        log.info("获取词云数据: surveyId={}", surveyId);
        return analysisService.getWordCloud(surveyId);
    }

    @GetMapping("/surveys/{surveyId}/export")
    public void export(@PathVariable("surveyId") Long surveyId,
                       @RequestParam(value = "format", defaultValue = "excel") String format,
                       HttpServletResponse response) throws IOException {
        log.info("导出统计数据: surveyId={}, format={}", surveyId, format);

        boolean isExcel = "excel".equalsIgnoreCase(format);
        String ext = isExcel ? "xlsx" : "csv";
        String contentType = isExcel
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv; charset=UTF-8";

        String filename = URLEncoder.encode("问卷统计_" + surveyId, StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename + "." + ext);

        try {
            analysisService.export(surveyId, format, response.getOutputStream());
        } catch (IllegalArgumentException e) {
            response.reset();
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"" + e.getMessage() + "\"}");
        }
    }
}
