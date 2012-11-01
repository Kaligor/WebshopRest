package se.black.webshop.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BadXMLException extends WebApplicationException {
	
	private static final long serialVersionUID = -1675824215209021124L;

	public BadXMLException(String message) {
		super(Response.status(Status.BAD_REQUEST).entity(message).build());
	}
}
