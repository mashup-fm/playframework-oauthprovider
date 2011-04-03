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

package models;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.CRUD.Hidden;

/**
 * Properties of one User of an OAuthConsumer. Properties may be added freely,
 * e.g. to support extensions.
 * 
 * @author John Kristian
 */
@Entity
public class OAuthAccessor extends Model implements Cloneable, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5590788443138352999L;

	/** The consumer. */
	@ManyToOne
	@Required
	public OAuthConsumer consumer;

	/** The request token. */
	@Required
	public String requestToken;

	/** The access token. */
	@Required
	public String accessToken;

	/** The token secret. */
	@Required
	public String tokenSecret;

	/** The authorized. */
	public Date authorized;

	/** The http method. */
	@Hidden
	private String httpMethod;

	/**
	 * Instantiates a new o auth accessor.
	 *
	 * @param consumer the consumer
	 */
	public OAuthAccessor(OAuthConsumer consumer) {
		this.consumer = consumer;
		requestToken = null;
		accessToken = null;
		tokenSecret = null;
		authorized = null;
	}

	// private final Map<String, Object> properties = new HashMap<String,
	// Object>();

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public OAuthAccessor clone() {
		try {
			return (OAuthAccessor) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * public Object getProperty(String name) { return properties.get(name); }
	 * 
	 * public void setProperty(String name, Object value) { properties.put(name,
	 * value); }
	 */

	/**
	 * Construct a request message containing the given parameters but no body.
	 * Don't send the message, merely construct it. The caller will ordinarily
	 * send it, for example by calling OAuthClient.invoke or access.
	 * 
	 * @param method
	 *            the HTTP request method. If this is null, use the default
	 *            method; that is getProperty("httpMethod") or (if that's null)
	 *            consumer.getProperty("httpMethod") or (if that's null)
	 *            OAuthMessage.GET.
	 */
	public OAuthMessage newRequestMessage(String method, String url,
			Collection<? extends Map.Entry> parameters, InputStream body)
			throws OAuthException, IOException, URISyntaxException {
		if (method == null) {
			method = httpMethod;
			if (method == null) {
				method = consumer.httpMethod;
				if (method == null) {
					method = OAuthMessage.GET;
				}
			}
		}
		OAuthMessage message = new OAuthMessage(method, url, parameters, body);
		message.addRequiredParameters(this);
		return message;
	}

	/**
	 * New request message.
	 *
	 * @param method the method
	 * @param url the url
	 * @param parameters the parameters
	 * @return the o auth message
	 * @throws OAuthException the o auth exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the uRI syntax exception
	 */
	public OAuthMessage newRequestMessage(String method, String url,
			Collection<? extends Map.Entry> parameters) throws OAuthException,
			IOException, URISyntaxException {
		return newRequestMessage(method, url, parameters, null);
	}

	/* (non-Javadoc)
	 * @see play.db.jpa.JPABase#toString()
	 */
	@Override
	public String toString() {
		return "OAuthAccessor [consumer=" + consumer + ", requestToken="
				+ requestToken + ", accessToken=" + accessToken
				+ ", tokenSecret=" + tokenSecret + ", authorized=" + authorized
				+ ", httpMethod=" + httpMethod + "]";
	}

}
