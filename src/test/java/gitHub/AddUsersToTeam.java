package gitHub;

import genericFunctions.RestFunctions;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;


public class AddUsersToTeam extends RestFunctions {

//    String userDetails = "SahanaBaasha26,AbiramiSam,Anandveera,AnnieDheeben,arun-qa,Ashishdhal\n" +
//            ",AswinBahulayan,BalajiSaravanan210697,BenjaminD98,darnimca,rdineshbabu22,DivyaVithyakar,geethakalaiselvan2303,gokulsam07,KarthikD1999,mithran14,nagasgit,NazreenFathima,Ajay230818,CPoorani,pragapraga,RajiSDET,Ramya Seshan,sathishkumaravelu,sivagtesting,SrikanthSDET,subha17431,NarpaviSuresh,sureshnatesan,tamilelavenil,vijoy-vijayan";
    String userDetails = "AbiramiSam";

    @Test
    public void addUsersToTeamAndOrganization(){
        String addUser = "https://api.github.com/orgs/"+StaticData.organizationName+"/invitations";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", "direct_member");
        jsonObject.put("team_ids", new int[]{getTeamId()});
        for(String eachEmail : userDetails.split(",")){
            jsonObject.put("username", eachEmail);
            Response resp = postCall(null, jsonObject.toString(), StaticData.authKey, TOKEN_TYPE.OAUTH,addUser);
            if (resp.statusCode() > 299 && resp.statusCode() < 200)
                System.out.println("User is not added to Git " +eachEmail);
        }
    }

    private int getTeamId(){
        return getJsonResponse(getCall(null,StaticData.authKey,TOKEN_TYPE.OAUTH,"https://api.github.com/orgs/"+StaticData.organizationName+"/teams/"+StaticData.teamName)).get("id");
    }
}
