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
package org.overlord.sramp.governance;

import java.util.HashMap;
import java.util.Map;

public class Query {

    private String srampQuery;
    private String workflowId;
    private Map<String, String> parameters;

    public Query(String srampQuery, String workflowId, Map<String, String> parameters) {
        super();
        this.workflowId = workflowId;
        this.setSrampQuery(srampQuery);
        setParameters(parameters);
    }

    public Query(String srampQuery, String workflowId) {
        super();
        this.workflowId = workflowId;
        this.setSrampQuery(srampQuery);
        this.parameters = new HashMap<String, String>();
    }

    public void setSrampQuery(String srampQuery) {
        this.srampQuery = srampQuery;
    }

    public String getSrampQuery() {
        return srampQuery;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public Map<String,Object> getParsedParameters() {
        Map<String, Object> params = new HashMap<String, Object>();
        if (parameters != null) {
            params.putAll(parameters);
        }
        return params;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Query addParameter(String key, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, String>();
        }
        this.parameters.put(key, value);
        return this;
    }
    @Override
    public String toString() {
        return "srampQuery=" + srampQuery + "\nworkflowId=" + workflowId + "\nparameters=" + getParsedParameters(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public Query replaceParametersValues(String toFind, String replace) {
        if (this.parameters != null && !this.parameters.isEmpty()) {
            for (String key : this.parameters.keySet()) {
                this.parameters.put(key, this.parameters.get(key).replace(toFind, replace));
            }
        }
        return this;
    }

}
