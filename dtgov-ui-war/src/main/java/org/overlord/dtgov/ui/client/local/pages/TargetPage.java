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
package org.overlord.dtgov.ui.client.local.pages;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.UnorderedListPanel;
import org.overlord.dtgov.ui.client.local.ClientMessages;
import org.overlord.dtgov.ui.client.local.beans.UiConfiguration;
import org.overlord.dtgov.ui.client.local.pages.targets.ClassifiersTable;
import org.overlord.dtgov.ui.client.local.pages.targets.TargetTypeListBox;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.AbstractTargetPanel;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.CliTargetPanel;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.CopyTargetPanel;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.CustomTargetPanel;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.MavenTargetPanel;
import org.overlord.dtgov.ui.client.local.pages.targets.panel.RhqTargetPanel;
import org.overlord.dtgov.ui.client.local.services.ConfigurationService;
import org.overlord.dtgov.ui.client.local.services.NotificationService;
import org.overlord.dtgov.ui.client.local.services.TargetsRpcService;
import org.overlord.dtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.dtgov.ui.client.local.util.DOMUtil;
import org.overlord.dtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetBean;
import org.overlord.dtgov.ui.client.shared.beans.TargetType;
import org.overlord.dtgov.ui.client.shared.beans.ValidationError;
import org.overlord.dtgov.ui.client.shared.exceptions.DtgovFormValidationException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Manage Target initial page. Used to edit/create targets.
 *
 * @author David Virgil Naranjo
 */
@Templated("/org/overlord/dtgov/ui/client/local/site/target.html#page")
@Page(path = "target")
@Dependent
public class TargetPage extends AbstractPage {



    // Breadcrumbs
    /** The _back to dashboard. */
    @Inject
    @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> _backToDashboard;

    // Breadcrumbs
    /** The _back to queries. */
    @Inject
    @DataField("back-to-targets")
    private TransitionAnchor<TargetsPage> _backToTargets;

    /** The _config service. */
    @Inject
    private ConfigurationService _configService;

    /** The _description box. */
    @Inject
    @DataField("form-target-description-input")
    private TextArea _descriptionBox;

    /** The _form validation error div. */
    @Inject
    @DataField("form-validation-div")
    private HtmlSnippet _formValidationErrorDiv;

    /** The _i18n. */
    @Inject
    private ClientMessages _i18n;

    /** The _notification service. */
    @Inject
    private NotificationService _notificationService;

    /** The _page content. */
    private Element _pageContent;

    /** The _query name box. */
    @Inject
    @DataField("form-target-name-input")
    private TextBox _targetNameBox;

    /** The _reset button. */
    @Inject
    @DataField("btn-reset")
    private Button _resetButton;

    /** The _submit button. */
    @Inject
    @DataField("btn-save")
    private Button _submitButton;

    /** The _uuid. */
    @PageState("uuid")
    private String _uuid;

    /** The _validation_errors. */
    @Inject
    @DataField("form-validation-errors")
    private UnorderedListPanel _validation_errors;

    /** The _workflow query loading. */
    @Inject
    @DataField("target-loading-spinner")
    private HtmlSnippet _targetLoading;

    /** The _workflow query service. */
    @Inject
    private TargetsRpcService _targetService;

    /** The _workflow. */
    @Inject
    @DataField("form-target-type-input")
    private TargetTypeListBox _targetType;

    @Inject
    Instance<MavenTargetPanel> _addMavenForm;

    @Inject
    Instance<CliTargetPanel> _addCliForm;

    @Inject
    Instance<RhqTargetPanel> _addRhqForm;

    @Inject
    Instance<CopyTargetPanel> _addCopyForm;

    @Inject
    Instance<CustomTargetPanel> _addCustomForm;

    private AbstractTargetPanel _targetPanel;

    @Inject
    @DataField("classifiers-table")
    private ClassifiersTable _classifiersTable;

    @Inject
    @DataField("target-panel")
    private FlowPanel _panel;

    /** The _add property. */
    @Inject
    @DataField("btn-add-classifier")
    private Button _addClassifier;

    /**
     * Creates the target bean.
     *
     * @return the target bean
     */
    private TargetBean createTargetBean() {
        TargetBean target = null;
        if (_targetType != null && _targetType.getValue() != null && !_targetType.getValue().equals("")) { //$NON-NLS-1$
            target = _targetPanel.getTargetBean();
        } else {
            target = new TargetBean();
        }
        if (target != null) {
            target.setUuid(this._uuid);
            target.setName(this._targetNameBox.getValue());
            target.setDescription(this._descriptionBox.getValue());
            target.setClassifiers(_classifiersTable.getValue());
        }

        return target;
    }

    /**
     * Inits the page.
     */
    private void init() {
        if (_uuid != null && !_uuid.isEmpty()) {
            _pageContent.addClassName("hide"); //$NON-NLS-1$
            _targetLoading.getElement().removeClassName("hide"); //$NON-NLS-1$

            _targetService.get(_uuid, new IRpcServiceInvocationHandler<TargetBean>() {
                @Override
                public void onError(Throwable error) {
                    _notificationService.sendErrorNotification(_i18n.format("target.error-loading"), error); //$NON-NLS-1$
                    _targetLoading.getElement().addClassName("hide"); //$NON-NLS-1$
                }

                @Override
                public void onReturn(TargetBean data) {
                    updateContent(data);
                    _targetLoading.getElement().addClassName("hide"); //$NON-NLS-1$
                }
            });
        }
    }

    /* (non-Javadoc)
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {

        if (_uuid != null && !_uuid.isEmpty()) {
            init();
        }

    }

    /**
     * Method execuded on post construct. Initialize the components.
     */
    @PostConstruct
    protected void onPostConstruct() {
        _formValidationErrorDiv.getElement().addClassName("hide"); //$NON-NLS-1$
        _pageContent = DOMUtil.findElementById(getElement(), "target-content-wrapper"); //$NON-NLS-1$

        this._targetType.clear();
        UiConfiguration uiConfig = _configService.getUiConfig();
        Map<String, String> typesMap = uiConfig.getTargetKeyTypes();
        for (String key : typesMap.keySet()) {
            _targetType.addItem(key, typesMap.get(key));
        }
        this._targetType.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                refreshTargetForm(null);
            }
        });
        _descriptionBox.setVisibleLines(3);

    }

    /**
     * Refresh target form using a target bean as income.
     *
     * @param data
     *            the data
     */
    private void refreshTargetForm(TargetBean data) {
        String value = _targetType.getValue();
        if (value != null && !value.equals("")) { //$NON-NLS-1$
            TargetType type = TargetType.value(value);
            _panel.clear();
            if (type != null) {
                switch (type) {
                case MAVEN:
                    _targetPanel = this._addMavenForm.get();
                    break;
                case RHQ:
                    _targetPanel = this._addRhqForm.get();
                    break;

                case CLI:
                    _targetPanel = this._addCliForm.get();
                    break;

                case COPY:
                    _targetPanel = this._addCopyForm.get();
                    break;

                case CUSTOM:
                    _targetPanel = this._addCustomForm.get();
                    break;
                }

                if (_targetPanel != null) {
                    _panel.add(_targetPanel);
                    if (data != null) {
                        _targetPanel.initialize(data);
                    }


                }
            }
        }
    }

    /**
     * Update the content of the page using a target bean as param.
     *
     * @param data
     *            the data
     */
    protected void updateContent(TargetBean data) {
        _pageContent.removeClassName("hide"); //$NON-NLS-1$

        _targetNameBox.setValue(data.getName());

        _descriptionBox.setValue(data.getDescription());
        _targetType.setValue(data.getType().getValue());
        /*
         * for (int i = 0; i < _targetType.getItemCount(); i++) { if
         * (_targetType.getValue(i) != null &&
         * _targetType.getValue(i).equals(data.getType().getValue())) {
         * _targetType.setSelectedIndex(i); break; } }
         */
        _classifiersTable.setValue(data.getClassifiers());
        refreshTargetForm(data);
        if (_uuid == null || _uuid.equals("")) { //$NON-NLS-1$
            _resetButton.setVisible(false);
        }

    }

    /**
     * On submit click.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-save")
    public void onSubmitClick(ClickEvent event) {
        final NotificationBean notification = _notificationService.startProgressNotification(_i18n.format("target-submit.save"), //$NON-NLS-1$
                _i18n.format("target-submit.save-msg")); //$NON-NLS-1$

        final TargetBean target = this.createTargetBean();
        _validation_errors.clear();
        _targetService.save(target, new IRpcServiceInvocationHandler<String>() {
            @Override
            public void onError(Throwable error) {
                if (error instanceof DtgovFormValidationException) {
                    List<ValidationError> errors = ((DtgovFormValidationException) error).getErrors();
                    for (ValidationError err : errors) {
                        _validation_errors.add(new InlineLabel(_i18n.format(err.getErrorLabel())));
                    }

                    _formValidationErrorDiv.getElement().removeClassName("hide"); //$NON-NLS-1$
                    _notificationService.removeNotification(notification.getUuid());
                    Window.scrollTo(0, 0);
                } else {
                    _notificationService.completeProgressNotification(notification.getUuid(), _i18n.format("target-submit.error-saving"), //$NON-NLS-1$
                            error);
                }
            }

            @Override
            public void onReturn(String data) {
                _uuid = data;
                _formValidationErrorDiv.getElement().addClassName("hide"); //$NON-NLS-1$
                _notificationService.completeProgressNotification(notification.getUuid(), _i18n.format("target-submit.successfully-saved"), //$NON-NLS-1$
                        _i18n.format("target-submit.successfully-saved-message", target.getName())); //$NON-NLS-1$
            }
        });
    }

    /**
     * On reset.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-reset")
    public void reset(ClickEvent event) {
        _formValidationErrorDiv.getElement().addClassName("hide"); //$NON-NLS-1$
        if (_uuid != null && !_uuid.isEmpty()) {
            init();
        } else {
            _descriptionBox.setText(""); //$NON-NLS-1$
            _targetNameBox.setText(""); //$NON-NLS-1$
            _targetType.setSelectedIndex(0);
            _panel.clear();
            _classifiersTable.clear();
        }

    }

    /**
     * Ond add property.
     *
     * @param event
     *            the event
     */
    @EventHandler("btn-add-classifier")
    public void ondAddClassifier(ClickEvent event) {
        _classifiersTable.addNewRow();
    }

    /**
     * Gets the back to dashboard.
     *
     * @return the back to dashboard
     */
    public TransitionAnchor<DashboardPage> getBackToDashboard() {
        return _backToDashboard;
    }

    /**
     * Sets the back to dashboard.
     *
     * @param backToDashboard
     *            the new back to dashboard
     */
    public void setBackToDashboard(TransitionAnchor<DashboardPage> backToDashboard) {
        this._backToDashboard = backToDashboard;
    }

    /**
     * Gets the back to targets.
     *
     * @return the back to targets
     */
    public TransitionAnchor<TargetsPage> getBackToTargets() {
        return _backToTargets;
    }

    /**
     * Sets the back to targets.
     *
     * @param backToTargets
     *            the new back to targets
     */
    public void setBackToTargets(TransitionAnchor<TargetsPage> backToTargets) {
        this._backToTargets = backToTargets;
    }

    /**
     * Gets the config service.
     *
     * @return the config service
     */
    public ConfigurationService getConfigService() {
        return _configService;
    }

    /**
     * Sets the config service.
     *
     * @param configService
     *            the new config service
     */
    public void setConfigService(ConfigurationService configService) {
        this._configService = configService;
    }

    /**
     * Gets the description box.
     *
     * @return the description box
     */
    public TextArea getDescriptionBox() {
        return _descriptionBox;
    }

    /**
     * Sets the description box.
     *
     * @param descriptionBox
     *            the new description box
     */
    public void setDescriptionBox(TextArea descriptionBox) {
        this._descriptionBox = descriptionBox;
    }

    /**
     * Gets the form validation error div.
     *
     * @return the form validation error div
     */
    public HtmlSnippet getFormValidationErrorDiv() {
        return _formValidationErrorDiv;
    }

    /**
     * Sets the form validation error div.
     *
     * @param formValidationErrorDiv
     *            the new form validation error div
     */
    public void setFormValidationErrorDiv(HtmlSnippet formValidationErrorDiv) {
        this._formValidationErrorDiv = formValidationErrorDiv;
    }

    /**
     * Gets the i18n.
     *
     * @return the i18n
     */
    public ClientMessages getI18n() {
        return _i18n;
    }

    /**
     * Sets the i18n.
     *
     * @param i18n
     *            the new i18n
     */
    public void setI18n(ClientMessages i18n) {
        this._i18n = i18n;
    }

    /**
     * Gets the notification service.
     *
     * @return the notification service
     */
    public NotificationService getNotificationService() {
        return _notificationService;
    }

    /**
     * Sets the notification service.
     *
     * @param notificationService
     *            the new notification service
     */
    public void setNotificationService(NotificationService notificationService) {
        this._notificationService = notificationService;
    }

    /**
     * Gets the page content.
     *
     * @return the page content
     */
    public Element getPageContent() {
        return _pageContent;
    }

    /**
     * Sets the page content.
     *
     * @param pageContent
     *            the new page content
     */
    public void setPageContent(Element pageContent) {
        this._pageContent = pageContent;
    }

    /**
     * Gets the target name box.
     *
     * @return the target name box
     */
    public TextBox getTargetNameBox() {
        return _targetNameBox;
    }

    /**
     * Sets the target name box.
     *
     * @param targetNameBox
     *            the new target name box
     */
    public void setTargetNameBox(TextBox targetNameBox) {
        this._targetNameBox = targetNameBox;
    }

    /**
     * Gets the reset button.
     *
     * @return the reset button
     */
    public Button getResetButton() {
        return _resetButton;
    }

    /**
     * Sets the reset button.
     *
     * @param resetButton
     *            the new reset button
     */
    public void setResetButton(Button resetButton) {
        this._resetButton = resetButton;
    }

    /**
     * Gets the submit button.
     *
     * @return the submit button
     */
    public Button getSubmitButton() {
        return _submitButton;
    }

    /**
     * Sets the submit button.
     *
     * @param submitButton
     *            the new submit button
     */
    public void setSubmitButton(Button submitButton) {
        this._submitButton = submitButton;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return _uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid
     *            the new uuid
     */
    public void setUuid(String uuid) {
        this._uuid = uuid;
    }

    /**
     * Gets the validation_errors.
     *
     * @return the validation_errors
     */
    public UnorderedListPanel getValidation_errors() {
        return _validation_errors;
    }

    /**
     * Sets the validation_errors.
     *
     * @param validation_errors
     *            the new validation_errors
     */
    public void setValidation_errors(UnorderedListPanel validation_errors) {
        this._validation_errors = validation_errors;
    }

    /**
     * Gets the target loading.
     *
     * @return the target loading
     */
    public HtmlSnippet getTargetLoading() {
        return _targetLoading;
    }

    /**
     * Sets the target loading.
     *
     * @param targetLoading
     *            the new target loading
     */
    public void setTargetLoading(HtmlSnippet targetLoading) {
        this._targetLoading = targetLoading;
    }

    /**
     * Gets the target service.
     *
     * @return the target service
     */
    public TargetsRpcService getTargetService() {
        return _targetService;
    }

    /**
     * Sets the target service.
     *
     * @param targetService
     *            the new target service
     */
    public void setTargetService(TargetsRpcService targetService) {
        this._targetService = targetService;
    }

    /**
     * Gets the target type.
     *
     * @return the target type
     */
    public TargetTypeListBox getTargetType() {
        return _targetType;
    }

    /**
     * Sets the target type.
     *
     * @param targetType
     *            the new target type
     */
    public void setTargetType(TargetTypeListBox targetType) {
        this._targetType = targetType;
    }

    /**
     * Gets the adds the maven form.
     *
     * @return the adds the maven form
     */
    public Instance<MavenTargetPanel> getAddMavenForm() {
        return _addMavenForm;
    }

    /**
     * Sets the adds the maven form.
     *
     * @param addMavenForm
     *            the new adds the maven form
     */
    public void setAddMavenForm(Instance<MavenTargetPanel> addMavenForm) {
        this._addMavenForm = addMavenForm;
    }

    /**
     * Gets the adds the cli form.
     *
     * @return the adds the cli form
     */
    public Instance<CliTargetPanel> getAddCliForm() {
        return _addCliForm;
    }

    /**
     * Sets the adds the cli form.
     *
     * @param addCliForm
     *            the new adds the cli form
     */
    public void setAddCliForm(Instance<CliTargetPanel> addCliForm) {
        this._addCliForm = addCliForm;
    }

    /**
     * Gets the adds the rhq form.
     *
     * @return the adds the rhq form
     */
    public Instance<RhqTargetPanel> getAddRhqForm() {
        return _addRhqForm;
    }

    /**
     * Sets the adds the rhq form.
     *
     * @param addRhqForm
     *            the new adds the rhq form
     */
    public void setAddRhqForm(Instance<RhqTargetPanel> addRhqForm) {
        this._addRhqForm = addRhqForm;
    }

    /**
     * Gets the adds the copy form.
     *
     * @return the adds the copy form
     */
    public Instance<CopyTargetPanel> getAddCopyForm() {
        return _addCopyForm;
    }

    /**
     * Sets the adds the copy form.
     *
     * @param addCopyForm
     *            the new adds the copy form
     */
    public void setAddCopyForm(Instance<CopyTargetPanel> addCopyForm) {
        this._addCopyForm = addCopyForm;
    }

    /**
     * Gets the target panel.
     *
     * @return the target panel
     */
    public AbstractTargetPanel getTargetPanel() {
        return _targetPanel;
    }

    /**
     * Sets the target panel.
     *
     * @param targetPanel
     *            the new target panel
     */
    public void setTargetPanel(AbstractTargetPanel targetPanel) {
        this._targetPanel = targetPanel;
    }

    /**
     * Gets the classifiers table.
     *
     * @return the classifiers table
     */
    public ClassifiersTable getClassifiersTable() {
        return _classifiersTable;
    }

    /**
     * Sets the classifiers table.
     *
     * @param classifiersTable
     *            the new classifiers table
     */
    public void setClassifiersTable(ClassifiersTable classifiersTable) {
        this._classifiersTable = classifiersTable;
    }

    /**
     * Gets the panel.
     *
     * @return the panel
     */
    public FlowPanel getPanel() {
        return _panel;
    }

    /**
     * Sets the panel.
     *
     * @param panel
     *            the new panel
     */
    public void setPanel(FlowPanel panel) {
        this._panel = panel;
    }

    /**
     * Gets the adds the classifier.
     *
     * @return the adds the classifier
     */
    public Button getAddClassifier() {
        return _addClassifier;
    }

    /**
     * Sets the adds the classifier.
     *
     * @param addClassifier
     *            the new adds the classifier
     */
    public void setAddClassifier(Button addClassifier) {
        this._addClassifier = addClassifier;
    }

}
