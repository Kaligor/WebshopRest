package se.black.webshop.rest.exceptionmappers;

import se.black.webshop.model.exception.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.JsonSyntaxException;

public class ExceptionMappers {
	
	@Provider
	public static class NoSuchEntryMapper implements ExceptionMapper<NoSuchEntryException> {

		@Override
		public Response toResponse(NoSuchEntryException exception) {
			exception.printStackTrace();
			return Response.status(Status.NOT_FOUND).entity("{\"errormessage\":\"" + exception.getMessage() + "\"}").build();
		}
		
	}
	
	@Provider
	public static class DuplicateEntryMapper implements ExceptionMapper<DuplicateEntryException> {
		
		@Override
		public Response toResponse(DuplicateEntryException exception) {
			exception.printStackTrace();
			return Response.status(Status.CONFLICT).entity("{\"errormessage\":\"" + exception.getMessage() + "\"}").build();
		}
	}

	@Provider
	public static class DatasourceMapper implements ExceptionMapper<DatasourceException> {
		
		@Override
		public Response toResponse(DatasourceException exception) {
			exception.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"errormessage\":\"" + exception.getMessage() + "\"}").build();
		}
	}
	
	@Provider
	public static class JsonSyntaxMapper implements ExceptionMapper<JsonSyntaxException> {
		
		@Override
		public Response toResponse(JsonSyntaxException exception) {
			exception.printStackTrace();
			return Response.status(Status.BAD_REQUEST).entity("{\"errormessage\":\"" + exception.getMessage() + "\"}").build();
		}
	}
	
	@Provider
	public static class AllOtherExceptionsMapper implements ExceptionMapper<Exception> {
		
		@Override
		public Response toResponse(Exception exception) {
			exception.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"errormessage\":\" Internal Server Error \"}").build();
		}
	}
}
