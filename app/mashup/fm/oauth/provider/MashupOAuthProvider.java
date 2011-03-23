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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;

import mashup.fm.oauth.provider.simple.MashupSimpleOAuthValidator;
import mashup.fm.oauth.provider.util.ExceptionUtil;
import mashup.fm.oauth.provider.util.MashupOAuthUtil;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;
import play.Play;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class MashupOAuthProvider.
 */
public class MashupOAuthProvider {

	/** The Constant VALIDATOR. */
	public static final MashupOAuthValidator VALIDATOR = new MashupSimpleOAuthValidator();

	/** The Constant ALL_CONSUMERS. */
	private static final Map<String, OAuthConsumer> ALL_CONSUMERS = Collections.synchronizedMap(new HashMap<String, OAuthConsumer>(10));

	/** The Constant ALL_TOKENS. */
	private static final Collection<OAuthAccessor> ALL_TOKENS = new HashSet<OAuthAccessor>();

	/** The consumer properties. */
	private static Properties consumerProperties = null;

	/**
	 * Load consumers.
	 *
	 * @param config the config
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static synchronized void loadConsumers(ServletConfig config) throws IOException {
		Properties p = consumerProperties;
		if (p == null) {
			p = new Properties();
			String resourceName = "/" + MashupOAuthProvider.class.getPackage().getName().replace(".", "/") + "/provider.properties";
			URL resource = MashupOAuthProvider.class.getClassLoader().getResource(resourceName);
			if (resource == null) {
				throw new IOException("resource not found: " + resourceName);
			}
			InputStream stream = resource.openStream();
			try {
				p.load(stream);
			} finally {
				stream.close();
			}
		}
		consumerProperties = p;

		// for each entry in the properties file create a OAuthConsumer
		for (Map.Entry prop : p.entrySet()) {
			String consumer_key = (String) prop.getKey();
			// make sure it's key not additional properties
			if (!consumer_key.contains(".")) {
				String consumer_secret = (String) prop.getValue();
				if (consumer_secret != null) {
					String consumer_description = p.getProperty(consumer_key + ".description");
					String consumer_callback_url = p.getProperty(consumer_key + ".callbackURL");
					// Create OAuthConsumer w/ key and secret
					OAuthConsumer consumer = new OAuthConsumer(consumer_callback_url, consumer_key, consumer_secret, null);
					consumer.setProperty("name", consumer_key);
					consumer.setProperty("description", consumer_description);
					ALL_CONSUMERS.put(consumer_key, consumer);
				}
			}
		}

	}

	/**
	 * Gets the consumer.
	 *
	 * @param requestMessage the request message
	 * @return the consumer
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException the o auth problem exception
	 */
	public static synchronized OAuthConsumer getConsumer(OAuthMessage requestMessage) throws IOException, OAuthProblemException {

		OAuthConsumer consumer = null;
		// try to load from local cache if not throw exception
		String consumer_key = requestMessage.getConsumerKey();

		consumer = MashupOAuthProvider.ALL_CONSUMERS.get(consumer_key);
		
 		//String mockConfig = Play.configuration.getProperty("oauthprovider.mock");
		String mockConfig = "true";
		if ( mockConfig != null && Boolean.valueOf(mockConfig) ) {
			// OAuthConsumer(java.lang.String callbackURL, java.lang.String consumerKey, java.lang.String consumerSecret, net.oauth.OAuthServiceProvider serviceProvider)
			String callbackUrl = "";
			String consumerKey = "";
			String consumerSecret = "";
			String requestTokenUrl = "";
			String userAuthorizationUrl = "";
			String accessTokenUrl = ""; 
			OAuthServiceProvider serviceProvider = new OAuthServiceProvider(requestTokenUrl, userAuthorizationUrl, accessTokenUrl);
			consumer = new OAuthConsumer( callbackUrl, consumerKey, consumerSecret, serviceProvider );
		}

		if (consumer == null) {
			Logger.error("Invalid Consumer Key: " + consumer_key);
			OAuthProblemException problem = new OAuthProblemException("token_rejected");
			throw problem;
		}

		return consumer;
	}

	/**
	 * Gets the accessor.
	 *
	 * @param requestMessage the request message
	 * @return the accessor
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException the o auth problem exception
	 */
	public static synchronized OAuthAccessor getAccessor(OAuthMessage requestMessage) throws IOException, OAuthProblemException {

		// try to load from local cache if not throw exception
		String consumer_token = requestMessage.getToken();
		OAuthAccessor accessor = null;
		for (OAuthAccessor a : MashupOAuthProvider.ALL_TOKENS) {
			if (a.requestToken != null) {
				if (a.requestToken.equals(consumer_token)) {
					accessor = a;
					break;
				}
			} else if (a.accessToken != null) {
				if (a.accessToken.equals(consumer_token)) {
					accessor = a;
					break;
				}
			}
		}

		if (accessor == null) {
			OAuthProblemException problem = new OAuthProblemException("token_expired");
			throw problem;
		}

		return accessor;
	}

	/**
	 * Mark as authorized.
	 *
	 * @param accessor the accessor
	 * @param userId the user id
	 * @throws OAuthException the o auth exception
	 */
	public static synchronized void markAsAuthorized(OAuthAccessor accessor, String userId) throws OAuthException {

		// first remove the accessor from cache
		ALL_TOKENS.remove(accessor);

		accessor.setProperty("user", userId);
		accessor.setProperty("authorized", Boolean.TRUE);

		// update token in local cache
		ALL_TOKENS.add(accessor);
	}

	/**
	 * Generate request token.
	 *
	 * @param accessor the accessor
	 * @throws OAuthException the o auth exception
	 */
	public static synchronized void generateRequestToken(OAuthAccessor accessor) throws OAuthException {

		// generate oauth_token and oauth_secret
		String consumer_key = (String) accessor.consumer.getProperty("name");
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
		ALL_TOKENS.add(accessor);

	}

	/**
	 * Generate access token.
	 *
	 * @param accessor the accessor
	 * @throws OAuthException the o auth exception
	 */
	public static synchronized void generateAccessToken(OAuthAccessor accessor) throws OAuthException {

		// generate oauth_token and oauth_secret
		String consumer_key = (String) accessor.consumer.getProperty("name");
		// generate token and secret based on consumer_key

		// for now use md5 of name + current time as token
		String token_data = consumer_key + System.nanoTime();
		String token = DigestUtils.md5Hex(token_data);
		// first remove the accessor from cache
		ALL_TOKENS.remove(accessor);

		accessor.requestToken = null;
		accessor.accessToken = token;

		// update token in local cache
		ALL_TOKENS.add(accessor);
	}

	/**
	 * Handle exception.
	 *
	 * @param e the e
	 * @param request the request
	 * @param response the response
	 * @param sendBody the send body
	 */
	public static void handleException(Exception e, Request request, Response response, boolean sendBody) {
		Logger.error(ExceptionUtil.getStackTrace(e));
		String realm = (request.secure) ? "https://" : "http://";
		realm += request.host;
		MashupOAuthUtil.handleException(response, e, realm, sendBody);
	}

}
