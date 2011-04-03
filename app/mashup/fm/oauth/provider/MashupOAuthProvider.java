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
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;

import mashup.fm.oauth.provider.simple.MashupSimpleOAuthValidator;
import mashup.fm.oauth.provider.util.ExceptionUtil;
import mashup.fm.oauth.provider.util.MashupOAuthUtil;
import models.OAuthAccessor;
import models.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class MashupOAuthProvider.
 */
public class MashupOAuthProvider {

	/** The Constant VALIDATOR. */
	public static final MashupOAuthValidator VALIDATOR = new MashupSimpleOAuthValidator();

	/**
	 * Gets the consumer.
	 * 
	 * @param requestMessage
	 *            the request message
	 * @return the consumer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException
	 *             the o auth problem exception
	 */
	public static synchronized OAuthConsumer getConsumer(
			OAuthMessage requestMessage) throws IOException,
			OAuthProblemException {
		// Try to load from local cache if not throw exception
		String consumer_key = requestMessage.getConsumerKey();
		OAuthConsumer consumer = OAuthConsumer.find("consumerKey", consumer_key)
				.first();

		if (consumer == null) {
			Logger.error("Invalid Consumer Key: " + consumer_key);
			OAuthProblemException problem = new OAuthProblemException(
					"token_rejected");
			throw problem;
		}

		return consumer;
	}

	/**
	 * Gets the accessor.
	 * 
	 * @param requestMessage
	 *            the request message
	 * @return the accessor
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException
	 *             the o auth problem exception
	 */
	public static synchronized OAuthAccessor getAccessor(
			OAuthMessage requestMessage) throws IOException,
			OAuthProblemException {
		String consumer_token = requestMessage.getToken();
		Logger.info("Consumer Token: %s", consumer_token);
		OAuthAccessor accessor = OAuthAccessor.find(
				"requestToken = ? or accessToken = ?", consumer_token,
				consumer_token).first();

		Logger.info("Accessor: %s", accessor);

		if (accessor == null) {
			OAuthProblemException problem = new OAuthProblemException(
					"token_expired");
			throw problem;
		}

		return accessor;
	}

	/**
	 * Mark as authorized.
	 * 
	 * @param accessor
	 *            the accessor
	 * @param userId
	 *            the user id
	 * @throws OAuthException
	 *             the o auth exception
	 */
	public static synchronized OAuthAccessor markAsAuthorized(
			OAuthAccessor accessor, String userId) throws OAuthException {
		Logger.info("Mark as Authorized: %s", accessor);

		OAuthConsumer consumer = OAuthConsumer.findByUserId(userId);
		accessor.consumer = consumer;
		accessor.authorized = new Date();
		return accessor.save();
	}

	/**
	 * Generate request token.
	 * 
	 * @param accessor
	 *            the accessor
	 * @throws OAuthException
	 *             the o auth exception
	 */
	public static synchronized OAuthAccessor generateRequestToken(
			OAuthAccessor accessor) throws OAuthException {
		Logger.info("Generate Request Token: %s", accessor);

		// generate oauth_token and oauth_secret
		String consumer_key = accessor.consumer.consumerKey;
		// generate token and secret based on consumer_key

		// for now use md5 of name + current time as token
		String token_data = consumer_key + System.nanoTime();
		String token = DigestUtils.md5Hex(token_data);
		// for now use md5 of name + current time + token as secret
		String secret_data = consumer_key + System.nanoTime() + token;
		String secret = DigestUtils.md5Hex(secret_data);

		accessor.requestToken = token;
		accessor.tokenSecret = secret;
		accessor.accessToken = null;

		// add to the local cache
		// ALL_TOKENS.add(accessor);

		return accessor.save();

	}

	/**
	 * Generate access token.
	 * 
	 * @param accessor
	 *            the accessor
	 * @throws OAuthException
	 *             the o auth exception
	 */
	public static synchronized OAuthAccessor generateAccessToken(
			OAuthAccessor accessor) throws OAuthException {
		Logger.info("Generate Access Token: %s", accessor);

		// generate oauth_token and oauth_secret
		String consumer_key = accessor.consumer.consumerKey;
		// generate token and secret based on consumer_key

		// for now use md5 of name + current time as token
		String token_data = consumer_key + System.nanoTime();
		String token = DigestUtils.md5Hex(token_data);
		// first remove the accessor from cache
		// ALL_TOKENS.remove(accessor);

		accessor.requestToken = null;
		accessor.accessToken = token;

		// update token in local cache
		// ALL_TOKENS.add(accessor);
		return accessor.save();
	}

	/**
	 * Handle exception.
	 * 
	 * @param e
	 *            the e
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param sendBody
	 *            the send body
	 */
	public static void handleException(Exception e, Request request,
			Response response, boolean sendBody) {
		Logger.error(ExceptionUtil.getStackTrace(e));
		String realm = (request.secure) ? "https://" : "http://";
		realm += request.host;
		MashupOAuthUtil.handleException(response, e, realm, sendBody);
	}

}
