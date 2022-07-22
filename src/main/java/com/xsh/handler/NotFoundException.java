package com.xsh.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author : xsh
 * @create : 2020-02-06 - 23:04
 * @describe: 自定义业务异常类   @ResponseStatus定义异常状态码
 */

/**
 * 如果加@ResponseStatus，在代码中直接抛出该自定义异常，
 * 会以指定的HTTP状态码和指定的reson响应到浏览器；
 * 我们自定义异常的目的就是为了让它正确表述我们的思想，
 * 所以给其设置响应状态码和原因让其准确表达我们的目的。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{
    static {
        System.out.println("此方法被使用！");
    }

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
