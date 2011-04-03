/*
 * Copyright 2009 John Kristian
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
 */
package net.oauth;

/**
 * Where to place OAuth parameters in an HTTP message. The alternatives are
 * summarized in <a href="http://oauth.net/documentation/spec">OAuth Core</a>
 * under <a href="http://oauth.net/core/1.0a#consumer_req_param">Consumer
 * Request Parameters</a>.
 */
public enum ParameterStyle {
    /**
     * Send parameters whose names begin with "oauth_" in an HTTP header, and
     * other parameters (whose names don't begin with "oauth_") in either the
     * message body or URL query string. The header formats are specified by
     * OAuth Core under <a href="http://oauth.net/core/1.0a#auth_header">OAuth
     * HTTP Authorization Scheme</a>.
     */
    AUTHORIZATION_HEADER,

    /**
     * Send all parameters in the message body, with a Content-Type of
     * application/x-www-form-urlencoded.
     */
    BODY,

    /** Send all parameters in the query string part of the URL. */
    QUERY_STRING;
}
