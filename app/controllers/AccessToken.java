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

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

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
 * The Class AccessToken.
 */
public class AccessToken extends Controller {

	/**
	 * Process request.
	 *
	 * @param request the request
	 * @param response the response
	 */
	public static void request() {
		try {
			OAuthMessage requestMessage = MashupOAuthUtil.getMessage(request, null);

			OAuthAccessor accessor = MashupOAuthProvider.getAccessor(requestMessage);
			MashupOAuthProvider.VALIDATOR.validateMessage(requestMessage, accessor);

			// make sure token is authorized
			if (!Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
				OAuthProblemException problem = new OAuthProblemException("permission_denied");
				throw problem;
			}
			// generate access token and secret
			MashupOAuthProvider.generateAccessToken(accessor);

			response.setContentTypeIfNotSet("text/plain");
			OutputStream out = response.out;
			OAuth.formEncode(OAuth.newList("oauth_token", accessor.accessToken, "oauth_token_secret", accessor.tokenSecret), out);
			out.close();

		} catch (Exception e) {
			MashupOAuthProvider.handleException(e, request, response, true);
		}
	}

}
