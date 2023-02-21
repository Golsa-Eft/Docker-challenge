package com.example.demo.log;

import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class Logger extends DispatcherServlet {

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    protected void doDispatch(HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (!(req instanceof ContentCachingRequestWrapper)) {
            req = new ContentCachingRequestWrapper(req);
        }
        if (!(res instanceof ContentCachingResponseWrapper)) {
            res = new ContentCachingResponseWrapper(res);
        }
        HandlerExecutionChain handler = getHandler(req);

        try {
            super.doDispatch(req, res);
        } finally {
            log(req, res, handler);
            updateResponse(res);
        }
    }

    private void log(HttpServletRequest requestToCache, HttpServletResponse responseToCache, HandlerExecutionChain handler) {
        LogMessage log = new LogMessage();
        log.setHttpStatus(responseToCache.getStatus());
        log.setHttpMethod(requestToCache.getMethod());
        log.setDate(ZonedDateTime.now(ZoneOffset.UTC ).format(DateTimeFormatter.ISO_DATE));
        log.setClientIp(requestToCache.getRemoteAddr());
        log.setPath(requestToCache.getRequestURI());
        log.setResponse(getResponsePayload(responseToCache));
        logger.info(log);
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        return "[unknown]";
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

    @Data
    private class LogMessage {
        private int httpStatus;
        private String httpMethod;
        private String path;
        private String clientIp;
        private String response;
        private String date;

        @Override
        public String toString() {
            return new StringJoiner("  ", "\n" + LogMessage.class.getSimpleName() + "{", "}")
                    .add("\n")
                    .add("httpStatus=" + httpStatus)
                    .add("\n")
                    .add("httpMethod='" + httpMethod + "'")
                    .add("\n")
                    .add("path='" + path + "'")
                    .add("\n")
                    .add("clientIp='" + clientIp + "'")
                    .add("\n")
                    .add("response='" + response + "'")
                    .add("\n")
                    .add("date=" + date)
                    .add("\n")
                    .toString();
        }
    }
}
