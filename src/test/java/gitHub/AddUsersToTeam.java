package gitHub;

import genericFunctions.RestFunctions;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;


public class AddUsersToTeam extends RestFunctions {

    String userDetails = "abilashmchand@gmail.com,aravindsvb@gamil.com,asiflayeeq@gmail.com,b.sathish21@gmail.com,bharathece15@gmail.com,bharathipriya03@gmail.com,devi12anbu@gmail.com,eswaran.anantharaman@gmail.com,hemaabinaya18@gmail.com,hemamurugesan2794@gmail.com,jotheesh1992@gmail.com,karthikeyan1993ganesan@gmail.com,Karupasami94@gmail.com,krishnabharathimano06@gmail.com,krishnakumarj1993@gmail.com,letsmailbhuvanesh@gmail.com,nawin27laxman@gmail.com,priyankabuvana3@gmail.com,sgangadaran1999@gmail.com,shankarec24@gmail.com,shilviyajayaraj@gmail.com,shiva39nand@gmail.com,spradeep1331@gmail.com,subash129@gmail.com,uma31be@gmail.com,vigneshkumartamilselvan@gmail.com";

    @Test
    public void addUsersToTeamAndOrganization(){
        String addUser = "https://api.github.com/orgs/"+StaticData.organizationName+"/invitations";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", "direct_member");
        jsonObject.put("team_ids", new int[]{getTeamId()});
        for(String eachEmail : userDetails.split(",")){
            jsonObject.put("email", eachEmail);
            Response resp = postCall(null, jsonObject.toString(), StaticData.authKey, TOKEN_TYPE.OAUTH,addUser);
            if (resp.statusCode() > 299 && resp.statusCode() < 200)
                System.out.println("User is not added to Git " +eachEmail);
        }
    }

    private int getTeamId(){
        return getJsonResponse(getCall(null,StaticData.authKey,TOKEN_TYPE.OAUTH,"https://api.github.com/orgs/"+StaticData.organizationName+"/teams/"+StaticData.teamName)).get("id");
    }
}
