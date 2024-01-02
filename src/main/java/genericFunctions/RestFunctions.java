package genericFunctions;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.Map;

public class RestFunctions {
    public enum TOKEN_TYPE{
        OAUTH,
        TOKEN
    }

    private RequestSpecification requestSpecification(String token, TOKEN_TYPE token_type){
        RequestSpecification requestSpecification = RestAssured.given();
        if(token_type == TOKEN_TYPE.OAUTH) requestSpecification.auth().oauth2(token);
        else  if(token_type == TOKEN_TYPE.TOKEN) requestSpecification.headers(Collections.singletonMap("Authorization",token));
        return requestSpecification;
    }

    public ResponseBody getCall(Map<String, Object> headers, String token, TOKEN_TYPE token_type, String url){
        RequestSpecification requestSpecification = requestSpecification(token, token_type);
        if(headers!=null)
            requestSpecification.headers(headers);
        return requestSpecification.accept(ContentType.JSON).get(url).getBody();
    }

    public ResponseBody putCall(Map<String, Object> headers, String token, TOKEN_TYPE token_type, String url){
        RequestSpecification requestSpecification = requestSpecification(token, token_type);
        if(headers!=null)
            requestSpecification.headers(headers);
        return requestSpecification.put(url).getBody();
    }

    public Response postCall(Map<String, Object> headers, String body , String token, TOKEN_TYPE token_type, String url){
        RequestSpecification requestSpecification = requestSpecification(token, token_type);
        if(headers!=null)
            requestSpecification.headers(headers);
        if(body != null)
            requestSpecification.body(body);
        return requestSpecification.post(url);
    }

    public JsonPath getJsonResponse(ResponseBody responseBody){
        return responseBody.jsonPath();
    }
}
