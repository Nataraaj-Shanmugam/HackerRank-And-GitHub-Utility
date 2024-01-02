package gitHub;

import genericFunctions.RestFunctions;
import gitHubHelper.GitHubHelperUtil;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;


public class AddUsersToTeam extends RestFunctions {

 String userDetails = "GitUserName";

    @Test
    public void addUsersToTeamAndOrganization(){
        String addUser = "https://api.github.com/orgs/"+GitHubHelperUtil.getOrganisationName()+"/invitations";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("role", "direct_member");
        jsonObject.put("team_ids", new int[]{getTeamId()});
        for(String eachEmail : userDetails.split(",")){
            jsonObject.put("username", eachEmail);
            Response resp = postCall(null, jsonObject.toString(), GitHubHelperUtil.getAuthKey(), TOKEN_TYPE.OAUTH,addUser);
            if (resp.statusCode() > 299 && resp.statusCode() < 200)
                System.out.println("User is not added to Git " +eachEmail);
        }
    }

    private int getTeamId(){
        return getJsonResponse(getCall(null,GitHubHelperUtil.getAuthKey(),TOKEN_TYPE.OAUTH,"https://api.github.com/orgs/"+GitHubHelperUtil.getOrganisationName()+"/teams/"+GitHubHelperUtil.getTeamName())).get("id");
    }
}
