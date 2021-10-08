package de.hsw;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    public static HashMap<LocalDateTime, Historieneintrag> historie = new HashMap<>();

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
        Historieneintrag h = new Historieneintrag();
        historie.put(LocalDateTime.now(), h);
        
        

        if (isbn == null) {
            h.setAnfrage("ValidateISBN mit leerer ISBN");
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass an ISBN on the query string").build();
        } else {
            h.setAnfrage("ValidateISBN mit folgender ISBN: "+isbn);
            if(validateIsbn(isbn)){
                h.setAntwort("valid");
                return request.createResponseBuilder(HttpStatus.OK).body("valid").build();
            }
            h.setAntwort("invalid");
            return request.createResponseBuilder(HttpStatus.OK).body("invalid").build();
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
        Historieneintrag h = new Historieneintrag();
        historie.put(LocalDateTime.now(), h);

        String isbn = request.getQueryParameters().get("isbn");

        if (isbn == null) {
            h.setAnfrage("calculateCheckDigit mit leerem ISBN-Parameter");
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass an ISBN on the query string").build();
        } else {
            h.setAnfrage("calculateCheckDigit ISBN: "+isbn);
            char cd;
            try {
                cd = calculateCheckDigit(isbn);
            } catch (Exception e) {
                h.setAntwort("Fehler: "+e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Fehler: "+e.getMessage()).build();
            }
            h.setAntwort("The calculated Checkdigit is \""+cd+"\".");
            return request.createResponseBuilder(HttpStatus.OK).body(cd).build();
        }
    }

    @FunctionName("showHistory")
    public HttpResponseMessage showHistory(@HttpTrigger(name="req", 
                methods = {HttpMethod.GET}, 
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        StringBuilder sb = new StringBuilder("Historie\n");
        for (Entry e : historie.entrySet()){
            sb.append("LocalDateTime: " + e.getKey()).append("\nAnfrage: "+ ((Historieneintrag)e.getValue()).getAnfrage());
            sb.append("\nAntwort: "+ ((Historieneintrag) e.getValue()).getAntwort());
        }
        String hist = sb.toString();
        return request.createResponseBuilder(HttpStatus.OK).body(hist).build();

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
        Historieneintrag h = new Historieneintrag();
        historie.put(LocalDateTime.now(), h);
        String gnumber = request.getQueryParameters().get("gnumber");
        String vnumber = request.getQueryParameters().get("vnumber");
        String tnumber = request.getQueryParameters().get("tnumber");

        if (gnumber == null || vnumber == null || tnumber == null) {
            h.setAnfrage("generateISBN mit fehlenden Parametern");
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a gnumber(Gruppennummer), vnumber(Verlagsnummer) and tnumber(Titelnummer) on the query string").build();
        } else {
            String isbn;
            h.setAnfrage("generateIsbn mit Gruppennummer: "+gnumber+", Verlagsnummer: "+vnumber+" und Titelnummer: "+tnumber);
            try {
                isbn = generateISBN(gnumber, vnumber, tnumber);
            } catch (Exception e) {
                h.setAntwort("Fehler: "+e.getMessage());
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Fehler: "+e.getMessage()).build();
            }
            h.setAntwort("The calculated ISBN is \""+isbn+"\".");
            return request.createResponseBuilder(HttpStatus.OK).body(isbn).build();
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
        String isbn = gnumber + "-" + vnumber + "-" + tnumber + "-";
        char checkDigit = calculateCheckDigit(isbn);
        isbn = isbn + checkDigit;
        return isbn;
    }

    
    //TODO format Methode
}
