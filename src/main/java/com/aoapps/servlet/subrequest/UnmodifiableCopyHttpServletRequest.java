/*
 * ao-servlet-subrequest - Servlet sub-request wrappers with optional concurrency.
 * Copyright (C) 2016, 2019, 2020, 2021, 2022, 2025, 2026  AO Industries, Inc.
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

import com.aoapps.collections.MinimalList;
import com.aoapps.collections.MinimalMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.PushBuilder;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;

public class UnmodifiableCopyHttpServletRequest extends UnmodifiableCopyServletRequest implements HttpServletRequest {

  private static Cookie[] copyCookies(Cookie[] cookies) {
    if (cookies == null) {
      return null;
    }
    Cookie[] copy = new Cookie[cookies.length];
    for (int i = 0; i < cookies.length; i++) {
      copy[i] = (Cookie) cookies[i].clone();
    }
    return copy;
  }

  private final HttpServletRequest req;

  private final String authType;
  private final Cookie[] cookies;
  private final Map<String, List<String>> headers;
  private final String method;
  private final String pathInfo;
  private final String pathTranslated;
  private final String contextPath;
  private final String queryString;
  private final String remoteUser;
  private final Principal userPrincipal;
  private final String requestedSessionId;
  private final String requestURI;
  private final String servletPath;
  private final boolean requestedSessionIdValid;
  private final boolean requestedSessionIdFromCookie;
  private final boolean requestedSessionIdFromURL;

  public UnmodifiableCopyHttpServletRequest(HttpServletRequest req) {
    super(req);
    this.req = req;

    authType = req.getAuthType();
    cookies = copyCookies(req.getCookies());
    Map<String, List<String>> newHeaders = null;
    Enumeration<String> headerNames = req.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      List<String> values = null;
      Enumeration<String> headerValues = req.getHeaders(name);
      while (headerValues.hasMoreElements()) {
        values = MinimalList.add(
            values,
            headerValues.nextElement()
        );
      }
      newHeaders = MinimalMap.put(
          newHeaders,
          name,
          MinimalList.unmodifiable(values)
      );
    }
    headers = MinimalMap.unmodifiable(newHeaders);
    method = req.getMethod();
    pathInfo = req.getPathInfo();
    pathTranslated = req.getPathTranslated();
    contextPath = req.getContextPath();
    queryString = req.getQueryString();
    remoteUser = req.getRemoteUser();
    userPrincipal = req.getUserPrincipal();
    requestedSessionId = req.getRequestedSessionId();
    requestURI = req.getRequestURI();
    servletPath = req.getServletPath();
    requestedSessionIdValid = req.isRequestedSessionIdValid();
    requestedSessionIdFromCookie = req.isRequestedSessionIdFromCookie();
    requestedSessionIdFromURL = req.isRequestedSessionIdFromURL();
  }

  @Override
  public String getAuthType() {
    return authType;
  }

  @Override
  public Cookie[] getCookies() {
    return copyCookies(cookies);
  }

  @Override
  public long getDateHeader(String name) {
    if (!headers.containsKey(name)) {
      return -1;
    }
    // TODO: cache here?
    synchronized (lock) {
      return req.getDateHeader(name);
    }
  }

  @Override
  public String getHeader(String name) {
    List<String> values = headers.get(name);
    return values == null ? null : values.get(0);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> values = headers.get(name);
    if (values == null) {
      return Collections.emptyEnumeration();
    }
    return Collections.enumeration(values);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(headers.keySet());
  }

  @Override
  public int getIntHeader(String name) {
    String header = getHeader(name);
    return header == null ? -1 : Integer.parseInt(header);
  }

  @Override
  public HttpServletMapping getHttpServletMapping() {
    // TODO: Cache?
    synchronized (lock) {
      return req.getHttpServletMapping();
    }
  }

  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getPathTranslated() {
    return pathTranslated;
  }

  @Override
  public PushBuilder newPushBuilder() {
    // Not supported on subrequests
    return null;
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }

  @Override
  public String getQueryString() {
    return queryString;
  }

  @Override
  public String getRemoteUser() {
    return remoteUser;
  }

  @Override
  public boolean isUserInRole(String role) {
    // TODO: Cache?
    synchronized (lock) {
      return req.isUserInRole(role);
    }
  }

  @Override
  public Principal getUserPrincipal() {
    return userPrincipal;
  }

  @Override
  public String getRequestedSessionId() {
    return requestedSessionId;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public StringBuffer getRequestURL() {
    // TODO: Cache?
    synchronized (lock) {
      return req.getRequestURL();
    }
  }

  @Override
  public String getServletPath() {
    return servletPath;
  }

  @Override
  public HttpSession getSession(boolean create) {
    // TODO: Cache?
    synchronized (lock) {
      return req.getSession(create);
    }
  }

  @Override
  public HttpSession getSession() {
    // TODO: Cache?
    synchronized (lock) {
      return req.getSession();
    }
  }

  @Override
  public String changeSessionId() {
    // TODO: Cache?
    synchronized (lock) {
      return req.changeSessionId();
    }
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    return requestedSessionIdValid;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return requestedSessionIdFromCookie;
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    return requestedSessionIdFromURL;
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
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    // TODO: Cache?
    synchronized (lock) {
      Collection<Part> parts = req.getParts();
      List<Part> wrapped = new ArrayList<>(parts.size());
      for (Part part : parts) {
        wrapped.add(new ThreadSafePart(part, lock));
      }
      return wrapped;
    }
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    // TODO: Cache?
    synchronized (lock) {
      Part part = req.getPart(name);
      return part == null ? null : new ThreadSafePart(part, lock);
    }
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> type) throws IOException, ServletException {
    // TODO: Cache?
    synchronized (lock) {
      return req.upgrade(type);
    }
  }

  @Override
  public Map<String, String> getTrailerFields() {
    // TODO: Cache?
    synchronized (lock) {
      return req.getTrailerFields();
    }
  }

  @Override
  public boolean isTrailerFieldsReady() {
    // TODO: Cache?
    synchronized (lock) {
      return req.isTrailerFieldsReady();
    }
  }
}
