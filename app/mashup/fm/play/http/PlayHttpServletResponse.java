/** 
* Copyright 2011 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
* @author Felipe Oliveira (http://mashup.fm)
* 
*/
package mashup.fm.play.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import play.mvc.Http;
import play.mvc.Http.Response;

/**
 * The Class PlayHttpServletResponse.
 */
public class PlayHttpServletResponse implements HttpServletResponse {
	
	/** The res. */
	private Response res;

	/**
	 * Instantiates a new play http servlet response.
	 *
	 * @param response the response
	 */
	public PlayHttpServletResponse(Response response) {
		this.res = response;
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	@Override
	public void addCookie(Cookie cookie) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	@Override
	public void addDateHeader(String name, long date) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHeader(String name, String value) {
		Http.Header header = new Http.Header(name, value);
		this.res.headers.put(name, header);
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
	 */
	@Override
	public void addIntHeader(String name, int value) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	@Override
	public boolean containsHeader(String name) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 */
	@Override
	public String encodeRedirectUrl(String url) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	@Override
	public String encodeRedirectURL(String url) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 */
	@Override
	public String encodeUrl(String url) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	@Override
	public String encodeURL(String url) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	@Override
	public void flushBuffer() throws IOException {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		throw new RuntimeException("Method not implemented!");
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getContentType()
	 */
	@Override
	public String getContentType() {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	@Override
	public Locale getLocale() {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		//return this.res.out;
		//return ServletResponse
		return null;
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	@Override
	public boolean isCommitted() {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#reset()
	 */
	@Override
	public void reset() {
		this.res.reset();		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	@Override
	public void resetBuffer() {
		this.res.reset();
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	@Override
	public void sendError(int sc) throws IOException {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
	 */
	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	@Override
	public void sendRedirect(String location) throws IOException {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	@Override
	public void setBufferSize(int size) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String charset) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	@Override
	public void setContentLength(int len) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String type) {
		this.res.contentType = type;
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	@Override
	public void setDateHeader(String name, long date) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeader(String name, String value) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	@Override
	public void setIntHeader(String name, int value) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale loc) {
		throw new RuntimeException("Method not implemented!");
		
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	@Override
	public void setStatus(int sc) {
		this.res.status = sc;
	}

	/** (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 */
	@Override
	public void setStatus(int sc, String sm) {
		this.res.status = sc;
	}

}
