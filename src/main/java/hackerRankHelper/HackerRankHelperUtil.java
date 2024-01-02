package hackerRankHelper;

import genericFunctions.HelperUtil;

import java.util.Base64;

public class HackerRankHelperUtil extends HelperUtil {

    public static String getAuthKey(){
        return new String(Base64.getDecoder().decode(prop.getProperty("HackerRank_key")));
    }
    public static String getTestId(){
        return prop.getProperty("HackerRank_testID");
    }
    public static String getReportPath(){
        return prop.getProperty("HackerRank_ReportPath");
    }
}
