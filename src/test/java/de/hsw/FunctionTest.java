package de.hsw;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for Function class.
 */
public class FunctionTest {
    

    /**
     * Unit-Test für korrekte Isbn-Validierung.
     */
    @Test
    public void testIsbnValidator(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("isbn", "3928475320");
        doReturn(queryParams).when(req).getQueryParameters();
        
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        final HttpResponseMessage ret = new Function().validate(req, context);

        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("valid", ret.getBody().toString(), "ISBN wird nicht validiert, Antwort lautet: "+ret.getBody().toString());
    }

    /**
     * Unit-Test für korrekte Prüfzifferberechnung.
     */
    @Test
    public void testCheckDigitCalculation(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("isbn", "392847532");
        doReturn(queryParams).when(req).getQueryParameters();
        
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        final HttpResponseMessage ret = new Function().calculate(req, context);
        
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("0", ret.getBody().toString(), "Prüfziffer wurde falsch berechnet, Antwort: "+ret.getBody().toString());
    }

    /**
     * Unit-Test für korrekte ISBN-Erzeugung.
     */
    @Test
    public void testeIsbnErzeugung(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("gnumber", "3");
        queryParams.put("vnumber", "9284");
        queryParams.put("tnumber", "7532");
        doReturn(queryParams).when(req).getQueryParameters();
        
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));
        
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        final HttpResponseMessage ret = new Function().build(req, context);
        
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("3-9284-7532-0", ret.getBody().toString(), "ISBN wurde falsch berechnet, Antwort: "+ret.getBody().toString());
    }
}
