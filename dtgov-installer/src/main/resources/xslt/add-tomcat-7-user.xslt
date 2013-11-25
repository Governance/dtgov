<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:param name="username" />
  <xsl:param name="password" />
  <xsl:param name="roles" />

  <xsl:output xmlns:xalan="http://xml.apache.org/xalan" method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="2" />

  <xsl:template match="/tomcat-users/user[@username='admin']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>
    <user>
      <xsl:attribute name="username">
        <xsl:value-of select="$username"></xsl:value-of>
      </xsl:attribute>
      <xsl:attribute name="password">
        <xsl:value-of select="$password"></xsl:value-of>
      </xsl:attribute>
      <xsl:attribute name="roles">
        <xsl:value-of select="$roles"></xsl:value-of>
      </xsl:attribute>
    </user>
  </xsl:template>

  <!-- Copy everything else. -->
  <xsl:template match="@*|node()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
