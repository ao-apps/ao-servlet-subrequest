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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * Synchronizes access to the wrapped request.
 */
public class ThreadSafeHttpServletRequest extends ThreadSafeServletRequest implements HttpServletRequest {

  private HttpServletRequest req;

  public ThreadSafeHttpServletRequest(HttpServletRequest req) {
    super(req);
    this.req = req;
  }

  @Override
  public void setRequest(ServletRequest request) {
    synchronized (lock) {
      this.req = (HttpServletRequest) request;
      super.setRequest(request);
    }
  }

  @Override
  public String getAuthType() {
    synchronized (lock) {
      return req.getAuthType();
    }
  }

  @Override
  public Cookie[] getCookies() {
    synchronized (lock) {
      return req.getCookies();
    }
  }

  @Override
  public long getDateHeader(String name) {
    synchronized (lock) {
      return req.getDateHeader(name);
    }
  }

  @Override
  public String getHeader(String name) {
    synchronized (lock) {
      return req.getHeader(name);
    }
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> headers;
    synchronized (lock) {
      Enumeration<String> e = req.getHeaders(name);
      if (e == null) {
        return null;
      } else {
        headers = new ArrayList<>();
        while (e.hasMoreElements()) {
          headers.add(e.nextElement());
        }
      }
    }
    return Collections.enumeration(headers);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    List<String> headerNames;
    synchronized (lock) {
      Enumeration<String> e = req.getHeaderNames();
      if (e == null) {
        return null;
      } else {
        headerNames = new ArrayList<>();
        while (e.hasMoreElements()) {
          headerNames.add(e.nextElement());
        }
      }
    }
    return Collections.enumeration(headerNames);
  }

  @Override
  public int getIntHeader(String name) {
    synchronized (lock) {
      return req.getIntHeader(name);
    }
  }

  @Override
  public String getMethod() {
    synchronized (lock) {
      return req.getMethod();
    }
  }

  @Override
  public String getPathInfo() {
    synchronized (lock) {
      return req.getPathInfo();
    }
  }

  @Override
  public String getPathTranslated() {
    synchronized (lock) {
      return req.getPathTranslated();
    }
  }

  @Override
  public String getContextPath() {
    synchronized (lock) {
      return req.getContextPath();
    }
  }

  @Override
  public String getQueryString() {
    synchronized (lock) {
      return req.getQueryString();
    }
  }

  @Override
  public String getRemoteUser() {
    synchronized (lock) {
      return req.getRemoteUser();
    }
  }

  @Override
  public boolean isUserInRole(String role) {
    synchronized (lock) {
      return req.isUserInRole(role);
    }
  }

  @Override
  public Principal getUserPrincipal() {
    synchronized (lock) {
      return req.getUserPrincipal();
    }
  }

  @Override
  public String getRequestedSessionId() {
    synchronized (lock) {
      return req.getRequestedSessionId();
    }
  }

  @Override
  public String getRequestURI() {
    synchronized (lock) {
      return req.getRequestURI();
    }
  }

  @Override
  public StringBuffer getRequestURL() {
    synchronized (lock) {
      return req.getRequestURL();
    }
  }

  @Override
  public String getServletPath() {
    synchronized (lock) {
      return req.getServletPath();
    }
  }

  @Override
  public HttpSession getSession(boolean create) {
    synchronized (lock) {
      return req.getSession(create);
    }
  }

  @Override
  public HttpSession getSession() {
    synchronized (lock) {
      return req.getSession();
    }
  }

  @Override
  public String changeSessionId() {
    synchronized (lock) {
      return req.changeSessionId();
    }
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    synchronized (lock) {
      return req.isRequestedSessionIdValid();
    }
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    synchronized (lock) {
      return req.isRequestedSessionIdFromCookie();
    }
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    synchronized (lock) {
      return req.isRequestedSessionIdFromURL();
    }
  }

  @Deprecated(forRemoval = false)
  @Override
  public boolean isRequestedSessionIdFromUrl() {
    synchronized (lock) {
      return req.isRequestedSessionIdFromUrl();
    }
  }

  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    synchronized (lock) {
      return req.authenticate(response);
    }
  }

  @Override
  public void login(String username, String password) throws ServletException {
    synchronized (lock) {
      req.login(username, password);
    }
  }

  @Override
  public void logout() throws ServletException {
    synchronized (lock) {
      req.logout();
    }
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
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
    synchronized (lock) {
      Part part = req.getPart(name);
      return part == null ? null : new ThreadSafePart(part, lock);
    }
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> type) throws IOException, ServletException {
    synchronized (lock) {
      return req.upgrade(type);
    }
  }
}
