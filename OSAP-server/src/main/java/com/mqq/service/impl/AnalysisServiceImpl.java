package com.mqq.service.impl;

import com.mqq.entity.*;
import com.mqq.mapper.*;
import com.mqq.result.Result;
import com.mqq.service.AnalysisService;
import com.mqq.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private AnswerMapper answerMapper;

        private static final Pattern CHINESE_PUNCTUATION = Pattern.compile("[，。！？、；：]+");
    private static final Set<String> STOP_WORDS = Set.of(
            "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都",
            "一", "个", "上", "也", "很", "到", "说", "要", "去", "你", "会",
            "着", "没有", "看", "好", "自己", "这", "那", "什么", "怎么",
            "因为", "所以", "但是", "如果", "虽然", "不过", "而且", "或者",
            "还是", "只是", "可以", "应该", "能够", "需要", "可能", "必须",
            "已经", "正在", "关于", "对于", "按照", "根据", "除了", "通过",
            "同时", "比较", "非常", "更加", "特别", "一些", "以及", "及其",
            "the", "a", "an", "is", "are", "was", "were", "be", "been",
            "it", "its", "this", "that", "to", "in", "on", "for", "and"
    );

    @Override
    public Result<SurveyOverviewVO> getOverview(Long surveyId) {
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        Integer totalResponses = submissionMapper.countBySurveyId(surveyId);
        Integer avgDuration = submissionMapper.averageDurationBySurveyId(surveyId);
        List<Map<String, Object>> dailyData = submissionMapper.dailyResponseCounts(surveyId);

        // 完成率 = 实际回答数 / (总提交数 * 题目数) * 100
        double completionRate = 0;
        if (totalResponses > 0 && survey.getQuestionCount() > 0) {
            int totalAnswers = answerMapper.countAllBySurveyId(surveyId);
            completionRate = (double) totalAnswers / (totalResponses * survey.getQuestionCount()) * 100;
            DecimalFormat df = new DecimalFormat("#.#");
            completionRate = Double.parseDouble(df.format(completionRate));
        }

        List<DailyResponseVO> dailyResponses = dailyData.stream().map(row -> {
            DailyResponseVO vo = new DailyResponseVO();
            vo.setDate(String.valueOf(row.get("date")));
            vo.setCount(((Number) row.get("count")).intValue());
            return vo;
        }).toList();

        SurveyOverviewVO overview = new SurveyOverviewVO();
        overview.setSurveyId(survey.getId());
        overview.setTitle(survey.getTitle());
        overview.setTotalResponses(totalResponses);
        overview.setCompletionRate(completionRate);
        overview.setAverageDuration(avgDuration != null ? avgDuration : 0);
        overview.setQuestionCount(survey.getQuestionCount());
        overview.setDailyResponses(dailyResponses);

        return Result.success(overview);
    }

    @Override
    public Result<QuestionAnalysisVO> getQuestionAnalysis(Long surveyId) {
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        int totalResponses = submissionMapper.countBySurveyId(surveyId);
        List<Question> questions = questionMapper.getById(surveyId);
        List<QuestionAnalysisItemVO> items = new ArrayList<>();

        for (Question question : questions) {
            int totalAnswers = answerMapper.countBySurveyAndQuestion(surveyId, question.getId());
            int skipCount = totalResponses - totalAnswers;
            if (skipCount < 0) skipCount = 0;

            List<Map<String, Object>> distribution = answerMapper.getValueDistribution(surveyId, question.getId());

            QuestionStatisticsVO statistics = buildStatistics(question, distribution, totalAnswers);

            QuestionAnalysisItemVO item = new QuestionAnalysisItemVO();
            item.setQuestionId(question.getId());
            item.setQuestionTitle(question.getTitle());
            item.setQuestionType(question.getType());
            item.setTotalAnswers(totalAnswers);
            item.setSkipCount(skipCount);
            item.setStatistics(statistics);

            items.add(item);
        }

        QuestionAnalysisVO result = new QuestionAnalysisVO();
        result.setQuestions(items);
        return Result.success(result);
    }

    @Override
    public Result<WordCloudVO> getWordCloud(Long surveyId) {
        Survey survey = surveyMapper.getSurveyById(surveyId);
        if (survey == null) {
            return Result.fail("问卷不存在");
        }

        List<String> textAnswers = answerMapper.getTextAnswers(surveyId);
        Map<String, Integer> wordCounts = new LinkedHashMap<>();

        for (String text : textAnswers) {
            if (text == null || text.isBlank()) continue;

            // 按标点符号拆分
            String[] segments = CHINESE_PUNCTUATION.split(text.toLowerCase());
            for (String segment : segments) {
                segment = segment.trim();
                if (segment.length() < 2 || STOP_WORDS.contains(segment)) continue;
                wordCounts.merge(segment, 1, Integer::sum);
            }
        }

        List<WordFrequencyVO> words = wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(50)
                .map(e -> new WordFrequencyVO(e.getKey(), e.getValue()))
                .toList();

        WordCloudVO result = new WordCloudVO();
        result.setWords(words);
        return Result.success(result);
    }

    private QuestionStatisticsVO buildStatistics(Question question,
                                                 List<Map<String, Object>> distribution,
                                                 int totalAnswers) {
        QuestionStatisticsVO stats = new QuestionStatisticsVO();
        stats.setType(question.getType());

        switch (question.getType()) {
            case "RADIO", "DROPDOWN" -> {
                // 获取所有选项用于 label 映射
                List<QuestionOption> options = questionOptionMapper.getById(question.getId());
                Map<String, String> labelMap = options.stream()
                        .collect(Collectors.toMap(opt -> String.valueOf(opt.getId()), QuestionOption::getLabel));

                List<OptionStatisticVO> optionStats = buildOptionStats(distribution, totalAnswers, labelMap);
                stats.setOptions(optionStats);
            }
            case "CHECKBOX" -> {
                // CHECKBOX 的 value 是逗号分隔的 ID 列表，需要展平统计
                List<QuestionOption> options = questionOptionMapper.getById(question.getId());
                Map<String, String> labelMap = options.stream()
                        .collect(Collectors.toMap(opt -> String.valueOf(opt.getId()), QuestionOption::getLabel));

                // 展平所有单个选项 ID 的计数
                Map<String, Integer> flatCounts = new LinkedHashMap<>();
                int respondentCount = 0;
                for (Map<String, Object> row : distribution) {
                    String value = (String) row.get("value");
                    int count = ((Number) row.get("count")).intValue();
                    if (value == null || value.isBlank()) continue;
                    respondentCount += count;
                    for (String id : value.split(",")) {
                        id = id.trim();
                        flatCounts.merge(id, count, Integer::sum);
                    }
                }

                int finalRespondentCount = respondentCount;
                List<OptionStatisticVO> optionStats = flatCounts.entrySet().stream()
                        .map(entry -> {
                            OptionStatisticVO vo = new OptionStatisticVO();
                            vo.setLabel(labelMap.getOrDefault(entry.getKey(), entry.getKey()));
                            vo.setCount(entry.getValue());
                            vo.setPercentage(finalRespondentCount > 0
                                    ? Math.round((double) entry.getValue() / finalRespondentCount * 1000) / 10.0
                                    : 0);
                            return vo;
                        }).toList();
                stats.setOptions(optionStats);
            }
            case "RATING" -> {
                // RATING 统计：平均分、分布
                double sum = 0;
                int count = 0;
                int minScore = Integer.MAX_VALUE;
                int maxScore = Integer.MIN_VALUE;
                Map<Integer, Integer> distMap = new TreeMap<>();

                for (Map<String, Object> row : distribution) {
                    String value = (String) row.get("value");
                    int cnt = ((Number) row.get("count")).intValue();
                    if (value == null || value.isBlank()) continue;
                    try {
                        int score = Integer.parseInt(value);
                        sum += (double) score * cnt;
                        count += cnt;
                        minScore = Math.min(minScore, score);
                        maxScore = Math.max(maxScore, score);
                        distMap.put(score, cnt);
                    } catch (NumberFormatException ignored) {
                    }
                }

                // 补充缺失的分值（如 min=1, max=5，但可能某些分没人选）
                if (question.getMinRating() != null && question.getMaxRating() != null) {
                    for (int s = question.getMinRating(); s <= question.getMaxRating(); s++) {
                        distMap.putIfAbsent(s, 0);
                    }
                }

                stats.setAverageScore(count > 0 ? Math.round(sum / count * 10) / 10.0 : 0);
                stats.setMinScore(count > 0 ? minScore : 0);
                stats.setMaxScore(count > 0 ? maxScore : 0);

                int finalCount = count;
                List<RatingDistributionVO> distributionList = distMap.entrySet().stream()
                        .map(e -> {
                            RatingDistributionVO d = new RatingDistributionVO();
                            d.setScore(e.getKey());
                            d.setCount(e.getValue());
                            d.setPercentage(finalCount > 0
                                    ? Math.round((double) e.getValue() / finalCount * 1000) / 10.0
                                    : 0);
                            return d;
                        }).toList();
                stats.setDistribution(distributionList);
            }
            case "TEXT" -> {
                // TEXT: 将所有回答用逗号拼接
                String wordCloud = distribution.stream()
                        .map(row -> (String) row.get("value"))
                        .filter(Objects::nonNull)
                        .filter(v -> !v.isBlank())
                        .collect(Collectors.joining(","));
                stats.setWordCloud(wordCloud);
            }
        }

        return stats;
    }

    private List<OptionStatisticVO> buildOptionStats(List<Map<String, Object>> distribution,
                                                      int totalAnswers,
                                                      Map<String, String> labelMap) {
        return distribution.stream().map(row -> {
            String value = (String) row.get("value");
            int count = ((Number) row.get("count")).intValue();

            OptionStatisticVO vo = new OptionStatisticVO();
            vo.setLabel(labelMap.getOrDefault(value, value));
            vo.setCount(count);
            vo.setPercentage(totalAnswers > 0
                    ? Math.round((double) count / totalAnswers * 1000) / 10.0
                    : 0);
            return vo;
        }).toList();
    }

    @Override
    public void export(Long surveyId, String format, OutputStream outputStream) throws IOException {
        // 复用已有的统计分析结果，export 只负责格式化输出
        Result<SurveyOverviewVO> overviewResult = getOverview(surveyId);
        if (overviewResult.getCode() == 0) {
            throw new IllegalArgumentException(overviewResult.getMsg());
        }
        Result<QuestionAnalysisVO> analysisResult = getQuestionAnalysis(surveyId);

        SurveyOverviewVO overview = overviewResult.getData();
        QuestionAnalysisVO analysis = analysisResult.getData();

        if ("csv".equalsIgnoreCase(format)) {
            exportCsv(overview, analysis, outputStream);
        } else {
            exportExcel(overview, analysis, outputStream);
        }
    }

    // ==================== Excel 导出 ====================

    private void exportExcel(SurveyOverviewVO overview, QuestionAnalysisVO analysis,
                             OutputStream outputStream) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            // 创建加粗字体样式
            CellStyle boldStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            // 百分比样式（显示为 53.6）
            CellStyle pctStyle = wb.createCellStyle();
            pctStyle.setDataFormat(wb.createDataFormat().getFormat("0.0"));

            // ===== Sheet1: 统计概览 =====
            Sheet s1 = wb.createSheet("统计概览");
            int r = 0;

            setCell(s1, r, 0, "问卷统计报告 - " + overview.getTitle(), boldStyle);
            r += 2;
            setCell(s1, r, 0, "总提交数");     setCell(s1, r, 1, overview.getTotalResponses()); r++;
            setCell(s1, r, 0, "题目数量");     setCell(s1, r, 1, overview.getQuestionCount()); r++;
            setCell(s1, r, 0, "完成率(%)");    setCell(s1, r, 1, overview.getCompletionRate()); r++;
            setCell(s1, r, 0, "平均耗时(秒)"); setCell(s1, r, 1, overview.getAverageDuration()); r++;

            r++;
            setCell(s1, r, 0, "日期", boldStyle);   setCell(s1, r, 1, "提交数", boldStyle); r++;
            for (DailyResponseVO d : overview.getDailyResponses()) {
                setCell(s1, r, 0, d.getDate());
                setCell(s1, r, 1, d.getCount());
                r++;
            }
            s1.setColumnWidth(0, 4000);
            s1.setColumnWidth(1, 3000);

            // ===== Sheet2: 题目分析 =====
            Sheet s2 = wb.createSheet("题目分析");
            r = 0;

            for (QuestionAnalysisItemVO q : analysis.getQuestions()) {
                // 题目行
                setCell(s2, r, 0, "【" + q.getQuestionType() + "】" + q.getQuestionTitle(), boldStyle);
                setCell(s2, r, 1, "回答:" + q.getTotalAnswers() + "  跳过:" + q.getSkipCount());
                r++;

                QuestionStatisticsVO st = q.getStatistics();

                // 根据题型输出不同的统计表格
                if ("RADIO".equals(q.getQuestionType()) || "DROPDOWN".equals(q.getQuestionType())
                        || "CHECKBOX".equals(q.getQuestionType())) {
                    setCell(s2, r, 0, "选项", boldStyle);
                    setCell(s2, r, 1, "计数", boldStyle);
                    setCell(s2, r, 2, "百分比(%)", boldStyle);
                    r++;
                    for (OptionStatisticVO opt : st.getOptions()) {
                        setCell(s2, r, 0, opt.getLabel());
                        setCell(s2, r, 1, opt.getCount());
                        setCell(s2, r, 2, opt.getPercentage());
                        r++;
                    }

                } else if ("RATING".equals(q.getQuestionType())) {
                    setCell(s2, r, 0, "平均分"); setCell(s2, r, 1, st.getAverageScore()); r++;
                    setCell(s2, r, 0, "分值", boldStyle);
                    setCell(s2, r, 1, "计数", boldStyle);
                    setCell(s2, r, 2, "百分比(%)", boldStyle);
                    r++;
                    for (RatingDistributionVO d : st.getDistribution()) {
                        setCell(s2, r, 0, d.getScore());
                        setCell(s2, r, 1, d.getCount());
                        setCell(s2, r, 2, d.getPercentage());
                        r++;
                    }

                } else if ("TEXT".equals(q.getQuestionType())) {
                    setCell(s2, r, 0, "回答内容", boldStyle);
                    setCell(s2, r, 1, "次数", boldStyle);
                    r++;
                    if (st.getWordCloud() != null) {
                        // wordCloud 是逗号拼接的所有文本，拆开展示
                        for (String text : st.getWordCloud().split(",")) {
                            setCell(s2, r, 0, text);
                            r++;
                        }
                    }
                }
                r++; // 题目间空行
            }
            s2.setColumnWidth(0, 6000);
            s2.setColumnWidth(1, 3000);
            s2.setColumnWidth(2, 3000);

            wb.write(outputStream);
        }
    }

    // ==================== CSV 导出 ====================

    private void exportCsv(SurveyOverviewVO overview, QuestionAnalysisVO analysis,
                           OutputStream outputStream) {
        PrintWriter w = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        w.print('﻿'); // BOM，中文 Excel 打开不乱码

        w.println("问卷统计报告," + overview.getTitle());
        w.println("总提交数," + overview.getTotalResponses());
        w.println("题目数量," + overview.getQuestionCount());
        w.println("完成率(%)," + overview.getCompletionRate());
        w.println("平均耗时(秒)," + overview.getAverageDuration());
        w.println();

        for (QuestionAnalysisItemVO q : analysis.getQuestions()) {
            w.println("【" + q.getQuestionType() + "】" + q.getQuestionTitle());
            w.println("回答:" + q.getTotalAnswers() + ",跳过:" + q.getSkipCount());

            QuestionStatisticsVO st = q.getStatistics();

            if ("RADIO".equals(q.getQuestionType()) || "DROPDOWN".equals(q.getQuestionType())
                    || "CHECKBOX".equals(q.getQuestionType())) {
                w.println("选项,计数,百分比(%)");
                for (OptionStatisticVO opt : st.getOptions()) {
                    w.println(opt.getLabel() + "," + opt.getCount() + "," + opt.getPercentage());
                }

            } else if ("RATING".equals(q.getQuestionType())) {
                w.println("平均分," + st.getAverageScore());
                w.println("分值,计数,百分比(%)");
                for (RatingDistributionVO d : st.getDistribution()) {
                    w.println(d.getScore() + "," + d.getCount() + "," + d.getPercentage());
                }

            } else if ("TEXT".equals(q.getQuestionType()) && st.getWordCloud() != null) {
                for (String text : st.getWordCloud().split(",")) {
                    w.println(text);
                }
            }
            w.println();
        }

        w.flush();
    }

    // ==================== 辅助方法 ====================

    /** 简化 Excel 单元格写入 */
    private void setCell(Sheet sheet, int row, int col, Object value) {
        setCell(sheet, row, col, value, null);
    }

    private void setCell(Sheet sheet, int row, int col, Object value, CellStyle style) {
        Row r = sheet.getRow(row);
        if (r == null) r = sheet.createRow(row);
        Cell c = r.getCell(col);
        if (c == null) c = r.createCell(col);

        if (value instanceof Number n) {
            c.setCellValue(n.doubleValue());
        } else {
            c.setCellValue(value != null ? value.toString() : "");
        }
        if (style != null) c.setCellStyle(style);
    }
}
