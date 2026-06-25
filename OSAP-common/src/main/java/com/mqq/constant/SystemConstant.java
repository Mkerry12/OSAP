package com.mqq.constant;

import java.util.Set;
import java.util.regex.Pattern;

public class SystemConstant {

    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAILURE = 0;

    public static final String REGISTER_SUCCESS ="注册成功";

    public static final String MSG_PHONE_INVALID = "手机号格式错误！";
    public static final String MSG_EMAIL_INVALID = "邮箱格式错误！";
    public static final String MSG_CODE_ERROR = "验证码错误！";
    public static final String MSG_USER_NOT_EXIST = "不存在用户";
    public static final String MSG_CHECK_ACC_OR_PAS = "请查看账号和密码是否输入正确！";
    public static final String MSG_LOGOUT_SUCCESS = "退出登录成功！";
    public static final String MSG_PHONE_NO_MATCH = "该账号绑定的手机号不正确！";
    public static final String MSG_PASSWORD_OLD_ERROR = "原密码输入错误，尝试修改前请输入原密码！";
    public static final String ALREADY_EXISTS = "已经存在用户";
    public static final String CHECK_SUCCESS_RESET = "验证成功，请重置密码";
    public static final String RESET_SUCCESS = "密码重置成功";
    public static final String DISABLED = "你已经被禁用了";
    public static final Set<String> STOP_WORDS = Set.of(
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
    public static final Pattern CHINESE_PUNCTUATION = Pattern.compile("[，。！？、；：]+");
}
