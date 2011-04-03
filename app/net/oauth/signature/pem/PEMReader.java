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
package net.oauth.signature.pem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.oauth.signature.OAuthSignatureMethod;

/**
 * This class convert PEM into byte array. The begin marker is saved and it can
 * be used to determine the type of the PEM file.
 * 
 * @author zhang
 */
public class PEMReader {

	// Begin markers for all supported PEM files
	/** The Constant PRIVATE_PKCS1_MARKER. */
	public static final String PRIVATE_PKCS1_MARKER = "-----BEGIN RSA PRIVATE KEY-----";
	
	/** The Constant PRIVATE_PKCS8_MARKER. */
	public static final String PRIVATE_PKCS8_MARKER = "-----BEGIN PRIVATE KEY-----";
	
	/** The Constant CERTIFICATE_X509_MARKER. */
	public static final String CERTIFICATE_X509_MARKER = "-----BEGIN CERTIFICATE-----";
	
	/** The Constant PUBLIC_X509_MARKER. */
	public static final String PUBLIC_X509_MARKER = "-----BEGIN PUBLIC KEY-----";

	/** The Constant BEGIN_MARKER. */
	private static final String BEGIN_MARKER = "-----BEGIN ";

	/** The stream. */
	private InputStream stream;
	
	/** The der bytes. */
	private byte[] derBytes;
	
	/** The begin marker. */
	private String beginMarker;

	/**
	 * Instantiates a new pEM reader.
	 *
	 * @param inStream the in stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PEMReader(InputStream inStream) throws IOException {
		stream = inStream;
		readFile();
	}

	/**
	 * Instantiates a new pEM reader.
	 *
	 * @param buffer the buffer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PEMReader(byte[] buffer) throws IOException {
		this(new ByteArrayInputStream(buffer));
	}

	/**
	 * Instantiates a new pEM reader.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public PEMReader(String fileName) throws IOException {
		this(new FileInputStream(fileName));
	}

	/**
	 * Gets the der bytes.
	 *
	 * @return the der bytes
	 */
	public byte[] getDerBytes() {
		return derBytes;
	}

	/**
	 * Gets the begin marker.
	 *
	 * @return the begin marker
	 */
	public String getBeginMarker() {
		return beginMarker;
	}

	/**
	 * Read the PEM file and save the DER encoded octet stream and begin marker.
	 * 
	 * @throws IOException
	 */
	protected void readFile() throws IOException {

		String line;
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		try {
			while ((line = reader.readLine()) != null) {
				if (line.indexOf(BEGIN_MARKER) != -1) {
					beginMarker = line.trim();
					String endMarker = beginMarker.replace("BEGIN", "END");
					derBytes = readBytes(reader, endMarker);
					return;
				}
			}
			throw new IOException("Invalid PEM file: no begin marker");
		} finally {
			reader.close();
		}
	}

	/**
	 * Read the lines between BEGIN and END marker and convert the Base64
	 * encoded content into binary byte array.
	 * 
	 * @return DER encoded octet stream
	 * @throws IOException
	 */
	private byte[] readBytes(BufferedReader reader, String endMarker)
			throws IOException {
		String line = null;
		StringBuffer buf = new StringBuffer();

		while ((line = reader.readLine()) != null) {
			if (line.indexOf(endMarker) != -1) {

				return OAuthSignatureMethod.decodeBase64(buf.toString());
			}

			buf.append(line.trim());
		}

		throw new IOException("Invalid PEM file: No end marker");
	}
}
