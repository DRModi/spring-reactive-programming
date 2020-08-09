/*
package com.drmodi.learn.reactive.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
@Component
@Slf4j
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> map = super.getErrorAttributes(
                webRequest, options);
        map.put("status", HttpStatus.BAD_REQUEST);
        map.put("message", "INTERNAL SERVER ERROR - RuntimeException");
        log.error("Error Logged at GlobalError Attributes : "+map.toString());
        return map;

    }
}
*/
