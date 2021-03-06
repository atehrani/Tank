package com.intuit.tank.http;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;

public abstract class BaseResponse {

    private static final char NEWLINE = '\n';

    private static Logger logger = Logger.getLogger(BaseResponse.class);

    protected String response;
    protected long responseTime = -1;
    protected int httpCode = -1;
    protected String rspMessage = "";
    protected HashMap<String, String> headers;
    protected Cookie[] cookies;
    protected HashMap<String, String> cookiesByDomain;
    protected String responseLogMsg;

    protected byte[] responseByteArray;

    public abstract String getValue(String key); // returns value for the given
                                                 // path

    public BaseResponse() {
        this.headers = new HashMap<String, String>();
        this.cookies = new Cookie[0];
        this.cookiesByDomain = new HashMap<String, String>();
    }

    /**
     * @return the responseLogMsg
     */
    public String getLogMsg() {
        return responseLogMsg;
    }

    /**
     * Common codes are 200 OK, 202 accepted, 204 no content, 400 bad request, 404 not found, 500 internal server error,
     * 503 Service Unavailable
     * 
     * @return int - The integer representing the HTTP code for this response. (200, 404, 503 etc)
     */
    public int getHttpCode() {
        return this.httpCode;
    }

    public void setHttpCode(int code) {
        this.httpCode = code;
    }

    public void setHttpMessage(String msg) {
        this.rspMessage = msg;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    /**
     * 
     * @return String - The response message associated with this response.
     */
    public String getHttpMsg() {
        return this.rspMessage;
    }

    /**
     * 
     * @param header
     *            String - The key for the header to lookup in the header hashmap.
     * @return The value associated with the key, or null if it doesn't exist.
     */
    public String getHttpHeader(String header) {
        return this.headers.get(header);
    }

    /**
     * Returns entire header as a Hashmap for this response.
     * 
     * @return a HashMap<String,String> of the entire response header.
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Sets the response time.
     * 
     * @param time
     *            - long
     */
    public void setResponseTime(long time) {
        this.responseTime = time;
    }

    /**
     * Gets the response time for this response. The returned value is whatever was set by setResponseTime().
     * 
     * @return
     */
    public long getResponseTime() {
        return this.responseTime;
    }

    /**
     * Set the response body for this response. Inherited classes should overwrite this to their specific needs.
     * 
     * @param body
     */
    public void setResponseBody(String body) {
        this.response = body;
    }

    public void setResponseBody(byte[] byteArray) {
        this.responseByteArray = byteArray;
        this.response = new String(byteArray);
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public String getResponseBody() {
        return this.response;
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public String getBody() {
        return this.response;
    }

    /**
     * Returns the response body for this response.
     * 
     * @return String, the body of this response.
     */
    public byte[] getResponseBytes() {
        return this.responseByteArray;
    }

    public int getResponseSize() {
        return this.responseByteArray != null ? this.responseByteArray.length : this.response != null ? this.response
                .length() : -1;
    }

    /**
     * Lookup a cookies value
     * 
     * @param key
     *            Cookie name to lookup
     * @return Entire cookie string as it is return to browser in Set-Cookie header
     */
    public String getCookie(String key) {

        for (int c = 0; c < this.cookies.length; c++) {
            if (cookies[c].getName().equals(key))
                return cookies[c].getValue();
        }

        return null;
    }

    public void setCookies(Cookie[] cookies) {

        if (cookies != null)
            this.cookies = cookies;
    }

    public HashMap<String, String> getCookiesByDomain() {
        return cookiesByDomain;
    }

    /**
     * Log the response object
     */
    protected void logResponse() {
        try {
            StringBuilder sb = new StringBuilder();
            // System.out.println("******** RESPONSE ***********");
            sb.append("RESPONSE HTTP CODE: " + this.httpCode).append(NEWLINE);
            sb.append("RESPONSE HTTP MSG: " + this.rspMessage).append(NEWLINE);
            sb.append("RESPONSE TIME: " + responseTime).append(NEWLINE);
            sb.append("RESPONSE SIZE: " + responseByteArray.length).append(NEWLINE);
            for (Entry<String, String> mapEntry : headers.entrySet()) {
                sb.append("RESPONSE HEADER: "
                        + (String) mapEntry.getKey() + " = "
                        + (String) mapEntry.getValue()).append(NEWLINE);
            }
            for (int c = 0; c < cookies.length; c++) {
                sb.append("RESPONSE COOKIE: "
                        + cookies[c].toExternalForm() + " (domain=" + cookies[c].getDomain() + " : path="
                        + cookies[c].getPath() + ")").append(NEWLINE);
            }
            if (response != null) {
                String contentType = this.headers.get("Content-Type");
                if (isDataType(contentType)) {
                    sb.append("RESPONSE BODY: " + this.response).append(NEWLINE);
                } else {
                    sb.append("RESPONSE BODY: not logged because " + contentType + " is not a data type.").append(
                            NEWLINE);
                }
            }
            this.responseLogMsg = sb.toString();
            if (APITestHarness.getInstance().isDebug()) {
                System.out.println("******** RESPONSE ***********");
                System.out.println(this.responseLogMsg);
            }
            logger.debug("******** RESPONSE ***********");
            logger.debug(this.responseLogMsg);

        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.getMessage()), ex);
        }
    }

    public static final boolean isDataType(String contentType) {
        boolean ret = false;
        if (!StringUtils.isBlank(contentType)) {
            contentType = contentType.toLowerCase();
            ret = contentType.contains("html") ||
                    contentType.contains("text") ||
                    contentType.contains("json") ||
                    contentType.contains("xml");
        }
        return ret;
    }
}
