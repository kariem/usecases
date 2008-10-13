<?xml version="1.0"?>
<!--
	XML Stylesheet
	$Id$
-->
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:uc="urn:use-case:description"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl"
	exclude-result-prefixes="uc">

	<xsl:strip-space elements="*"/>

	<!-- This stylesheet produces HTML in utf-8 -->
	<xsl:output method="html" indent="no"
			doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
			encoding="utf-8"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:if test="uc:use-cases/uc:title">
						<xsl:value-of select="uc:use-cases/uc:title"/>
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:text>Preview</xsl:text>
				</title>
				<link rel="stylesheet" href="style/preview.css"/>
			</head>

			<body>
				<div id="container">
					<xsl:apply-templates select="uc:use-cases"/>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="uc:use-cases/uc:title">
		<h1>
			<xsl:apply-templates/>
		</h1>
	</xsl:template>

	<xsl:template match="uc:section">
		<div class="section">
			<div>
				<xsl:apply-templates select="@*"/>
				<xsl:apply-templates select="./*"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="uc:section/uc:title">
		<xsl:variable name="depth" select="count(ancestor::*)"/>
		<xsl:element name="h{$depth}">
			<xsl:number level="multiple" count="uc:section" format="1.1"/>
			<xsl:text> </xsl:text>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="uc:uc">
		<div class="usecase">
		<div>
		<xsl:apply-templates select="@change"/>
		<xsl:variable name="depth" select="count(ancestor-or-self::*)"/>
		<xsl:element name="h{$depth}">
			<xsl:if test="@xml:id">
				<xsl:element name="a">
					<xsl:attribute name="name">
						<xsl:value-of select="@xml:id"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>

			<xsl:number level="multiple" count="uc:section|uc:uc" format="1.1"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="uc:title"/>
		</xsl:element>
		<dl>
			<dt>Version</dt>
			<dd>
				<xsl:choose>
					<xsl:when test="@version">
						<xsl:apply-templates select="@version"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>1.0</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</dd>

			<dt>Summary</dt>
			<dd>
				<xsl:apply-templates select="uc:desc"/>
			</dd>

			<dt>Primary Actor</dt>
			<dd>
				<xsl:apply-templates select="uc:actor"/>
			</dd>

			<dt>Preconditions</dt>
			<dd>
				<xsl:apply-templates select="uc:pre"/>
			</dd>

			<dt>Use Case Flow</dt>
			<dd>
				<xsl:apply-templates select="uc:flow"/>
			</dd>

			<dt>Postconditions</dt>
			<dd>
				<xsl:apply-templates select="uc:post"/>
			</dd>

			<xsl:if test="uc:non-funct">
				<dt>Non-functional Requirements</dt>
				<dd>
					<xsl:apply-templates select="uc:non-funct"/>
				</dd>
			</xsl:if>

			<xsl:if test="uc:comment">
				<dt>Notes</dt>
				<dd>
					<xsl:apply-templates select="uc:comment"/>
				</dd>
			</xsl:if>
		</dl>
		</div>
		</div>
	</xsl:template>

	<xsl:template match="uc:flow">
		<table class="flow">
			<!-- copy steps to node set with additional @index showing the position -->
			<xsl:variable name="steps_custom">
				<xsl:for-each select="uc:step">
					<xsl:copy>
						<!-- copy attributes -->
						<xsl:copy-of select="@*"/>
						<!-- add index -->
						<xsl:attribute name="index">
							<xsl:value-of select="position()"/>
						</xsl:attribute>
						<!-- copy descendants -->
						<xsl:copy-of select="."/>
					</xsl:copy>
				</xsl:for-each>
			</xsl:variable>

			<xsl:variable name="steps" select="exsl:node-set($steps_custom)/uc:step"/>

			<xsl:for-each select="$steps/uc:step">
				<tr>
					<xsl:call-template name="flow.step"/>
				</tr>
			</xsl:for-each>
			<xsl:if test="uc:alternatives">
				<xsl:for-each select="uc:alternatives/uc:alternative">
					<xsl:call-template name="flow.alternative">
						<xsl:with-param name="prefix">
							<xsl:number value="position()" format="A" />
						</xsl:with-param>
						<xsl:with-param name="steps" select="$steps"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:if>
		</table>
	</xsl:template>

	<xsl:template name="flow.step">
		<!-- simple parameters, overriding generated information -->
		<xsl:param name="label" select="''" />
		<xsl:param name="content" select="''" />
		<!-- parameters used for content generation -->
		<xsl:param name="start" select="'0'"/>
		<xsl:param name="prefix"/>

		<td class="item">
			<xsl:choose>
				<xsl:when test="$label != ''">
					<xsl:value-of select="$label"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$prefix"/>
					<xsl:value-of select="$start + position()"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
		<td class="content">
			<xsl:choose>
				<xsl:when test="$content != ''">
					<xsl:value-of select="$content"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<xsl:template name="flow.alternative">
		<xsl:param name="prefix" select="''"/>
		<!-- step elements with index, name and content -->
		<xsl:param name="steps" />
		<tr class="alternative">
			<td colspan="2" class="alternative">
				<xsl:text>Alternative Flow </xsl:text>
				<xsl:if test="$prefix != ''">
					<xsl:value-of select="$prefix"/>
				</xsl:if>
				<xsl:if test="uc:description">
					<span class="description">
						<xsl:text>: </xsl:text>
						<xsl:apply-templates select="uc:description"/>
					</span>
				</xsl:if>
			</td>
		</tr>

		<!-- check start position -->
		<xsl:variable name="startpos">
			<xsl:choose>
				<xsl:when test="@start">
					<xsl:call-template name="search_by_name">
						<xsl:with-param name="name" select="@start"/>
						<xsl:with-param name="nodes" select="$steps"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>1</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="start" select="$startpos - 1"/>

		<!-- render steps -->
		<xsl:for-each select="uc:step">
			<tr>
				<xsl:call-template name="flow.step">
					<xsl:with-param name="prefix" select="$prefix"/>
					<xsl:with-param name="start" select="$start"/>
				</xsl:call-template>
			</tr>
		</xsl:for-each>

		<!-- check continue position for re-entry to default flow -->
		<xsl:if test="@continue">
			<xsl:variable name="continuepos">
				<xsl:call-template name="search_by_name">
					<xsl:with-param name="name" select="@continue"/>
					<xsl:with-param name="nodes" select="$steps"/>
				</xsl:call-template>
			</xsl:variable>

			<tr>
				<xsl:call-template name="flow.step">
					<xsl:with-param name="label">
						<xsl:value-of select="$prefix"/>
						<xsl:value-of select="$start + 1 + count(uc:step)"/>
					</xsl:with-param>
					<xsl:with-param name="content">
						<xsl:text>Continue the use case execution at step </xsl:text>
						<xsl:value-of select="$continuepos - 1"/>
						<xsl:text> of the default flow.</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Include, exclude and generic references -->

	<xsl:template match="uc:include">
		<xsl:call-template name="ref.link">
			<xsl:with-param name="prefix" select="'Execute use case '"/>
			<xsl:with-param name="title" select="'&#171;include&#187;'"/>
			<xsl:with-param name="ref" select="@ref"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="uc:extend">
		<xsl:call-template name="ref.link">
			<xsl:with-param name="prefix" select="'Extension with use case '"/>
			<xsl:with-param name="title" select="'&#171;extend&#187;'"/>
			<xsl:with-param name="ref" select="@ref"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="uc:ref">
		<xsl:call-template name="ref.link">
			<xsl:with-param name="ref" select="@ref"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="ref.reference">
		<xsl:param name="ref" select="@ref"/>
		<xsl:param name="nodes" select="/"/>
		<xsl:for-each select="$nodes//uc:uc[@xml:id = $ref]">
			<xsl:value-of select="uc:title"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="ref.link">
		<xsl:param name="prefix" select="''"/>
		<xsl:param name="title" select="'use case reference'"/>
		<xsl:param name="ref" select="@ref"/>
		<xsl:param name="reference">
			<xsl:call-template name="ref.reference"/>
		</xsl:param>

		<xsl:if test="$prefix != ''">
			<xsl:value-of select="$prefix"/>
		</xsl:if>
		<a href="#{@ref}" title="{$title}"><xsl:value-of select="$reference"/></a>
	</xsl:template>

	<xsl:template name="search_by_name">
		<xsl:param name="name"/>
		<xsl:param name="nodes"/>

		<xsl:for-each select="$nodes[@name = $name]">
			<xsl:value-of select="@index"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="debug_nodes">
		<xsl:param name="nodes"/>
		<xsl:message>
			Node set with <xsl:value-of select="count($nodes)"/> nodes
			<xsl:for-each select="$nodes">
				<xsl:text>&#xA;</xsl:text><xsl:value-of select="name()"/>
				<xsl:for-each select="attribute::*"><xsl:text> </xsl:text><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"</xsl:for-each>
			</xsl:for-each>
		</xsl:message>
	</xsl:template>

	<!-- Change annotations -->

	<xsl:template match="uc:change">
		<span>
			<xsl:attribute name="class">
				<xsl:text>change</xsl:text>
				<xsl:if test="@type">
					<xsl:text> </xsl:text>
					<xsl:value-of select="@type"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates select="node()"/>
		</span>
	</xsl:template>

	<xsl:template match="@change">
		<xsl:attribute name="class">
			<xsl:text>change</xsl:text>
			<xsl:text> </xsl:text>
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<!-- Mixed content model for some elements -->

	<xsl:template match="uc:pre | uc:post | uc:actor | uc:description ">
		<xsl:apply-templates select="./* | text()"/>
	</xsl:template>

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>