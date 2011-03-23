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
package mashup.fm.oauth.provider.simple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import play.Logger;

import mashup.fm.oauth.provider.MashupOAuthValidator;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.signature.OAuthSignatureMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class MashupSimpleOAuthValidator.
 */
public class MashupSimpleOAuthValidator implements MashupOAuthValidator {

	/** The Constant DEFAULT_MAX_TIMESTAMP_AGE. */
	public static final long DEFAULT_MAX_TIMESTAMP_AGE = 5 * 60 * 1000L;
	
	/** The Constant DEFAULT_TIMESTAMP_WINDOW. */
	public static final long DEFAULT_TIMESTAMP_WINDOW = DEFAULT_MAX_TIMESTAMP_AGE;

	/** The Constant SINGLE_PARAMETERS. */
	public static final Set<String> SINGLE_PARAMETERS = constructSingleParameters();

	/**
	 * Construct single parameters.
	 *
	 * @return the sets the
	 */
	private static Set<String> constructSingleParameters() {
		Set<String> s = new HashSet<String>();
		for (String p : new String[] { OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_TOKEN, OAuth.OAUTH_TOKEN_SECRET, OAuth.OAUTH_CALLBACK, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE, OAuth.OAUTH_TIMESTAMP, OAuth.OAUTH_NONCE, OAuth.OAUTH_VERSION }) {
			s.add(p);
		}
		return Collections.unmodifiableSet(s);
	}

	/**
	 * Instantiates a new mashup simple o auth validator.
	 */
	public MashupSimpleOAuthValidator() {
		this(DEFAULT_TIMESTAMP_WINDOW, Double.parseDouble(OAuth.VERSION_1_0));
	}

	/**
	 * Instantiates a new mashup simple o auth validator.
	 *
	 * @param maxTimestampAgeMsec the max timestamp age msec
	 * @param maxVersion the max version
	 */
	public MashupSimpleOAuthValidator(long maxTimestampAgeMsec, double maxVersion) {
		this.maxTimestampAgeMsec = maxTimestampAgeMsec;
		this.maxVersion = maxVersion;
	}

	/** The min version. */
	protected final double minVersion = 1.0;
	
	/** The max version. */
	protected final double maxVersion;
	
	/** The max timestamp age msec. */
	protected final long maxTimestampAgeMsec;
	
	/** The used nonces. */
	private final Set<UsedNonce> usedNonces = new TreeSet<UsedNonce>();

	/**
	 * Release garbage.
	 *
	 * @return the date
	 */
	public Date releaseGarbage() {
		return removeOldNonces(currentTimeMsec());
	}

	/**
	 * Removes the old nonces.
	 *
	 * @param currentTimeMsec the current time msec
	 * @return the date
	 */
	private Date removeOldNonces(long currentTimeMsec) {
		UsedNonce next = null;
		UsedNonce min = new UsedNonce((currentTimeMsec - maxTimestampAgeMsec + 500) / 1000L);
		synchronized (usedNonces) {
			// Because usedNonces is a TreeSet, its iterator produces
			// elements from oldest to newest (their natural order).
			for (Iterator<UsedNonce> iter = usedNonces.iterator(); iter.hasNext();) {
				UsedNonce used = iter.next();
				if (min.compareTo(used) <= 0) {
					next = used;
					break; // all the rest are also new enough
				}
				iter.remove(); // too old
			}
		}
		if (next == null) {
			return null;
		}
		return new Date((next.getTimestamp() * 1000L) + maxTimestampAgeMsec + 500);
	}

	/* (non-Javadoc)
	 * @see mashup.fm.oauth.provider.MashupOAuthValidator#validateMessage(net.oauth.OAuthMessage, net.oauth.OAuthAccessor)
	 */
	@Override
	public void validateMessage(OAuthMessage message, OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException {
		Logger.info("Validate OAuth Message: " + message + ", OAuthAccessor: " + accessor);
		checkSingleParameters(message);
		validateVersion(message);
		validateTimestampAndNonce(message);
		validateSignature(message, accessor);
	}

	/**
	 * Check single parameters.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthException the o auth exception
	 */
	protected void checkSingleParameters(OAuthMessage message) throws IOException, OAuthException {
		// Check for repeated oauth_ parameters:
		Logger.info("Check Single Parameters: %s", message);
		boolean repeated = false;
		Map<String, Collection<String>> nameToValues = new HashMap<String, Collection<String>>();
		for (Map.Entry<String, String> parameter : message.getParameters()) {
			Logger.info("Parameter: %s", parameter);
			String name = parameter.getKey();
			if (SINGLE_PARAMETERS.contains(name)) {
				Collection<String> values = nameToValues.get(name);
				if (values == null) {
					values = new ArrayList<String>();
					nameToValues.put(name, values);
				} else {
					repeated = true;
				}
				values.add(parameter.getValue());
			}
		}
		if (repeated) {
			Collection<OAuth.Parameter> rejected = new ArrayList<OAuth.Parameter>();
			for (Map.Entry<String, Collection<String>> p : nameToValues.entrySet()) {
				String name = p.getKey();
				Collection<String> values = p.getValue();
				if (values.size() > 1) {
					for (String value : values) {
						rejected.add(new OAuth.Parameter(name, value));
					}
				}
			}
			Logger.error("Rejecting Parameters: %s", rejected);
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.PARAMETER_REJECTED);
			problem.setParameter(OAuth.Problems.OAUTH_PARAMETERS_REJECTED, OAuth.formEncode(rejected));
			throw problem;
		}
	}

	/**
	 * Validate version.
	 *
	 * @param message the message
	 * @throws OAuthException the o auth exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void validateVersion(OAuthMessage message) throws OAuthException, IOException {
		Logger.info("Validate Version: %s", OAuth.OAUTH_VERSION);
		String versionString = message.getParameter(OAuth.OAUTH_VERSION);
		Logger.info("Version: %s", versionString);
		if (versionString != null) {
			double version = Double.parseDouble(versionString);
			Logger.info("Min Version: %s, Max Version: %s", minVersion, maxVersion);
			if (version < minVersion || maxVersion < version) {
				Logger.error("Invalid Version: %s", version);
				OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.VERSION_REJECTED);
				problem.setParameter(OAuth.Problems.OAUTH_ACCEPTABLE_VERSIONS, minVersion + "-" + maxVersion);
				throw problem;
			}
		}
	}

	/**
	 * Validate timestamp and nonce.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException the o auth problem exception
	 */
	protected void validateTimestampAndNonce(OAuthMessage message) throws IOException, OAuthProblemException {
		Logger.info("Validate Timestamp: %s", message);
		message.requireParameters(OAuth.OAUTH_TIMESTAMP, OAuth.OAUTH_NONCE);
		long timestamp = Long.parseLong(message.getParameter(OAuth.OAUTH_TIMESTAMP));
		long now = currentTimeMsec();
		validateTimestamp(message, timestamp, now);
		validateNonce(message, timestamp, now);
	}

	/**
	 * Validate timestamp.
	 *
	 * @param message the message
	 * @param timestamp the timestamp
	 * @param currentTimeMsec the current time msec
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException the o auth problem exception
	 */
	protected void validateTimestamp(OAuthMessage message, long timestamp, long currentTimeMsec) throws IOException, OAuthProblemException {
		long min = (currentTimeMsec - maxTimestampAgeMsec + 500) / 1000L;
		long max = (currentTimeMsec + maxTimestampAgeMsec + 500) / 1000L;
		Logger.info("Validate Timestamp: %s - Acceptable Timestamps: %s", timestamp, min + "-" + max);
		if (timestamp < min || max < timestamp) {
			Logger.error("Invalid Timestamp: %s - Acceptable Timestamps: %s", timestamp, min + "-" + max);
			OAuthProblemException problem = new OAuthProblemException(OAuth.Problems.TIMESTAMP_REFUSED);
			problem.setParameter(OAuth.Problems.OAUTH_ACCEPTABLE_TIMESTAMPS, min + "-" + max);
			throw problem;
		}
	}

	/**
	 * Validate nonce.
	 *
	 * @param message the message
	 * @param timestamp the timestamp
	 * @param currentTimeMsec the current time msec
	 * @return the date
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws OAuthProblemException the o auth problem exception
	 */
	protected Date validateNonce(OAuthMessage message, long timestamp, long currentTimeMsec) throws IOException, OAuthProblemException {
		UsedNonce nonce = new UsedNonce(timestamp, message.getParameter(OAuth.OAUTH_NONCE), message.getConsumerKey(), message.getToken());
		/*
		 * The OAuth standard requires the token to be omitted from the stored
		 * nonce. But I include it, to harmonize with a Consumer that generates
		 * nonces using several independent computers, each with its own token.
		 */
		boolean valid = false;
		synchronized (usedNonces) {
			valid = usedNonces.add(nonce);
		}
		if (!valid) {
			Logger.error(OAuth.Problems.NONCE_USED);
			throw new OAuthProblemException(OAuth.Problems.NONCE_USED);
		}
		return removeOldNonces(currentTimeMsec);
	}

	/**
	 * Validate signature.
	 *
	 * @param message the message
	 * @param accessor the accessor
	 * @throws OAuthException the o auth exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws URISyntaxException the uRI syntax exception
	 */
	protected void validateSignature(OAuthMessage message, OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException {
		Logger.info("Validate Signature - Required Parameters: %s, %s, %s", OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE);
		message.requireParameters(OAuth.OAUTH_CONSUMER_KEY, OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE);
		OAuthSignatureMethod.newSigner(message, accessor).validate(message);
	}

	/**
	 * Current time msec.
	 *
	 * @return the long
	 */
	protected long currentTimeMsec() {
		return System.currentTimeMillis();
	}

	/**
	 * The Class UsedNonce.
	 */
	private static class UsedNonce implements Comparable<UsedNonce> {
		
		/**
		 * Instantiates a new used nonce.
		 *
		 * @param timestamp the timestamp
		 * @param nonceEtc the nonce etc
		 */
		UsedNonce(long timestamp, String... nonceEtc) {
			StringBuilder key = new StringBuilder(String.format("%20d", Long.valueOf(timestamp)));
			// The blank padding ensures that timestamps are compared as
			// numbers.
			for (String etc : nonceEtc) {
				key.append("&").append(etc == null ? " " : OAuth.percentEncode(etc));
				// A null value is different from "" or any other String.
			}
			sortKey = key.toString();
		}

		/** The sort key. */
		private final String sortKey;

		/**
		 * Gets the timestamp.
		 *
		 * @return the timestamp
		 */
		long getTimestamp() {
			int end = sortKey.indexOf("&");
			if (end < 0) {
				end = sortKey.length();
			}
			return Long.parseLong(sortKey.substring(0, end).trim());
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UsedNonce that) {
			return (that == null) ? 1 : sortKey.compareTo(that.sortKey);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return sortKey.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object that) {
			if (that == null) {
				return false;
			}
			if (that == this) {
				return true;
			}
			if (that.getClass() != getClass()) {
				return false;
			}
			return sortKey.equals(((UsedNonce) that).sortKey);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return sortKey;
		}
	}
}