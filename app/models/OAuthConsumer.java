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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The Class OAuthConsumer.
 */
@Entity
public class OAuthConsumer extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the property whose value is the Accept-Encoding header in
	 * HTTP requests.
	 */
	public static final String ACCEPT_ENCODING = "HTTP.header.Accept-Encoding";

	/**
	 * The name of the property whose value is the <a
	 * href="http://oauth.pbwiki.com/AccessorSecret">Accessor Secret</a>.
	 */
	public static final String ACCESSOR_SECRET = "oauth_accessor_secret";

	/** The name. */
	public String name;

	/** The description. */
	public String description;

	/** The callback url. */
	public String callbackURL;

	/** The consumer key. */
	@Required
	public String consumerKey;

	/** The consumer secret. */
	@Required
	public String consumerSecret;

	/** The service provider. */
	@ManyToOne
	public OAuthServiceProvider serviceProvider;

	/** The http method. */
	public String httpMethod;

	/** The signature method. */
	public String signatureMethod;

	/** The x509 certificate. */
	public String x509Certificate;

	/**
	 * Instantiates a new o auth consumer.
	 *
	 * @param callbackURL the callback url
	 * @param consumerKey the consumer key
	 * @param consumerSecret the consumer secret
	 * @param serviceProvider the service provider
	 */
	public OAuthConsumer(String callbackURL, String consumerKey,
			String consumerSecret, OAuthServiceProvider serviceProvider) {
		this.callbackURL = callbackURL;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.serviceProvider = serviceProvider;
	}

	/*
	 * private final Map<String, Object> properties = new HashMap<String,
	 * Object>();
	 * 
	 * public Object getProperty(String name) { return properties.get(name); }
	 * 
	 * public void setProperty(String name, Object value) { properties.put(name,
	 * value); }
	 */

}
