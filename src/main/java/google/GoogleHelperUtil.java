package google;

import genericFunctions.HelperUtil;

import java.util.Base64;

public class GoogleHelperUtil extends HelperUtil {

    public static String getAuthKey(){
        return new String(Base64.getDecoder().decode(prop.getProperty("Google_token")));
    }
    public static String getSheetId(){
        return prop.getProperty("Google_sheetId");
    }
    public static String getCompletionSheetId(){
        return prop.getProperty("Google_completionStatusSheet");
    }
    public static String getMenteeSheetId(){
        return prop.getProperty("Google_menteeDetailsSheet");
    }
    public static String getToEmailId(){
        return prop.getProperty("toEmail");
    }
    public static String getFromEmailId(){
        return prop.getProperty("fromEmail");
    }


}
