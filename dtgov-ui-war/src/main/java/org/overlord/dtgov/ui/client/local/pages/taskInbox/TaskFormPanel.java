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
package org.overlord.dtgov.ui.client.local.pages.taskInbox;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

/**
 * Wraps the task form.
 * @author eric.wittmann@redhat.com
 */
public class TaskFormPanel extends HTML {

    /**
     * Constructor.
     */
    public TaskFormPanel() {
    }

    /**
     * Sets the form data on the task form.  This will try to find form fields on the task form
     * that correspond to the keys in the task data map.  For each map entry, a form field is
     * located and its value set.
     * @param taskData
     */
    public void setData(Map<String, String> taskData) {
        for (Map.Entry<String, String> entry : taskData.entrySet()) {
            setTaskField(getElement(), entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets a single task field value in the task form.
     * @param formRoot
     * @param name
     * @param value
     */
    protected native final void setTaskField(Element formRoot, String name, String value) /*-{
        // Handle input type='text'
        $wnd.jQuery(formRoot).find('input[type=text][name="'+name+'"]').val(value);
        // Handle input type='hidden'
        $wnd.jQuery(formRoot).find('input[type=hidden][name="'+name+'"]').val(value);
        // Handle textarea
        $wnd.jQuery(formRoot).find('textarea[name="'+name+'"]').val(value);
        // Handle input type='checkbox'
        $wnd.jQuery(formRoot).find('input[type=checkbox][name="'+name+'"]').prop('checked', value == 'true');
        // Handle input type='radio'
        $wnd.jQuery(formRoot).find('input[type=radio][name="'+name+'"]').prop('checked', 'false');
        $wnd.jQuery(formRoot).find('input[type=radio][name="'+name+'"][value="'+value+'"]').prop('checked', 'true');
        // Handle select
        $wnd.jQuery(formRoot).find('select[name='+name+']').val(value);

        // Handle read-only fields (span, label, etc)
        $wnd.jQuery(formRoot).find('div[data-name='+name+']').text(value);
        $wnd.jQuery(formRoot).find('span[data-name='+name+']').text(value);
        $wnd.jQuery(formRoot).find('label[data-name='+name+']').text(value);
    }-*/;

    /**
     * Gets the data currently stored in the task form.
     */
    public Map<String, String> getData() {
        HashMap<String, String> data = new HashMap<String, String>();
        getData(getElement(), data);
        return data;
    }

    /**
     * Gets the data from the task form.
     * @param formRoot
     * @param data
     */
    @SuppressWarnings("rawtypes")
    protected native final void getData(final Element formRoot, final HashMap data) /*-{
        var dis = this;
        var valFunction = function(idx, obj) {
            var input = $wnd.jQuery(obj);
            var name = input.attr('name');
            var value = input.val();
            if (name) {
                dis.@org.overlord.dtgov.ui.client.local.pages.taskInbox.TaskFormPanel::addToMap(Ljava/util/HashMap;Ljava/lang/String;Ljava/lang/String;)(data, name, value);
            }
        };
        $wnd.jQuery(formRoot).find('input[type=text]').each(valFunction);
        $wnd.jQuery(formRoot).find('input[type=hidden]').each(valFunction);
        $wnd.jQuery(formRoot).find('textarea').each(valFunction);
        $wnd.jQuery(formRoot).find('select').each(valFunction);
        $wnd.jQuery(formRoot).find('input[type=checkbox]').each(function(idx, obj) {
            var input = $wnd.jQuery(obj);
            var name = input.attr('name');
            var value = '' + input.prop('checked');
            if (name) {
                dis.@org.overlord.dtgov.ui.client.local.pages.taskInbox.TaskFormPanel::addToMap(Ljava/util/HashMap;Ljava/lang/String;Ljava/lang/String;)(data, name, value);
            }
        });
        $wnd.jQuery(formRoot).find('input[type=radio]:checked').each(valFunction);
    }-*/;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final void addToMap(final HashMap data, String name, String value) {
        data.put(name, value);
    }

}
