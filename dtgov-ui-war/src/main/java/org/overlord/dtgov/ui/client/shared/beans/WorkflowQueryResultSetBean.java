/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.dtgov.ui.client.shared.beans;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;


/**
 * Models the set of Workflow Query  summary objects.
 *
 * @author dvirgiln@redhat.com
 */
@Portable
public class WorkflowQueryResultSetBean implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4302100421983844252L;

    /** The _queries. */
    private List<WorkflowQuerySummaryBean> _queries;
    
    /** The _total results. */
    protected long _totalResults;
    
    /** The _items per page. */
    private int _itemsPerPage;
    
    /** The _start index. */
    private int _startIndex;
    
    /**
     * Instantiates a new workflow query result set bean.
     */
    public WorkflowQueryResultSetBean(){
        
    }

    /**
     * Gets the queries.
     * 
     * @return the queries
     */
    public List<WorkflowQuerySummaryBean> getQueries() {
        return _queries;
    }

    /**
     * Sets the queries.
     * 
     * @param queries
     *            the new queries
     */
    public void setQueries(List<WorkflowQuerySummaryBean> queries) {
        this._queries = queries;
    }



    public long get_totalResults() {
        return _totalResults;
    }

    public void set_totalResults(long _totalResults) {
        this._totalResults = _totalResults;
    }

    /**
     * Gets the items per page.
     * 
     * @return the items per page
     */
    public int getItemsPerPage() {
        return _itemsPerPage;
    }

    /**
     * Sets the items per page.
     * 
     * @param itemsPerPage
     *            the new items per page
     */
    public void setItemsPerPage(int itemsPerPage) {
        this._itemsPerPage = itemsPerPage;
    }

    /**
     * Gets the start index.
     * 
     * @return the start index
     */
    public int getStartIndex() {
        return _startIndex;
    }

    /**
     * Sets the start index.
     * 
     * @param startIndex
     *            the new start index
     */
    public void setStartIndex(int startIndex) {
        this._startIndex = startIndex;
    }


    
    
}
