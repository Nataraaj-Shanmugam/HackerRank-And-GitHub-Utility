package gitHub;

import genericFunctions.RestFunctions;
import google.GoogleGenericFunctions.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.*;

public class AuditHomeWork extends RestFunctions{
    static String sheetId = "17oVcButb1QtnjAAkJn9KYHmCV7wjkREv44dcMPxD7fA",
            token = "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";
    private Map<String, Object> getHeaders(){
//        HashMap<String, Object> header = new HashMap<>();
//        header.put("Authorization", "Bearer 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");
//        header.put("X-GitHub-Api-Version", "2022-11-28");
        return Collections.singletonMap("X-GitHub-Api-Version", "2022-11-28");
    }

    private String getBaseUrl(String repoOwner, String repoName){
//        return "https://api.github.com/repos/"+repoOwner+"/"+repoName+"/contents/src/main/java/";
        return "https://api.github.com/repos/"+repoOwner+"/"+repoName+"/contents/src/main/java/mandatoryHomeWork/";
    }

    private String getUrl(String repoOwner, String repoName,String week, String day){
        return getBaseUrl(repoOwner,repoName)+week+"/"+day;
    }

    private List<String> getWeekNumber(String repoOwner, String repoName){
        return getJsonResponse(getCall(getHeaders(),token,TOKEN_TYPE.OAUTH,getBaseUrl(repoOwner,repoName))).getList("findAll {it.type ='dir' && it.name =~ 'week'}.name");
    }

    private List<String> getDaysNumber(String repoOwner, String repoName,String week){
        return getJsonResponse(getCall(getHeaders(),token,TOKEN_TYPE.OAUTH,getBaseUrl(repoOwner,repoName)+week)).getList("findAll {it.type ='dir' && it.name =~ 'day'}.name");
    }

    private HashMap<String, List<Object>> getMenteeDetailsData(){
        HashMap<String, List<Object>> masterData = new HashMap<>();
        List<List<Object>> masterSheetData =  GoogleSheet.getData(sheetId, "Mentee Details");
        masterSheetData.remove(0);
        for(List<Object> eachData : masterSheetData){
            masterData.put((String)eachData.get(1), eachData);
        }
        return masterData;
    }

    private void getDSAAndUpdate(String ownerName, String repoName,  HashMap<String, Short> counter, List<List<Object>> auditData){
        short maxNumber = 0;
        for (String week : getWeekNumber(ownerName,repoName)) {
            counter.put(StringUtils.capitalize(week), (short)0);
            for (String day : getDaysNumber(ownerName,repoName,week)) {
                ArrayList<Object> temp = new ArrayList<>();
                temp.add(StringUtils.capitalize(week));
                temp.add(StringUtils.capitalize(day));
                ArrayList<String> contentsCall = getCall(null, StaticData.authKey, TOKEN_TYPE.OAUTH, getUrl(ownerName, repoName, week, day)).jsonPath().get("html_url");
                counter.put(StringUtils.capitalize(week), (short)(counter.get(StringUtils.capitalize(week))+contentsCall.size()));
                temp.add(contentsCall.size());
                maxNumber = (short) Math.max(contentsCall.size(), maxNumber);
                temp.addAll(contentsCall);
                auditData.add(temp);
            }
        }
        auditData.add(0, getMenteeSheetHeader(maxNumber));
    }

    private void clearAndUpdateSheet(String menteeName, List<List<Object>> auditData ){
        GoogleSheet.clearSheetRange(sheetId,menteeName+"!A:Z");
        GoogleSheet.updateSheet(sheetId,menteeName+"!A:Z", auditData);
    }

    private void getSeleniumUpdate(String ownerName, String repoName,  HashMap<String, Short> menteeCumulativeData,  List<List<Object>> auditData ){
        List<Object> contentsCall = getCall(null, StaticData.authKey, TOKEN_TYPE.OAUTH, getBaseUrl(ownerName, repoName)+"selenium").jsonPath().get("html_url");
        contentsCall.addAll(0, Arrays.asList("Selenium","",contentsCall.size()));
        auditData.add(contentsCall);
        menteeCumulativeData.put("Selenium", (short)contentsCall.size());
    }

    private List<Object> getMenteeSheetHeader(short maxNumber){
        List<Object> header = new ArrayList<>();
        header.add("Entity/Week #");
        header.add("Day #");
        header.add("# of problems");
        for(short i = 1; i<= maxNumber; i++)
            header.add("Question "+i);
        return header;
    }

    private HashMap[] setStatusSheetHeaderAndMenteeRowNumber(){
        HashMap<String, Short> header = new HashMap<>();
        HashMap<String, Short> menteeRowNumber = new HashMap<>();

        return new HashMap[]{header,menteeRowNumber};
    }

    @Test
    private void auditData(){
        HashMap<String, HashMap<String, Short>> menteeCounter = new HashMap<>();
        /*
        todo - get the completion sheet header and column number
        and map to say row number of an mentee
         */

        HashMap[] statusSheetDetails = setStatusSheetHeaderAndMenteeRowNumber();

        for(Map.Entry<String,List<Object>> eachMentee: getMenteeDetailsData().entrySet()){
            if(eachMentee.getValue().size() > 2){
                String ownerName = (String)eachMentee.getValue().get(2),
                        repoName = (String)eachMentee.getValue().get(3),
                        menteeName =(String) eachMentee.getValue().get(1);
                HashMap<String, Short> menteeCumulativeData = new HashMap<>();
                List<List<Object>> auditData = new ArrayList<>();
                getSeleniumUpdate(ownerName,repoName,menteeCumulativeData, auditData);
                getDSAAndUpdate(ownerName,repoName, menteeCumulativeData, auditData);
                clearAndUpdateSheet(menteeName, auditData);
                menteeCounter.put(menteeName,menteeCumulativeData);
            }else{
                System.out.println("Fill details for "+eachMentee.getKey());
            }
        }
        System.out.println(menteeCounter);

    }
}
