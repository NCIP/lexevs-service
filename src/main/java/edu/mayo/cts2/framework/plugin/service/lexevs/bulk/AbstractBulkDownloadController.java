/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.bulk;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.mayo.cts2.framework.webapp.rest.extensions.controller.ControllerProvider;


/**
 * An abstract Controller for any bulk downloads.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractBulkDownloadController implements ControllerProvider {
	
	protected static final String DEFAULT_SEPARATOR = "|";
	
	protected static final String DEFAULT_FILE_NAME = "terminology-bulk-download.txt";

	public static class UserInputException extends RuntimeException {

		private static final long serialVersionUID = -4224285377598954236L;

		public UserInputException(String message){
			super(message);
		}
	}
	
	@ExceptionHandler(UserInputException.class)
	@ResponseBody
	public void handleException(UserInputException e, HttpServletResponse response) {
		StringBuffer error = new StringBuffer();
	    error.append("Invalid Input: ").append(e.getMessage()).append("\n");
	    
	    error.append(this.getValidParametersMessage());
	    
	    this.writeException(response, error.toString(), 400);
	}
	
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public void handleUnknownException(Throwable e, HttpServletResponse response) {
	    StringBuffer error = new StringBuffer();
	    error.append("Server Error: ").append(e.getMessage()).append("\n");
	    
	    error.append(this.getValidParametersMessage());
	    
	    this.writeException(response, error.toString(), 500);
	}
	
	protected abstract String getValidParametersMessage();

	/**
	 * Sets the headers.
	 *
	 * @param response the response
	 * @param filename the filename
	 */
	protected void setHeaders(HttpServletResponse response, String filename){
		String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", filename);
        response.setHeader(headerKey, headerValue);
		response.setContentType("text/plain; charset=utf-8");
	}
	
	protected void writeException(HttpServletResponse response, String message, int errorCode){
		response.setContentType("text/plain; charset=utf-8");
		response.setStatus(errorCode);
		
		try {
			IOUtils.write(message, response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
