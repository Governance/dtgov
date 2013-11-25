/**
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
package org.overlord.dtgov.jbpm.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.api.DeploymentUnit;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.AbstractDeploymentService;
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.audit.ServicesAwareAuditEventBuilder;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Sramp
public class SrampKModuleDeploymentService extends AbstractDeploymentService {

    private static Logger logger = LoggerFactory.getLogger(SrampKModuleDeploymentService.class);
    
    private static final String DEFAULT_KBASE_NAME = "SRAMPPackage"; //$NON-NLS-1$

    @Inject
    private BeanManager beanManager;
    @Inject
    private EntityManagerFactory emf;
    @Inject
    private IdentityProvider identityProvider;
    @Inject
    private BPMN2DataService bpmn2Service;

    @Override
    public void deploy(DeploymentUnit unit) {
        super.deploy(unit);
        if (!(unit instanceof KModuleDeploymentUnit)) {
            throw new IllegalArgumentException("Invalid deployment unit provided - " + unit.getClass().getName()); //$NON-NLS-1$
        }
        KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
        DeployedUnitImpl deployedUnit = new DeployedUnitImpl(unit);
        
        KieSrampUtil kieSrampUtil = new KieSrampUtil();
        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion());
        KieContainer kieContainer;
		try {
			kieContainer = kieSrampUtil.getKieContainer(releaseId);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot connect to s-ramp " + e.getMessage()); //$NON-NLS-1$
		}

        String kbaseName = kmoduleUnit.getKbaseName();
        if (StringUtils.isEmpty(kbaseName)) {
            KieBaseModel defaultKBaseModel = ((KieContainerImpl)kieContainer).getKieProject().getDefaultKieBaseModel();
            if (defaultKBaseModel != null) {
                kbaseName = defaultKBaseModel.getName();
            } else {
                kbaseName = DEFAULT_KBASE_NAME;
            }
        }
        InternalKieModule module = (InternalKieModule) ((KieContainerImpl)kieContainer).getKieModuleForKBase(kbaseName);
        if (module == null) {
            throw new IllegalStateException("Cannot find kbase with name " + kbaseName); //$NON-NLS-1$
        }

        Map<String, String> formsData = new HashMap<String, String>();
        Collection<String> files = module.getFileNames();
        for (String fileName : files) {
            if(fileName.matches(".+bpmn[2]?$")) { //$NON-NLS-1$
                ProcessDesc process;
                try {
                    String processString = new String(module.getBytes(fileName), "UTF-8"); //$NON-NLS-1$
                    process = bpmn2Service.findProcessId(processString, kieContainer.getClassLoader());
                    process.setEncodedProcessSource(Base64.encodeBase64String(processString.getBytes()));
                    process.setDeploymentId(unit.getIdentifier());
                    process.setForms(formsData);
                    deployedUnit.addAssetLocation(process.getId(), process);
                } catch (UnsupportedEncodingException e) {
                    logger.warn("Unable to load content for file '{}' : {}", fileName, e); //$NON-NLS-1$
                }
            } else if (fileName.matches(".+ftl$")) { //$NON-NLS-1$
                try {
                    String formContent = new String(module.getBytes(fileName), "UTF-8"); //$NON-NLS-1$
                    Pattern regex = Pattern.compile("(.{0}|.*/)([^/]*?)\\.ftl"); //$NON-NLS-1$
                    Matcher m = regex.matcher(fileName);
                    String key = fileName;
                    while (m.find()) {
                        key = m.group(2);
                    }
                    formsData.put(key, formContent);
                } catch (UnsupportedEncodingException e) {
                    logger.warn("Unable to load content for form '{}' : {}", fileName, e); //$NON-NLS-1$
                }
            } else if (fileName.matches(".+form$")) { //$NON-NLS-1$
                try {
                    String formContent = new String(module.getBytes(fileName), "UTF-8"); //$NON-NLS-1$
                    Pattern regex = Pattern.compile("(.{0}|.*/)([^/]*?)\\.form"); //$NON-NLS-1$
                    Matcher m = regex.matcher(fileName);
                    String key = fileName;
                    while (m.find()) {
                        key = m.group(2);
                    }
                    formsData.put(key+".form", formContent); //$NON-NLS-1$
                } catch (UnsupportedEncodingException e) {
                    logger.warn("Unable to load content for form '{}' : {}", fileName, e); //$NON-NLS-1$
                }
            }
        }

        KieBase kbase = kieContainer.getKieBase(kbaseName);        

        AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(emf);
        ServicesAwareAuditEventBuilder auditEventBuilder = new ServicesAwareAuditEventBuilder();
        auditEventBuilder.setIdentityProvider(identityProvider);
        auditEventBuilder.setDeploymentUnitId(unit.getIdentifier());
        auditLogger.setBuilder(auditEventBuilder);

        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.getDefault()
                .entityManagerFactory(emf)
                .knowledgeBase(kbase)
                .classLoader(kieContainer.getClassLoader());
        if (beanManager != null) {
            builder.registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, auditLogger, kieContainer,
                    kmoduleUnit.getKsessionName()));
        }
        commonDeploy(unit, deployedUnit, builder.get());
    }

}
