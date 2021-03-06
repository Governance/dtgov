/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.dtgov.taskclient.auth;

import org.apache.http.HttpRequest;
import org.overlord.dtgov.taskclient.TaskApiClient;

/**
 * Clients can supply their own authentication implementation by
 * implementing this interface.  Simply provide one of these when
 * constructing the {@link TaskApiClient}.
 *
 * @author eric.wittmann@redhat.com
 */
public interface AuthenticationProvider {

    /**
     * Called by the S-RAMP atom API client to add authentication to the
     * {@link HttpRequest}.
     * @param request
     */
    public void provideAuthentication(HttpRequest request);

}
