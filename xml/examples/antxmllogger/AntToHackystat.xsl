<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <xmldata>
    <xsl:apply-templates select="build"/>  
  </xmldata>
</xsl:template>

<xsl:template match="build">
 <xsl:choose>
    <xsl:when test="@error">
      <entry sdt="Build" tool="Ant" Target="{target[last()]/@name}" Result="Failure"/>
    </xsl:when>
    <xsl:otherwise>
      <entry sdt="Build" tool="Ant" Target="{target[last()]/@name}" Result="Success"/>
    </xsl:otherwise>
  </xsl:choose>
  
</xsl:template>
</xsl:stylesheet>

<!-- 
  build/target[last()] selects the last <target> in <build>
  build[@error] selects a build node that has an error attribute.
   


-->

