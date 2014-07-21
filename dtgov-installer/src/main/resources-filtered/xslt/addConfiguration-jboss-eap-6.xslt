<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:oc="urn:jboss:domain:overlord-configuration:1.0"
    exclude-result-prefixes="oc">

  <xsl:param name="workflow-user-password" />

  <xsl:output xmlns:xalan="http://xml.apache.org/xalan" method="xml" encoding="UTF-8" indent="yes"
    xalan:indent-amount="2" />


  <xsl:template match="/*[name()='server']/*[name()='profile']/oc:subsystem/oc:configurations/oc:configuration[@name = 'overlord']/oc:properties">
    <xsl:variable name="currentNS" select="namespace-uri(.)" />
    <xsl:element name="properties" namespace="{$currentNS}">
      <xsl:apply-templates select="./node()|./text()" />
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.dtgov.href</xsl:attribute>
        <xsl:attribute name="value">/dtgov-ui/</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.dtgov.label</xsl:attribute>
        <xsl:attribute name="value">Design Time</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.dtgov.primary-brand</xsl:attribute>
        <xsl:attribute name="value">JBoss Overlord</xsl:attribute>
      </xsl:element>
      <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">overlord.headerui.apps.dtgov.secondary-brand</xsl:attribute>
        <xsl:attribute name="value">Governance</xsl:attribute>
      </xsl:element>
    </xsl:element>
  </xsl:template>


  <xsl:template match="/*[name()='server']/*[name()='profile']/oc:subsystem/oc:configurations">
    <xsl:variable name="currentNS" select="namespace-uri(.)" />
    <xsl:element name="configurations" namespace="{$currentNS}">
      <xsl:apply-templates select="./node()|./text()" />
      <!-- Dtgov Config -->
      <xsl:element name="configuration" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">dtgov</xsl:attribute>
        <xsl:element name="properties" namespace="urn:jboss:domain:overlord-configuration:1.0">
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">sramp.repo.url</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/s-ramp-server</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.url</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/dtgov</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov.ui.url</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/dtgov-ui</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.query.interval</xsl:attribute>
            <xsl:attribute name="value">20000</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov.workflows.group</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov.workflows.name</xsl:attribute>
            <xsl:attribute name="value">dtgov-workflows</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov.workflows.package</xsl:attribute>
            <xsl:attribute name="value">SRAMPPackage</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov.workflows.version</xsl:attribute>
            <xsl:attribute name="value">${project.version}</xsl:attribute>
          </xsl:element>

          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.bpm.user</xsl:attribute>
            <xsl:attribute name="value">dtgovworkflow</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.bpm.password</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:text>${vault:</xsl:text>
              <xsl:value-of select="$workflow-user-password" />
              <xsl:text>}</xsl:text>
            </xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">sramp.repo.user</xsl:attribute>
            <xsl:attribute name="value">dtgovworkflow</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">sramp.repo.password</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:text>${vault:</xsl:text>
              <xsl:value-of select="$workflow-user-password" />
              <xsl:text>}</xsl:text>
            </xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.user</xsl:attribute>
            <xsl:attribute name="value">dtgovworkflow</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">governance.password</xsl:attribute>
            <xsl:attribute name="value">
              <xsl:text>${vault:</xsl:text>
              <xsl:value-of select="$workflow-user-password" />
              <xsl:text>}</xsl:text>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:element>
      <!-- Dtgov UI Config -->
      <xsl:element name="configuration" namespace="urn:jboss:domain:overlord-configuration:1.0">
        <xsl:attribute name="name">dtgov-ui</xsl:attribute>
        <xsl:element name="properties" namespace="urn:jboss:domain:overlord-configuration:1.0">
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.auth.saml.issuer</xsl:attribute>
            <xsl:attribute name="value">/dtgov-ui</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.endpoint</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/s-ramp-server</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.provider</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov.ui.server.services.sramp.SAMLBearerTokenAuthenticationProvider</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.issuer</xsl:attribute>
            <xsl:attribute name="value">${dtgov-ui.auth.saml.issuer}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.service</xsl:attribute>
            <xsl:attribute name="value">/s-ramp-server</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.sign-assertions</xsl:attribute>
            <xsl:attribute name="value">true</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.keystore</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.keystore-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore-password}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.key-alias</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.authentication.saml.key-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias-password}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.s-ramp.atom-api.validating</xsl:attribute>
            <xsl:attribute name="value">true</xsl:attribute>
          </xsl:element>

          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.endpoint</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/dtgov/rest/tasks</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-client.class</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov.ui.server.services.tasks.DtGovTaskApiClient</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.provider</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov.ui.server.services.tasks.SAMLBearerTokenAuthenticationProvider</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.issuer</xsl:attribute>
            <xsl:attribute name="value">${dtgov-ui.auth.saml.issuer}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.service</xsl:attribute>
            <xsl:attribute name="value">/dtgov</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.sign-assertions</xsl:attribute>
            <xsl:attribute name="value">true</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.keystore</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.keystore-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore-password}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.key-alias</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.task-api.authentication.saml.key-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias-password}</xsl:attribute>
          </xsl:element>

          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.endpoint</xsl:attribute>
            <xsl:attribute name="value">${overlord.baseUrl}/dtgov/rest/</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-client.class</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov.ui.server.services.dtgov.DtGovBaseApiClient</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.provider</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov.ui.server.services.dtgov.SAMLBearerTokenAuthenticationProvider</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.issuer</xsl:attribute>
            <xsl:attribute name="value">${dtgov-ui.auth.saml.issuer}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.service</xsl:attribute>
            <xsl:attribute name="value">/dtgov</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.sign-assertions</xsl:attribute>
            <xsl:attribute name="value">true</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.keystore</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.keystore-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-keystore-password}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.key-alias</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.dtgov-api.authentication.saml.key-password</xsl:attribute>
            <xsl:attribute name="value">${overlord.auth.saml-key-alias-password}</xsl:attribute>
          </xsl:element>

          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.switchyard</xsl:attribute>
            <xsl:attribute name="value">SwitchYard Application:ext/SwitchYardApplication</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.jar</xsl:attribute>
            <xsl:attribute name="value">Java Archive:ext/JavaArchive</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.kjar</xsl:attribute>
            <xsl:attribute name="value">Drools Knowledge Archive:ext/KieJarArchive</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.war</xsl:attribute>
            <xsl:attribute name="value">Java Web Application:ext/JavaWebApplication</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.ear</xsl:attribute>
            <xsl:attribute name="value">Java Enterprise Application:ext/JavaEnterpriseApplication</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.types.vdb</xsl:attribute>
            <xsl:attribute name="value">Teiid Virtual Database:ext/TeiidVdb</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.stage.dev</xsl:attribute>
            <xsl:attribute name="value">Development:http://www.jboss.org/overlord/deployment-status.owl#InDev</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.stage.qa</xsl:attribute>
            <xsl:attribute name="value">QA:http://www.jboss.org/overlord/deployment-status.owl#InQa</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.stage.stage</xsl:attribute>
            <xsl:attribute name="value">Staging:http://www.jboss.org/overlord/deployment-status.owl#InStage</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.stage.prod</xsl:attribute>
            <xsl:attribute name="value">Production:http://www.jboss.org/overlord/deployment-status.owl#InProd</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.all</xsl:attribute>
            <xsl:attribute name="value">http://www.jboss.org/overlord/deployment-status.owl#Lifecycle</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.classifiers.base</xsl:attribute>
            <xsl:attribute name="value">http://www.jboss.org/overlord/deployment-status.owl</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.workflow.property.deploymentUrl</xsl:attribute>
            <xsl:attribute name="value">DeploymentUrl:{governance.url}/rest/deploy/{target}/{uuid}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.workflow.property.updateMetadataUrl</xsl:attribute>
            <xsl:attribute name="value">UpdateMetaDataUrl:{governance.url}/rest/update/{name}/{value}/{uuid}</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.deployment-lifecycle.workflow.property.notificationUrl</xsl:attribute>
            <xsl:attribute name="value">NotificationUrl:{governance.url}/rest/notify/email/{group}/deployed/{target}/{uuid}</xsl:attribute>
          </xsl:element>

          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.workflows.group</xsl:attribute>
            <xsl:attribute name="value">org.overlord.dtgov</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.workflows.name</xsl:attribute>
            <xsl:attribute name="value">dtgov-workflows</xsl:attribute>
          </xsl:element>
          <xsl:element name="property" namespace="urn:jboss:domain:overlord-configuration:1.0">
            <xsl:attribute name="name">dtgov-ui.workflows.version</xsl:attribute>
            <xsl:attribute name="value">${project.version}</xsl:attribute>
          </xsl:element>


        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- Copy everything else. -->
  <xsl:template match="@*|node()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
