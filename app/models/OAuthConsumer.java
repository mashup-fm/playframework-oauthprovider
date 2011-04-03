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
import controllers.CRUD.Hidden;

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
	@Required
	public String name;

	/** The description. */
	@Required
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
	@Required
	public OAuthServiceProvider serviceProvider;
	
	@ManyToOne
	@Required
	public User user;

	/** The http method. */
	@Hidden
	public String httpMethod;

	/** The signature method. */
	@Hidden
	public String signatureMethod;

	/** The x509 certificate. */
	@Hidden
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

	public String toString() {
		return name;
	}
	
	public static OAuthConsumer findByUserId(String id) {
		return OAuthConsumer.find("user.id = ?", id).first();
	}

}
