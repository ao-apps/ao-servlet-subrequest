/*
 * ao-servlet-subrequest - Servlet sub-request wrappers with optional concurrency.
 * Copyright (C) 2016, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-servlet-subrequest.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.servlet.subrequest;

import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * Synchronizes access to the wrapped input stream.
 */
public class ThreadSafeServletInputStream extends ServletInputStream {

	private static class Lock {}
	private final Lock lock = new Lock();

	private final ServletInputStream in;

	public ThreadSafeServletInputStream(ServletInputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		synchronized(lock) {
			return in.read();
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		synchronized(lock) {
			return in.read(b);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		synchronized(lock) {
			return in.read(b, off, len);
		}
	}

	@Override
	public long skip(long n) throws IOException {
		synchronized(lock) {
			return in.skip(n);
		}
	}

	@Override
	public int available() throws IOException {
		synchronized(lock) {
			return in.available();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized(lock) {
			in.close();
		}
	}

	@Override
	public void mark(int readlimit) {
		synchronized(lock) {
			in.mark(readlimit);
		}
	}

	@Override
	public void reset() throws IOException {
		synchronized(lock) {
			in.reset();
		}
	}

	@Override
	public boolean markSupported() {
		synchronized(lock) {
			return in.markSupported();
		}
	}

	@Override
	public int readLine(byte[] b, int off, int len) throws IOException {
		synchronized(lock) {
			return in.readLine(b, off, len);
		}
	}

	@Override
	public boolean isFinished() {
		synchronized(lock) {
			return in.isFinished();
		}
	}

	@Override
	public boolean isReady() {
		synchronized(lock) {
			return in.isReady();
		}
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		synchronized(lock) {
			in.setReadListener(readListener);
		}
	}
}
