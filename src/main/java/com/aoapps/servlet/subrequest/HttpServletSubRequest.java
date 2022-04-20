/*
 * ao-servlet-subrequest - Servlet sub-request wrappers with optional concurrency.
 * Copyright (C) 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-servlet-subrequest.
 *
 * ao-servlet-subrequest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-servlet-subrequest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-servlet-subrequest.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.servlet.subrequest;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.apache.commons.lang3.NotImplementedException;

public class HttpServletSubRequest extends ServletSubRequest implements IHttpServletSubRequest {

  private final HttpServletRequest req;

  private boolean loggedOut;

  public HttpServletSubRequest(HttpServletRequest req) {
    super(req);
    this.req = req;
  }

  @Override
  public String getAuthType() {
    return loggedOut ? null : req.getAuthType();
  }

  @Override
  public Cookie[] getCookies() {
    return req.getCookies();
  }

  @Override
  public long getDateHeader(String name) {
    return req.getDateHeader(name);
  }

  @Override
  public String getHeader(String name) {
    return req.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    return req.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return req.getHeaderNames();
  }

  @Override
  public int getIntHeader(String name) {
    return req.getIntHeader(name);
  }

  private String method;

  @Override
  public String getMethod() {
    if (method == null) {
      method = req.getMethod();
    }
    return method;
  }

  @Override
  public void setMethod(String method) {
    this.method = method;
  }

  @Override
  public String getPathInfo() {
    return req.getPathInfo();
  }

  @Override
  public String getPathTranslated() {
    return req.getPathTranslated();
  }

  @Override
  public String getContextPath() {
    return req.getContextPath();
  }

  @Override
  public String getQueryString() {
    return req.getQueryString();
  }

  @Override
  public String getRemoteUser() {
    return loggedOut ? null : req.getRemoteUser();
  }

  @Override
  public boolean isUserInRole(String role) {
    return !loggedOut && req.isUserInRole(role);
  }

  @Override
  public Principal getUserPrincipal() {
    return loggedOut ? null : req.getUserPrincipal();
  }

  @Override
  public String getRequestedSessionId() {
    return req.getRequestedSessionId();
  }

  @Override
  public String getRequestURI() {
    return req.getRequestURI();
  }

  @Override
  public StringBuffer getRequestURL() {
    return req.getRequestURL();
  }

  @Override
  public String getServletPath() {
    return req.getServletPath();
  }

  @Override
  public HttpSession getSession(boolean create) {
    return req.getSession(create);
  }

  @Override
  public HttpSession getSession() {
    return req.getSession();
  }

  @Override
  public String changeSessionId() {
    return req.changeSessionId();
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return req.isRequestedSessionIdValid();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return req.isRequestedSessionIdFromCookie();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return req.isRequestedSessionIdFromURL();
  }

  @Deprecated(forRemoval = false)
  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return req.isRequestedSessionIdFromUrl();
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    throw new NotImplementedException("TODO");
  }

  @Override
  public void login(String username, String password) throws ServletException {
    throw new NotImplementedException("TODO");
  }

  @Override
  public void logout() throws ServletException {
    loggedOut = true;
  }

  @Override
  public boolean isLoggedOut() {
    return loggedOut;
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return req.getParts();
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    return req.getPart(name);
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> type) throws IOException, ServletException {
    return req.upgrade(type);
  }
}
