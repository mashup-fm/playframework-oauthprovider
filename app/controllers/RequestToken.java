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
import java.io.OutputStream;

import javax.servlet.ServletException;

import mashup.fm.oauth.provider.MashupOAuthProvider;
import mashup.fm.oauth.provider.util.MashupOAuthUtil;
import models.OAuthAccessor;
import models.OAuthConsumer;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import play.Logger;
import play.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestToken.
 */
public class RequestToken extends Controller {

	/**
	 * Process request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public static void request() throws IOException, ServletException {
		try {
			OAuthMessage requestMessage = MashupOAuthUtil.getMessage(request,
					null);

			OAuthConsumer consumer = MashupOAuthProvider
					.getConsumer(requestMessage);

			OAuthAccessor accessor = new OAuthAccessor(consumer);
			MashupOAuthProvider.VALIDATOR.validateMessage(requestMessage,
					accessor);
			{
				// Support the 'Variable Accessor Secret' extension
				// described in http://oauth.pbwiki.com/AccessorSecret
				String secret = requestMessage
						.getParameter("oauth_accessor_secret");
				if (secret != null) {
					Logger.info("Secret: " + secret);
					accessor.tokenSecret = secret;
				} else {
					Logger.info("Empty Secret!");
				}
			}

			// generate request_token and secret
			accessor = MashupOAuthProvider.generateRequestToken(accessor);
			Logger.info("Accessor: %s", accessor);

			response.setContentTypeIfNotSet("text/plain");
			OutputStream out = response.out;
			OAuth.formEncode(OAuth.newList("oauth_token",
					accessor.requestToken, "oauth_token_secret",
					accessor.tokenSecret), out);
			out.close();

		} catch (Exception e) {
			MashupOAuthProvider.handleException(e, request, response, true);
		}

	}
}
