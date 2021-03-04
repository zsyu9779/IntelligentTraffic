package com.manager.traffic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名称：ErrorCodeEnum
 * ********************************
 * <p>
 * 类描述：异常编码枚举
 *
 * @author
 * @date 2021/3/1 下午9:54
 */
@AllArgsConstructor
@Getter
public enum ErrorCodeEnum {

    // 2** 成功
    SUCCESS(200, "H20000", "操作成功"),

    // 3** 参数异常
    PARAM_ERROR(300, "H3000", "参数错误"),
    PARAM_MISSING(300, "H3001", "参数缺失"),
    HEADER_ERR(300, "H3011", "请求头缺失"),
    UPLOAD_FILE_CONTENT_MISSING(300, "H3009", "上传文件内容缺失"),

    USER_NOT_ADMIN(301, "H3009", "用户未登录"),

    // 4** 资源操作异常
    RESOURCE_NOT_FOUND(400, "H4001", "资源查询错误"),
    USER_INFO_NOT_FOUND(400, "H4004", "用户数据查询不到"),
    USER_AUTH_INFO_NOT_FOUND(400, "H4016", "用户认证数据查询不到"),
    ILLEGAL_OPERATION(402,"H4021","非法操作"),

    // 5** 系统异常
    SYSTEM_ERROR(500, "H5000", "服务异常"),
    UNKNOWN_ERROR(501, "H5001", "未知异常"),

;


    /**
     * 错误编码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误描述
     */
    private String desc;
}
