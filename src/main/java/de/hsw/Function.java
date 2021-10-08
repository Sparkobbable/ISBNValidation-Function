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

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

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
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("validateIsbn")
    public HttpResponseMessage validate(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String isbn = request.getQueryParameters().get("isbn");

        if (isbn == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass an ISBN on the query string").build();
        } else {
            if(validateIsbn(isbn)){
                return request.createResponseBuilder(HttpStatus.OK).body("The ISBN \""+isbn+"\" is valid.").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body("The ISBN \""+isbn+"\" is invalid.").build();
        }
    }

    @FunctionName("calculateCheckDigit")
    public HttpResponseMessage calculate(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String isbn = request.getQueryParameters().get("isbn");

        if (isbn == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass an ISBN on the query string").build();
        } else {
            char cd;
            try {
                cd = calculateCheckDigit(isbn);
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Fehler: "+e.getMessage()).build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body("The calculated Checkdigit is \""+cd+"\".").build();
        }
    }

    @FunctionName("createIsbn")
    public HttpResponseMessage build(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String gnumber = request.getQueryParameters().get("gnumber");
        String vnumber = request.getQueryParameters().get("vnumber");
        String tnumber = request.getQueryParameters().get("tnumber");

        if (gnumber == null || vnumber == null || tnumber == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a gnumber(Gruppennummer), vnumber(Verlagsnummer) and tnumber(Titelnummer) on the query string").build();
        } else {
            String isbn;
            try {
                isbn = generateISBN(gnumber, vnumber, tnumber);
            } catch (Exception e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Fehler: "+e.getMessage()).build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body("The calculated ISBN is \""+isbn+"\".").build();
        }
    }

    public boolean validateIsbn(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }
        int sum = 0;
        for (int i = 1; i < 11; i++) {
            String nextChar = Character.toString(isbn.charAt(i - 1));
            int nextInt;
            if (i == 10 && nextChar.equals("X")) {
                nextInt = 10;
            } else {
                try {
                    nextInt = Integer.parseInt(nextChar);
                } catch (NumberFormatException e) {
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
        double modulo = sum % 11;
        if (modulo != 0) {
            return false;
        } else
            return true;
    }

    public char calculateCheckDigit(String isbn) throws Exception{
        if(isbn.length() != 9){
            throw new Exception("Der ISBN-String hat die falsche Länge!");
        }
        int sum = 0;
        for (int i=1; i<10; i++){
            String nextChar = Character.toString(isbn.charAt(i-1));
            Integer nextInt;
            try {
                nextInt = Integer.parseInt(nextChar);
            } catch (NumberFormatException e) {
                throw new Exception("Der ISBN-String enthält unerwartete Zeichen!");
            }
            if(0 <= nextInt && nextInt < 10){
                //valides Zeichen
            }
            else{
                throw new Exception("Der ISBN-String enthält unerwartete Zeichen!");
            }
            sum = sum + (nextInt*i);
        }
        int modulo = sum % 11;
        if(modulo == 10){
            return 'X';
        }
        return Character.forDigit(modulo, 10);
    }

    public String generateISBN(String gnumber, String vnumber, String tnumber) throws Exception{
        String isbn = gnumber + vnumber + tnumber;
        char checkDigit = calculateCheckDigit(isbn);
        isbn = isbn + checkDigit;
        return isbn;
    }

    
    //TODO format Methode
}
