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
 * along with ao-servlet-subrequest.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.servlet.subrequest;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * Synchronizes access to the wrapped output stream.
 */
public final class ThreadSafeServletOutputStream extends ServletOutputStream {

	private static class Lock {/* Empty lock class to help heap profile */}
	private final Lock lock = new Lock();

	private final ServletOutputStream out;

	public ThreadSafeServletOutputStream(ServletOutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		synchronized(lock) {
			out.write(b);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		synchronized(lock) {
			out.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		synchronized(lock) {
			out.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized(lock) {
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized(lock) {
			out.close();
		}
	}

	@Override
	public void print(String s) throws IOException {
		synchronized(lock) {
			out.print(s);
		}
	}

	@Override
	public void print(boolean b) throws IOException {
		synchronized(lock) {
			out.print(b);
		}
	}

	@Override
	public void print(char c) throws IOException {
		synchronized(lock) {
			out.print(c);
		}
	}

	@Override
	public void print(int i) throws IOException {
		synchronized(lock) {
			out.print(i);
		}
	}

	@Override
	public void print(long l) throws IOException {
		synchronized(lock) {
			out.print(l);
		}
	}

	@Override
	public void print(float f) throws IOException {
		synchronized(lock) {
			out.print(f);
		}
	}

	@Override
	public void print(double d) throws IOException {
		synchronized(lock) {
			out.print(d);
		}
	}

	@Override
	public void println() throws IOException {
		synchronized(lock) {
			out.println();
		}
	}

	@Override
	public void println(String s) throws IOException {
		synchronized(lock) {
			out.println(s);
		}
	}

	@Override
	public void println(boolean b) throws IOException {
		synchronized(lock) {
			out.println(b);
		}
	}

	@Override
	public void println(char c) throws IOException {
		synchronized(lock) {
			out.println(c);
		}
	}

	@Override
	public void println(int i) throws IOException {
		synchronized(lock) {
			out.println(i);
		}
	}

	@Override
	public void println(long l) throws IOException {
		synchronized(lock) {
			out.println(l);
		}
	}

	@Override
	public void println(float f) throws IOException {
		synchronized(lock) {
			out.println(f);
		}
	}

	@Override
	public void println(double d) throws IOException {
		synchronized(lock) {
			out.println(d);
		}
	}

	@Override
	public boolean isReady() {
		synchronized(lock) {
			return out.isReady();
		}
	}

	@Override
	public void setWriteListener(WriteListener wl) {
		synchronized(lock) {
			out.setWriteListener(wl);
		}
	}
}
