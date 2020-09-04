<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">


<helpset version="2.0">
	<!-- title -->
	<title>TariffEye - Help</title>

	<!-- maps -->
	<maps>
		<homeID>manual_toc</homeID>
		<mapref location="metafiles/map.jhm" />
	</maps>
	
	<!-- views -->
	<view>
		<name>TOC</name>
		<label>Table Of Contents</label>
		<type>javax.help.TOCView</type>
		<data>metafiles/toc.xml</data>
	</view>
	
	<!--
	<view mergetype="javax.help.NoMerge">
		<name>Index</name>
		<label>Index</label>
		<type>javax.help.IndexView</type>
		<data>metafiles/index.xml</data>
	</view>
	-->
	
	<presentation default="true">
		<name>default</name>
		<size width="500" height="300" />
	</presentation>
</helpset>