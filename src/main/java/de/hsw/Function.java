package de.hsw;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    private String gnumber;

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS) final HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    public boolean validateIsbn(final String isbn) {
        if (isbn.length() != 10) {
            return false;
        }
        int sum = 0;
        for (int i = 1; i < 11; i++) {
            final String nextChar = Character.toString(isbn.charAt(i - 1));
            int nextInt;
            if (i == 10 && nextChar.equals("X")) {
                nextInt = 10;
            } else {
                try {
                    nextInt = Integer.parseInt(nextChar);
                } catch (final NumberFormatException e) {
                    return false;
                }
                if (0 <= nextInt && nextInt < 10) {
                    // valides Zeichen
                } else {
                    return false;
                }
            }
            sum = sum + (nextInt * i);
        }
        final double modulo = sum % 11;
        if (modulo != 0) {
            return false;
        } else
            return true;
    }

    public char calculateCheckDigit(final String isbn){
        if(isbn.length() != 9){
            return 'f';
        }
        int sum = 0;
        for (int i=1; i<10; i++){
            final String nextChar = Character.toString(isbn.charAt(i-1));
            Integer nextInt;
            try {
                nextInt = Integer.parseInt(nextChar);
            } catch (final NumberFormatException e) {
                return 'f';
            }
            if(0 <= nextInt && nextInt < 10){
                //valides Zeichen
            }
            else{
                return 'f';
            }
            sum = sum + (nextInt*i);
        }
        final int modulo = sum % 11;
        if(modulo == 10){
            return 'X';
        }
        return Character.forDigit(modulo, 10);
    }

    public String genereteISBN(final String gnumber, final String vnumber, final String tnumber){
        String isbn = gnumber + vnumber + tnumber;
    }

    
    //TODO format Methode
}
