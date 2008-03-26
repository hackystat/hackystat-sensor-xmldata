<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <XmlData>
    <Entries>
      <xsl:apply-templates select="build"/>
    </Entries>  
  </XmlData>
</xsl:template>

<xsl:template match="build">
 <xsl:choose>
    <xsl:when test="@error">
      <Entry SensorDataType="Build" Tool="Ant" Target="{target[last()]/@name}" Result="Failure" Resource="unknown" Type="LoggerBuild"/>
    </xsl:when>
    <xsl:otherwise>
      <Entry SensorDataType="Build" Tool="Ant" Target="{target[last()]/@name}" Result="Success" Resource="unknown" Type="LoggerBuild"/>
    </xsl:otherwise>
  </xsl:choose>
  
</xsl:template>
</xsl:stylesheet>


