package com.tea.common.util;

import com.alibaba.dubbo.common.io.StreamUtils;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;





public class ServletInputStreamEx extends ServletInputStream {
	
	private final ByteArrayInputStream sourceStream;
	private boolean isReady = true;
	private boolean isFinished = false;
	private final byte[] srcData;




	public ServletInputStreamEx(byte[] data) {
		this.srcData = data;
		this.sourceStream = new ByteArrayInputStream(data);
	}

	public ServletInputStreamEx(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int k = inputStream.read(b);
		while (k > 0)
		{
			out.write(b,0,k);
			k = inputStream.read(b);
		}
		inputStream.close();
		byte[] dd = out.toByteArray();
		this.srcData = dd;
		this.sourceStream = new ByteArrayInputStream(dd);
		out.close();
	}

	/**
	 * Return the underlying source stream (never {@code null}).
	 */
	public final InputStream getSourceStream() {
		return this.sourceStream;
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.sourceStream.close();
	}

	public boolean isReady()
	{
		return isReady;
	}
	public boolean isFinished()
	{
		return  isFinished;
	}

//	public void setReadListener(ReadListener var1)
//	{
//		throw new RuntimeException("Not support setReadListener");
//	}

	@Override
	public int read() throws IOException {

			int c = sourceStream.read();
			if(c == -1)
			{
				isReady = false;
				isFinished = true;
			}
			return c;
	}
	
	public byte[] getData()
	{
		return this.srcData;
	}


	@Override
	public long skip(long n) throws IOException {
		return sourceStream.skip(n);
	}

	@Override
	public int available() throws IOException {
		return sourceStream.available();
	}

	@Override
	public synchronized void mark(int readlimit) {
		sourceStream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		isReady = true;
		isFinished = false;
		sourceStream.reset();
	}

	@Override
	public boolean markSupported() {
		return sourceStream.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int k = sourceStream.read(b, off, len);
		if(k <= 0)
		{
			isReady = false;
			isFinished = true;
		}
		return k;
	}
}
