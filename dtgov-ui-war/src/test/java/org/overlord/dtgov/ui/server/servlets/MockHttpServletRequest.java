/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.ui.server.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author eric.wittmann@redhat.com
 */
@SuppressWarnings("rawtypes")
public class MockHttpServletRequest implements HttpServletRequest {

    /**
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getAttributeNames()
     */
    @Override
    public Enumeration getAttributeNames() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    /**
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    @Override
    public int getContentLength() {

        return 0;
    }

    /**
     * @see javax.servlet.ServletRequest#getContentType()
     */
    @Override
    public String getContentType() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @Override
    public Enumeration getParameterNames() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name) {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    @Override
    public Map getParameterMap() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    @Override
    public String getProtocol() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getScheme()
     */
    @Override
    public String getScheme() {
        return "http"; //$NON-NLS-1$
    }

    /**
     * @see javax.servlet.ServletRequest#getServerName()
     */
    @Override
    public String getServerName() {
        return "test.overlord.org"; //$NON-NLS-1$
    }

    /**
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    @Override
    public int getServerPort() {
        return 8080;
    }

    /**
     * @see javax.servlet.ServletRequest#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object o) {

    }

    /**
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name) {

    }

    /**
     * @see javax.servlet.ServletRequest#getLocale()
     */
    @Override
    public Locale getLocale() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocales()
     */
    @Override
    public Enumeration getLocales() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#isSecure()
     */
    @Override
    public boolean isSecure() {

        return false;
    }

    /**
     * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
     */
    @Override
    public String getRealPath(String path) {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    @Override
    public int getRemotePort() {

        return 0;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    @Override
    public String getLocalName() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    @Override
    public String getLocalAddr() {

        return null;
    }

    /**
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    @Override
    public int getLocalPort() {

        return 0;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    @Override
    public String getAuthType() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    @Override
    public Cookie[] getCookies() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
     */
    @Override
    public long getDateHeader(String name) {

        return 0;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String name) {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    @Override
    public Enumeration getHeaders(String name) {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    @Override
    public Enumeration getHeaderNames() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
     */
    @Override
    public int getIntHeader(String name) {

        return 0;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    @Override
    public String getMethod() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    @Override
    public String getPathInfo() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    @Override
    public String getPathTranslated() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     */
    @Override
    public String getContextPath() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    @Override
    public String getQueryString() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role) {

        return false;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
     */
    @Override
    public String getRequestedSessionId() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    @Override
    public String getRequestURI() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     */
    @Override
    public String getServletPath() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    @Override
    public HttpSession getSession(boolean create) {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    @Override
    public HttpSession getSession() {

        return null;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
     */
    @Override
    public boolean isRequestedSessionIdValid() {

        return false;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    @Override
    public boolean isRequestedSessionIdFromCookie() {

        return false;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    @Override
    public boolean isRequestedSessionIdFromURL() {

        return false;
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    @Override
    public boolean isRequestedSessionIdFromUrl() {

        return false;
    }

}
