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
     * Unit-Test für Isbn-Validierung bei invaliden Eingaben.
     */
    @Test
    public void testIsbnValidatorInvalidISBN(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
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
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus(), "Falscher Returncode bei ISBN=null");
        assertEquals("Please pass an ISBN on the query string", ret.getBody().toString(), "Fehlerhafte Antwort bei leerer ISBN, Antwort lautet: "+ret.getBody().toString());
        queryParams.put("isbn", "jdj");
        final HttpResponseMessage ret2 = new Function().validate(req, context);
        assertEquals(HttpStatus.OK, ret2.getStatus());
        assertEquals("invalid", ret2.getBody().toString(), "ISBN wird nicht validiert, Antwort lautet: "+ret.getBody().toString());
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
     * Unit-Test für korrekte Prüfzifferberechnung.
     */
    @Test
    public void testCheckDigitCalculationInvalidIsbn(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("isbn", "3928");
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
        
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
        assertEquals("Fehler: Der ISBN-String hat die falsche Länge!", ret.getBody().toString(), "Fehler wurde falsch ausgegeben, Antwort: "+ret.getBody().toString());

        queryParams.put("isbn", "123456h89");
        final HttpResponseMessage ret2 = new Function().calculate(req, context);
        
        assertEquals(HttpStatus.BAD_REQUEST, ret2.getStatus());
        assertEquals("Fehler: Der ISBN-String enthält unerwartete Zeichen!", ret2.getBody().toString(), "Fehler wurde falsch ausgegeben, Antwort: "+ret.getBody().toString());
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

        /**
     * Unit-Test für ISBN-Erzeugung mit fehlerhaften Parametern.
     */
    @Test
    public void testeIsbnErzeugungInvalidParams(){
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("gnumber", "3");
        queryParams.put("vnumber", "9284");
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
        
        assertEquals(HttpStatus.BAD_REQUEST, ret.getStatus());
        assertEquals("Please pass a gnumber(Gruppennummer), vnumber(Verlagsnummer) and tnumber(Titelnummer) on the query string", ret.getBody().toString(), "Falscher Fehler bei ISBN-Erzeugung, Antwort: "+ret.getBody().toString());
        
        queryParams.put("tnumber", "75324");
        final HttpResponseMessage ret2 = new Function().build(req, context);
        assertEquals(HttpStatus.BAD_REQUEST, ret2.getStatus());
        assertEquals("Fehler: Der ISBN-String hat die falsche Länge!", ret2.getBody().toString(), "Falscher Fehler bei ISBN-Erzeugung, Antwort: "+ret2.getBody().toString());
        
        queryParams.put("tnumber", "753t");
        final HttpResponseMessage ret3 = new Function().build(req, context);
        assertEquals(HttpStatus.BAD_REQUEST, ret3.getStatus());
        assertEquals("Fehler: Der ISBN-String enthält unerwartete Zeichen!", ret3.getBody().toString(), "Falscher Fehler bei ISBN-Erzeugung, Antwort: "+ret3.getBody().toString());
    }
}
