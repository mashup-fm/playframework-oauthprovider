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

package net.oauth.signature;

import net.oauth.OAuth;
import net.oauth.OAuthException;

/**
 * The PLAINTEXT signature method.
 * 
 * @author John Kristian
 */
class PLAINTEXT extends OAuthSignatureMethod {

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#getSignature(java.lang.String)
	 */
	@Override
	public String getSignature(String baseString) {
		return getSignature();
	}

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#isValid(java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean isValid(String signature, String baseString)
			throws OAuthException {
		return equals(getSignature(), signature);
	}

	/**
	 * Gets the signature.
	 *
	 * @return the signature
	 */
	private synchronized String getSignature() {
		if (signature == null) {
			signature = OAuth.percentEncode(getConsumerSecret()) + '&'
					+ OAuth.percentEncode(getTokenSecret());
		}
		return signature;
	}

	/** The signature. */
	private String signature = null;

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#setConsumerSecret(java.lang.String)
	 */
	@Override
	public void setConsumerSecret(String consumerSecret) {
		synchronized (this) {
			signature = null;
		}
		super.setConsumerSecret(consumerSecret);
	}

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#setTokenSecret(java.lang.String)
	 */
	@Override
	public void setTokenSecret(String tokenSecret) {
		synchronized (this) {
			signature = null;
		}
		super.setTokenSecret(tokenSecret);
	}

}
