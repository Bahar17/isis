package org.apache.isis.core.webapp;

import javax.servlet.ServletContext;

import com.google.common.base.Strings;

public class WebAppContextSupport {

    /**
     * Property name given to the context path of the web application as returned by 
     * {@link ServletContext#getContextPath()}.
     */
	public static final String WEB_APP_CONTEXT_PATH = "application.webapp.context-path";
	
	
	public static String prependContextPathIfPresent(String contextPath, String path) {
		if(Strings.isNullOrEmpty(contextPath) || contextPath.equals("/"))
			return path;
		
		if(!contextPath.startsWith("/"))
			throw new IllegalArgumentException(
					"contextPath must start with a slash '/' character, got '"+contextPath+"'");

		if(!path.startsWith("/"))
			throw new IllegalArgumentException(
					"path must start with a slash '/' character, got '"+path+"'");
		
		return contextPath + path;
	}
	
}
