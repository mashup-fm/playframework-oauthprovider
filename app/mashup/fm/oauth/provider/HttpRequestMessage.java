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
package mashup.fm.oauth.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import play.mvc.Http.Header;
import play.mvc.Http.Request;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpRequestMessage.
 */
public class HttpRequestMessage extends OAuthMessage {

	/**
	 * Instantiates a new http request message.
	 *
	 * @param request the request
	 * @param URL the uRL
	 */
	public HttpRequestMessage(Request request, String URL) {
		super(request.method, URL, getParameters(request));
		this.request = request;
		copyHeaders(request, request.headers);
	}

	/** The request. */
	private final Request request;

	/* (non-Javadoc)
	 * @see net.oauth.OAuthMessage#getBodyAsStream()
	 */
	@Override
	public InputStream getBodyAsStream() throws IOException {
		return request.body;
	}

	/* (non-Javadoc)
	 * @see net.oauth.OAuthMessage#getBodyEncoding()
	 */
	@Override
	public String getBodyEncoding() {
		return "UTF-8";
	}

	/**
	 * Copy headers.
	 *
	 * @param request the request
	 * @param into the into
	 */
	private static void copyHeaders(Request request, Map<String, Header> into) {
		Set<String> names = request.headers.keySet();
		if (names != null) {
			for (String name : names) {
				Header header = request.headers.get(name);
				for (String value : header.values) {
						into.put(name, new Header(name, value));
				}
			}
		}
	}

	/**
	 * Gets the parameters.
	 *
	 * @param request the request
	 * @return the parameters
	 */
	public static List<OAuth.Parameter> getParameters(Request request) {
		List<OAuth.Parameter> list = new ArrayList<OAuth.Parameter>();
		if ( request.headers != null ) {
			Map<String, Header> headers = request.headers;
		for (Header header : headers.values()) {
			for (OAuth.Parameter parameter : OAuthMessage.decodeAuthorization(header.value())) {
				if (!"realm".equalsIgnoreCase(parameter.getKey())) {
					list.add(parameter);
				}
			}
		}
		}
		if ( request.params != null ) {
			Map<String, String> map = request.params.allSimple();
		for (String name : map.keySet()) {
			String value = map.get(name);
				list.add(new OAuth.Parameter(name, value));
			
		}
		}
		return list;
	}

}