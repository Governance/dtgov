<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="xml" encoding="UTF-8"
      doctype-public="-//Jetty//Configure//EN"
      doctype-system="http://www.eclipse.org/jetty/configure.dtd"
   />

  <xsl:template match="/Configure/Set[@name = 'ThreadPool']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>

    <!-- Default Mail Session -->
    <xsl:text>
    
    
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> Add the default mail session                                </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <New id="mail" class="org.eclipse.jetty.plus.jndi.Resource"><xsl:text>
      </xsl:text><Arg></Arg><xsl:text>
      </xsl:text><Arg>java:comp/env/mail/Default</Arg><xsl:text>
      </xsl:text><Arg><xsl:text>
        </xsl:text><New class="org.eclipse.jetty.jndi.factories.MailSessionReference"><xsl:text>
          </xsl:text><Set name="properties"><xsl:text>
            </xsl:text><New class="java.util.Properties"><xsl:text>
              </xsl:text><Put name="mail.transport.protocol">smtp</Put><xsl:text>
              </xsl:text><Put name="mail.smtp.host">localhost</Put><xsl:text>
              </xsl:text><Put name="mail.smtp.port">25</Put><xsl:text>
            </xsl:text></New><xsl:text>
          </xsl:text></Set><xsl:text>
        </xsl:text></New><xsl:text>
      </xsl:text></Arg><xsl:text>
    </xsl:text></New>

  </xsl:template>

  <!-- Copy everything else. -->
  <xsl:template match="@*|node()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
