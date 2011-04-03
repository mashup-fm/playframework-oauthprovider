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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.oauth.OAuth;
import net.oauth.OAuthException;

/**
 * The HMAC-SHA1 signature method.
 * 
 * @author John Kristian
 */
class HMAC_SHA1 extends OAuthSignatureMethod {

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#getSignature(java.lang.String)
	 */
	@Override
	protected String getSignature(String baseString) throws OAuthException {
		try {
			String signature = base64Encode(computeSignature(baseString));
			return signature;
		} catch (GeneralSecurityException e) {
			throw new OAuthException(e);
		} catch (UnsupportedEncodingException e) {
			throw new OAuthException(e);
		}
	}

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#isValid(java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean isValid(String signature, String baseString)
			throws OAuthException {
		try {
			byte[] expected = computeSignature(baseString);
			byte[] actual = decodeBase64(signature);
			return equals(expected, actual);
		} catch (GeneralSecurityException e) {
			throw new OAuthException(e);
		} catch (UnsupportedEncodingException e) {
			throw new OAuthException(e);
		}
	}

	/**
	 * Compute signature.
	 *
	 * @param baseString the base string
	 * @return the byte[]
	 * @throws GeneralSecurityException the general security exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private byte[] computeSignature(String baseString)
			throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKey key = null;
		synchronized (this) {
			if (this.key == null) {
				String keyString = OAuth.percentEncode(getConsumerSecret())
						+ '&' + OAuth.percentEncode(getTokenSecret());
				byte[] keyBytes = keyString.getBytes(ENCODING);
				this.key = new SecretKeySpec(keyBytes, MAC_NAME);
			}
			key = this.key;
		}
		Mac mac = Mac.getInstance(MAC_NAME);
		mac.init(key);
		byte[] text = baseString.getBytes(ENCODING);
		return mac.doFinal(text);
	}

	/** ISO-8859-1 or US-ASCII would work, too. */
	private static final String ENCODING = OAuth.ENCODING;

	/** The Constant MAC_NAME. */
	private static final String MAC_NAME = "HmacSHA1";

	/** The key. */
	private SecretKey key = null;

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#setConsumerSecret(java.lang.String)
	 */
	@Override
	public void setConsumerSecret(String consumerSecret) {
		synchronized (this) {
			key = null;
		}
		super.setConsumerSecret(consumerSecret);
	}

	/* (non-Javadoc)
	 * @see net.oauth.signature.OAuthSignatureMethod#setTokenSecret(java.lang.String)
	 */
	@Override
	public void setTokenSecret(String tokenSecret) {
		synchronized (this) {
			key = null;
		}
		super.setTokenSecret(tokenSecret);
	}

}
