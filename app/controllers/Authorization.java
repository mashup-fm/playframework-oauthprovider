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

package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import mashup.fm.oauth.provider.MashupOAuthProvider;
import mashup.fm.oauth.provider.util.ExceptionUtil;
import mashup.fm.oauth.provider.util.MashupOAuthUtil;
import mashup.fm.play.http.PlayHttpServletRequest;
import mashup.fm.play.http.PlayHttpServletResponse;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class Authorization.
 */
public class Authorization extends Controller {

	/**
	 * Confirm.
	 *
	 * @param request the request
	 * @param response the response
	 */
	public static void confirm() {

		try {
			OAuthMessage requestMessage = MashupOAuthUtil.getMessage(request, null);

			OAuthAccessor accessor = MashupOAuthProvider.getAccessor(requestMessage);

			if (Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
				// already authorized send the user back
				returnToConsumer(request, response, accessor);
			} else {
				sendToAuthorizePage(request, response, accessor);
			}

		} catch (Exception e) {
			MashupOAuthProvider.handleException(e, request, response, true);
		}

	}

	/**
	 * Request.
	 *
	 * @param request the request
	 * @param response the response
	 */
	public static void request() {

		try {
			OAuthMessage requestMessage = MashupOAuthUtil.getMessage(request, null);

			OAuthAccessor accessor = MashupOAuthProvider.getAccessor(requestMessage);

			String userId = request.params.get("userId");
			if (userId == null) {
				sendToAuthorizePage(request, response, accessor);
			}
			// set userId in accessor and mark it as authorized
			MashupOAuthProvider.markAsAuthorized(accessor, userId);

			returnToConsumer(request, response, accessor);

		} catch (Exception e) {
			MashupOAuthProvider.handleException(e, request, response, true);
		}
	}

	/**
	 * Send to authorize page.
	 *
	 * @param request the request
	 * @param response the response
	 * @param accessor the accessor
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	private static void sendToAuthorizePage(Request request, Response response, OAuthAccessor accessor) throws IOException, ServletException {
		String callback = request.params.get("oauth_callback");
		if (callback == null || callback.length() <= 0) {
			callback = "none";
		}
		String consumer_description = (String) accessor.consumer.getProperty("description");
		request.params.put("CONS_DESC", consumer_description);
		request.params.put("CALLBACK", callback);
		request.params.put("TOKEN", accessor.requestToken);
		Authorization.request();
	}

	/**
	 * Return to consumer.
	 *
	 * @param request the request
	 * @param response the response
	 * @param accessor the accessor
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	private static void returnToConsumer(Request request, Response response, OAuthAccessor accessor) throws IOException, ServletException {
		// send the user back to site's callBackUrl
		String callback = request.params.get("oauth_callback");
		if ("none".equals(callback) && accessor.consumer.callbackURL != null && accessor.consumer.callbackURL.length() > 0) {
			// first check if we have something in our properties file
			callback = accessor.consumer.callbackURL;
		}

		if ("none".equals(callback)) {
			// no call back it must be a client
			response.setContentTypeIfNotSet(("text/plain"));
			PrintWriter out = new PrintWriter(response.out);
			out.println("You have successfully authorized '" + accessor.consumer.getProperty("description") + "'. Please close this browser window and click continue" + " in the client.");
			out.close();
		} else {
			// if callback is not passed in, use the callback from config
			if (callback == null || callback.length() <= 0) {
				callback = accessor.consumer.callbackURL;
			}
			String token = accessor.requestToken;
			if (token != null) {
				callback = OAuth.addParameters(callback, "oauth_token", token);
			}

			response.status = HttpServletResponse.SC_MOVED_TEMPORARILY;
			response.setHeader("Location", callback);
		}
	}

}
