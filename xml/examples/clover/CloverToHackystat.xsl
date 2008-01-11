<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <xmldata>
    <xsl:apply-templates select="coverage/project/package/file"/>  
  </xmldata>
</xsl:template>

<xsl:template match="file">
  <xsl:variable name="uncovered" select="class/metrics/@statements - class/metrics/@coveredstatements" />
  <entry sdt="Coverage" tool="Clover" granularity="statement" fileName="{@name}" covered="{class/metrics/@coveredstatements}" uncovered="{$uncovered}"/>
</xsl:template>
</xsl:stylesheet>
