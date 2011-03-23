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
package mashup.fm.oauth.provider.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import mashup.fm.oauth.provider.HttpRequestMessage;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class MashupOAuthUtil.
 */
public abstract class MashupOAuthUtil {

	/**
	 * Gets the message.
	 *
	 * @param request the request
	 * @param URL the uRL
	 * @return the message
	 */
	public static OAuthMessage getMessage(Request request, String URL) {
		if (URL == null) {
			URL = request.url.toString();
		}
		int q = URL.indexOf('?');
		if (q >= 0) {
			URL = URL.substring(0, q);
			// The query string parameters will be included in
			// the result from getParameters(request).
		}
		return new HttpRequestMessage(request, URL);
	}

	/**
	 * Gets the request url.
	 *
	 * @param request the request
	 * @return the request url
	 */
	public static String getRequestURL(Request request) {
		StringBuffer url = new StringBuffer(request.url);
		String queryString = request.querystring;
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}

	/**
	 * Handle exception.
	 *
	 * @param response the response
	 * @param e the e
	 * @param realm the realm
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	public static void handleException(Response response, Exception e, String realm) throws IOException, ServletException {
		handleException(response, e, realm, true);
	}

	/**
	 * Handle exception.
	 *
	 * @param response the response
	 * @param e the e
	 * @param realm the realm
	 * @param sendBody the send body
	 */
	public static void handleException(Response response, Exception e, String realm, boolean sendBody) {
		if (e instanceof OAuthProblemException) {
			OAuthProblemException problem = (OAuthProblemException) e;
			Object httpCode = problem.getParameters().get(OAuthProblemException.HTTP_STATUS_CODE);
			if (httpCode == null) {
				httpCode = PROBLEM_TO_HTTP_CODE.get(problem.getProblem());
			}
			if (httpCode == null) {
				httpCode = SC_FORBIDDEN;
			}
			response.reset();
			
			response.status = Integer.parseInt(httpCode.toString());

			try {
				OAuthMessage message = new OAuthMessage(null, null, problem.getParameters().entrySet());
				response.headers.put("WWW-Authenticate", new Header("WWW-Authenticate", message.getAuthorizationHeader(realm)));
				if (sendBody) {
					sendForm(response, message.getParameters());
				}
			} catch (Throwable t) {
				ExceptionUtil.runException(t);
			}

		} else {
			ExceptionUtil.runException(e);
		}
	}

	/** The Constant SC_FORBIDDEN. */
	private static final Integer SC_FORBIDDEN = new Integer(HttpServletResponse.SC_FORBIDDEN);

	/** The Constant PROBLEM_TO_HTTP_CODE. */
	private static final Map<String, Integer> PROBLEM_TO_HTTP_CODE = OAuth.Problems.TO_HTTP_CODE;

	/**
	 * Send form.
	 *
	 * @param response the response
	 * @param parameters the parameters
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void sendForm(Response response, List<Map.Entry<String, String>> parameters) throws IOException {
		response.reset();
		response.setContentTypeIfNotSet(OAuth.FORM_ENCODED + ";charset=" + OAuth.ENCODING);
		OAuth.formEncode(parameters, response.out);
	}

	/**
	 * Html encode.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String htmlEncode(String s) {
		if (s == null) {
			return null;
		}
		StringBuilder html = new StringBuilder(s.length());
		for (char c : s.toCharArray()) {
			switch (c) {
			case '<':
				html.append("&lt;");
				break;
			case '>':
				html.append("&gt;");
				break;
			case '&':
				html.append("&amp;");
				// This also takes care of numeric character references;
				// for example &#169 becomes &amp;#169.
				break;
			case '"':
				html.append("&quot;");
				break;
			default:
				html.append(c);
				break;
			}
		}
		return html.toString();
	}

}
