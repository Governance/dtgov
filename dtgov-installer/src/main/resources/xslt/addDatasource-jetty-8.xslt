<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="xml" encoding="UTF-8"
      doctype-public="-//Jetty//Configure//EN"
      doctype-system="http://www.eclipse.org/jetty/configure.dtd"
   />

  <xsl:template match="/Configure/child::*[last()]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()" />
    </xsl:copy>

    <!-- Bitronix Config -->
    <xsl:text>
    
    
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> bitronix configuration                                      </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <Call name="getConfiguration" class="bitronix.tm.TransactionManagerServices"><xsl:text>
      </xsl:text><Set name="serverId">jetty-btm-node0</Set><xsl:text>
      </xsl:text><Set name="logPart1Filename"><SystemProperty name="jetty.home" default="." />/work/btm1.tlog</Set><xsl:text>
      </xsl:text><Set name="logPart2Filename"><SystemProperty name="jetty.home" default="." />/work/btm2.tlog</Set><xsl:text>
    </xsl:text></Call><xsl:text>
    </xsl:text><Call name="addLifeCycle"><xsl:text>
      </xsl:text><Arg><xsl:text>
        </xsl:text><New class="bitronix.tm.integration.jetty7.BTMLifeCycle"/><xsl:text>
      </xsl:text></Arg><xsl:text>
    </xsl:text></Call><xsl:text>
    </xsl:text><New id="ut" class="org.eclipse.jetty.plus.jndi.Resource"><xsl:text>
      </xsl:text><Arg>java:comp/UserTransaction</Arg><xsl:text>
      </xsl:text><Arg><xsl:text>
        </xsl:text><Call name="getTransactionManager" class="bitronix.tm.TransactionManagerServices" /><xsl:text>
      </xsl:text></Arg><xsl:text>
    </xsl:text></New>


    <!-- Datasource config -->
    <xsl:text>
    
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> Add the DTGov Datasource (jbpm/bitronix)                    </xsl:comment>
    <xsl:text>
    </xsl:text>
    <xsl:comment> =========================================================== </xsl:comment>
    <xsl:text>
    </xsl:text>
    <New id="jbpm" class="org.eclipse.jetty.plus.jndi.Resource"><xsl:text>
      </xsl:text><Arg></Arg><xsl:text>
      </xsl:text><Arg>java:comp/env/jdbc/jbpm</Arg><xsl:text>
      </xsl:text><Arg><xsl:text>
        </xsl:text><New class="bitronix.tm.resource.jdbc.PoolingDataSource"><xsl:text>
          </xsl:text><Set name="className">bitronix.tm.resource.jdbc.lrc.LrcXADataSource</Set><xsl:text>
          </xsl:text><Set name="minPoolSize">10</Set><xsl:text>
          </xsl:text><Set name="maxPoolSize">20</Set><xsl:text>
          </xsl:text><Set name="uniqueName">jdbc/jbpm</Set><xsl:text>
          </xsl:text><Get name="driverProperties"><xsl:text>
            </xsl:text><Put name="driverClassName">org.h2.Driver</Put><xsl:text>
            </xsl:text><Put name="url">jdbc:h2:file:overlord-data/jbpm</Put><xsl:text>
          </xsl:text></Get><xsl:text>
          </xsl:text><Call name="init" /><xsl:text>
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
