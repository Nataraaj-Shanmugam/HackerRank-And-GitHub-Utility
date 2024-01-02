package gitHubHelper;

import genericFunctions.HelperUtil;
import genericFunctions.Helpers;

import java.util.Base64;
import java.util.Properties;

public class GitHubHelperUtil extends HelperUtil {

    public static String getAuthKey(){
        return new String(Base64.getDecoder().decode(prop.getProperty("GitHub_authKey")));
    }
    public static String getOrganisationName(){
        return prop.getProperty("GitHub_organizationName");
    }
    public static String getTeamName(){
        return prop.getProperty("GitHub_teamName");
    }
}
