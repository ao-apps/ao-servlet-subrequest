/*
 * ao-servlet-subrequest - Servlet sub-request wrappers with optional concurrency.
 * Copyright (C) 2016, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;

/**
 * Wraps a servlet request with the intent to operate as a concurrent sub request.
 * Any changes made to the request will only affect this request and will not be passed
 * along to the wrapped request.
 *
 * <p>It is expected that the wrapped request will not change for the life of this wrapper.
 * If it does change, the changes may or may not be visible depending on what has been
 * accessed and changed on this request.</p>
 *
 * <p>This class is not thread safe.</p>
 */
public class ServletSubRequestWrapper extends ServletRequestWrapper implements IServletSubRequest {

  private static final Logger logger = Logger.getLogger(ServletSubRequestWrapper.class.getName());

  public ServletSubRequestWrapper(ServletRequest req) {
    super(req);
  }

  /**
   * These attributes are hidden.  They exist when queried on request
   * but are not returned as part of all attribute names.
   */
  private static final Set<String> hiddenAttributeNames = Collections.unmodifiableSet(
      new HashSet<>(
          Arrays.asList(
              "org.apache.catalina.core.DISPATCHER_TYPE",
              "org.apache.catalina.core.DISPATCHER_REQUEST_PATH",
              "org.apache.catalina.jsp_file",
              "javax.servlet.include.servlet_path",
              "javax.servlet.include.request_uri",
              "javax.servlet.include.context_path",
              "javax.servlet.include.path_info"
          )
      )
  );

  private Map<String, Object> attributes;

  @Override
  public Object getAttribute(String name) {
    if (logger.isLoggable(Level.FINER)) {
      logger.finer("name: " + name);
    }
    if (
        attributes != null
            && !hiddenAttributeNames.contains(name)
    ) {
      return attributes.get(name);
    } else {
      return super.getAttribute(name);
    }
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    if (logger.isLoggable(Level.FINER)) {
      logger.finer("start");
    }
    if (attributes != null) {
      Set<String> attrNames = attributes.keySet();
      List<String> nonHiddenAttributeNames = new ArrayList<>(attrNames.size());
      for (String attrName : attrNames) {
        if (!hiddenAttributeNames.contains(attrName)) {
          nonHiddenAttributeNames.add(attrName);
        }
      }
      return Collections.enumeration(nonHiddenAttributeNames);
    } else {
      return super.getAttributeNames();
    }
  }

  @Override
  public void setAttribute(String name, Object o) {
    if (logger.isLoggable(Level.FINER)) {
      try {
        logger.finer("DEBUG: setAttribute: " + name + ", " + o);
      } catch (IllegalStateException e) {
        // Object not ready for toString
      }
    }
    if (attributes == null) {
      Map<String, Object> newAttributes = new LinkedHashMap<>();
      for (String hiddenAttrName : hiddenAttributeNames) {
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("hiddenAttrName: " + hiddenAttrName);
        }
        Object hiddenAttrVal = super.getAttribute(hiddenAttrName);
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("hiddenAttrVal: " + hiddenAttrVal);
        }
        if (hiddenAttrVal != null) {
          newAttributes.put(hiddenAttrName, hiddenAttrVal);
        }
      }
      Enumeration<String> attrNames = super.getAttributeNames();
      while (attrNames.hasMoreElements()) {
        String attrName = attrNames.nextElement();
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("attrName: " + attrName);
        }
        Object attrVal = super.getAttribute(attrName);
        if (logger.isLoggable(Level.FINEST)) {
          logger.finest("attrVal: " + attrVal);
        }
        // Check for null in case attribute was removed during iteration
        if (attrVal != null) {
          newAttributes.put(attrName, attrVal);
        }
      }
      attributes = newAttributes;
    }
    if (o == null) {
      attributes.remove(name);
    } else {
      attributes.put(name, o);
    }
  }

  @Override
  public void removeAttribute(String name) {
    setAttribute(name, null);
  }

  private boolean characterEncodingSet;
  private String characterEncoding;

  @Override
  public String getCharacterEncoding() {
    if (characterEncodingSet) {
      return characterEncoding;
    } else {
      return super.getCharacterEncoding();
    }
  }

  @Override
  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
    characterEncoding = enc;
    characterEncodingSet = true;
    // Not checking to throw UnsupportedEncodingException here, assuming no longer in a context where character encoding may be set
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    throw new IllegalStateException("Not allowed on concurrent request");
  }

  @Override
  public BufferedReader getReader() throws IOException {
    throw new IllegalStateException("Not allowed on concurrent request");
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new IllegalStateException("Not allowed on concurrent request");
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    throw new IllegalStateException("Not allowed on concurrent request");
  }

  @Override
  public boolean isAsyncStarted() {
    return false;
  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new IllegalStateException("Not allowed on concurrent request");
  }
}
