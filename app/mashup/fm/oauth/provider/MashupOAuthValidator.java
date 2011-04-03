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
import java.net.URISyntaxException;

import models.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

// TODO: Auto-generated Javadoc
/**
 * The Interface MashupOAuthValidator.
 */
public interface MashupOAuthValidator {

	/**
	 * Validate message.
	 * 
	 * @param message
	 *            the message
	 * @param accessor
	 *            the accessor
	 * @throws OAuthException
	 *             the o auth exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	public void validateMessage(OAuthMessage message, OAuthAccessor accessor)
			throws OAuthException, IOException, URISyntaxException;

}
