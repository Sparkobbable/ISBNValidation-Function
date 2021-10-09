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
 * Unit test for Function class.
 */
public class FunctionTest {
    /**
     * Unit test for HttpTriggerJava method.
     */
    @Test
    public void testHttpTriggerJava() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
    }

    @Test
    public void testIsbnValidator(){
        String trueIsbn = "3928475320";
        String trueIsbn2 = "3551551677";
        boolean validatedTrueIsbn = new Function().validateIsbn(trueIsbn);
        boolean validatedTrueIsbn2 = new Function().validateIsbn(trueIsbn2);
        assertTrue(validatedTrueIsbn, "ISBN wird nicht validiert, obwohl sie korrekt ist.");
        assertTrue(validatedTrueIsbn2, "ISBN2 wird nicht validiert, obwohl sie korrekt ist.");
    }

    @Test
    public void testCheckDigitCalculation(){
        String isbn = "392847532";
        char checkDigit = '4';
        try {
            checkDigit = new Function().calculateCheckDigit(isbn);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals('0', checkDigit, "Prüfziffer wurde falsch berechnet.");
    }
    @Test
    public void testeIsbnErzeugung(){
        String gnumber = "3";
        String vnumber = "9284";
        String tnumber = "7532";
        String isbn="0", isbn2 = "0";
        try {
            isbn = new Function().generateISBN(gnumber,vnumber,tnumber);
            isbn2 = new Function().generateISBN("3", "5515", "5167");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("3-9284-7532-0", isbn, "ISBN wurde falsch berechnet.");
        
        assertEquals("3-5515-5167-7", isbn2, "ISBN2 wurde falsch berechnet.");
    }
}
