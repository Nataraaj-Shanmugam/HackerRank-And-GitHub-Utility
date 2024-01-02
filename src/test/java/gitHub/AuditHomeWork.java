package gitHub;

import genericFunctions.RestFunctions;
import gitHubHelper.GitHubHelperUtil;
import google.GoogleGenericFunctions;
import google.GoogleGenericFunctions.*;
import google.GoogleHelperUtil;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

public class AuditHomeWork extends GoogleHelperUtil {
    RestFunctions restFunctions = new RestFunctions();

    private Map<String, Object> getHeaders(){
        return Collections.singletonMap("X-GitHub-Api-Version", "2022-11-28");
    }

    private String getBaseUrl(String repoOwner, String repoName){
        return "https://api.github.com/repos/"+repoOwner+"/"+repoName+"/contents/src/main/java/mandatoryHomeWork/";
    }

    private String getUrl(String repoOwner, String repoName,String week, String day){
        return getBaseUrl(repoOwner,repoName)+week+"/"+day;
    }

    private List<String> getWeekNumber(String repoOwner, String repoName){
        return restFunctions.getJsonResponse(restFunctions.getCall(getHeaders(),getAuthKey(),RestFunctions.TOKEN_TYPE.OAUTH,getBaseUrl(repoOwner,repoName))).getList("findAll {it.type ='dir' && it.name =~ 'week'}.name");
    }

    private List<String> getDaysNumber(String repoOwner, String repoName,String week){
        return restFunctions.getJsonResponse(restFunctions.getCall(getHeaders(),getAuthKey(),RestFunctions.TOKEN_TYPE.OAUTH,getBaseUrl(repoOwner,repoName)+week)).getList("findAll {it.type ='dir' && it.name =~ 'day'}.name");
    }

    private HashMap<String, List<Object>> getMenteeDetailsData(){
        HashMap<String, List<Object>> masterData = new HashMap<>();
        List<List<Object>> masterSheetData =  GoogleSheet.getData(getSheetId(), getMenteeSheetId());
        masterSheetData.remove(0);
        for(List<Object> eachData : masterSheetData){
            masterData.put((String)eachData.get(1), eachData);
        }
        return masterData;
    }

    private void getDSAAndUpdate(String ownerName, String repoName,  HashMap<String, Short> counter, List<List<Object>> auditData, HashMap<String, Short> homeWorkGiven){
        short maxNumber = 0;
        for (String week : getWeekNumber(ownerName,repoName)) {
            try{
                String weekNumber = StringUtils.capitalize(week);
                counter.put(weekNumber, (short)0);
                for (String day : getDaysNumber(ownerName,repoName,week)) {
                    ArrayList<Object> temp = new ArrayList<>();
                    String dayNumber = StringUtils.capitalize(day);
                    ArrayList<String> contentsCall= null;
                    try {
                        contentsCall = restFunctions.getCall(null, GitHubHelperUtil.getAuthKey(), RestFunctions.TOKEN_TYPE.OAUTH, getUrl(ownerName, repoName, week, day)).jsonPath().get("html_url");
                        temp.add(weekNumber);
                        temp.add(dayNumber);
                        short givenHW = homeWorkGiven.get(weekNumber + dayNumber);
                        counter.put(weekNumber, (short) (counter.get(weekNumber) + Math.min(contentsCall.size(), givenHW)));
                        temp.add(Math.min(contentsCall.size(), givenHW));
                        maxNumber = (short) Math.max(Math.min(contentsCall.size(), givenHW), maxNumber);
                    }catch (Exception n){
                        System.out.println("Incorrect week or day "+ week+" "+day);
                    }
                    temp.addAll(contentsCall != null ? contentsCall : new ArrayList<>());
                    auditData.add(temp);
                }
            }
            catch (Exception e){
                System.out.println("Failed for mentee "+ownerName+" with error "+e.getMessage());
            }
        }
        auditData.add(0, getMenteeSheetHeader(maxNumber));
    }

    private void clearAndUpdateSheet(String menteeName, List<List<Object>> auditData ){
        GoogleSheet.clearSheetRange(getSheetId(),menteeName+"!A:Z");
        GoogleSheet.updateSheet(getSheetId(),menteeName+"!A:Z", auditData);
    }

    private void getNonDSAUpdate(String ownerName, String repoName,  HashMap<String, Short> menteeCumulativeData,  List<List<Object>> auditData, String entity){
        List<Object> contentsCall = null;
        try{
            contentsCall = restFunctions.getCall(null, GitHubHelperUtil.getAuthKey(), RestFunctions.TOKEN_TYPE.OAUTH, getBaseUrl(ownerName, repoName)+entity.toLowerCase()).jsonPath().get("html_url");
            menteeCumulativeData.put(entity, (short)contentsCall.size());
            contentsCall.addAll(0, Arrays.asList(entity,"", contentsCall.size()));
        }catch (NullPointerException e){
            contentsCall = Arrays.asList(new Object[]{entity,"", 0});
            menteeCumulativeData.put(entity, (short) 0);
        }
        auditData.add(contentsCall);
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
        short index = 0;
        for(Object eachColumnHeader : GoogleSheet.getData(getSheetId(), getCompletionSheetId(), "C1", "AZ1").get(0))
            header.put((String)eachColumnHeader, index++);

        index = 0;
        for( List<Object> eachRow : GoogleSheet.getData(getSheetId(),  getCompletionSheetId(), "A3", "A"))
            menteeRowNumber.put((String)eachRow.get(0), index++);


        return new HashMap[]{header,menteeRowNumber};
    }

    private void getGivenHomeWork(HashMap<String, Short> homeWorkGiven){
        for(List<Object> eachRow : GoogleGenericFunctions.GoogleSheet.getData(getSheetId(), "DSA HW Question", "B2", "D")){
            homeWorkGiven.put(eachRow.get(0) +((String)eachRow.get(1)), Short.valueOf((String)eachRow.get(2)));
        }
    }

    String prefixHTML = "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            "table, th, td {\n" +
            "border: 1px solid black;\n" +
            "border-collapse: collapse;\n" +
            "}\n" +
            "th {\n" +
            "text-align: left;\n" +
            "font-weight: bold;\n" +
            "background-color: yellow;\n" +
            "}\n" +
            "\t\t .veryLow{\n" +
            "\t\t background-color: red;\n" +
            "\t\t }\n" +
            "\t\t .low{\n" +
            "\t\t background-color: pink;\n" +
            "\t\t }\n" +
            "\t\t .on-track{\n" +
            "\t\t background-color: green;\n" +
            "\t\t }\n" +
            "\t\t \n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<p> Hi All, <br><br> Please find the current completion status of HomeWork,<br><br></p>\n" +
            "<table style=\"width:100%\" align = left>",
            suffix = "</table>\n" +
                    "<p>&nbsp;</p>\n" +
                    "<p>Regards,<br> Nataraaj</p>\n" +
                    "<body>\n" +
                    "</html>";
//            toEmail = "testleaf-sdet-batch-4-mentors@googlegroups.com",
//            fromEmail ="nataraajshanmugam08@gmail.com";

    private String buildHomeWorkStatusHtml(){
        List<List<Object>> data = GoogleGenericFunctions.GoogleSheet.getData(getSheetId(), "Completion Status");

        StringBuilder builder = new StringBuilder(prefixHTML);
        builder.append("<tr>");
        for(Object each:data.get(0))
            builder.append("<th>").append(each).append("</th>");
        builder.append("</tr>");

        float homeWorkNumbers = 0;

        for( int i = 2; i < data.get(1).size(); i++)
            homeWorkNumbers+= Short.valueOf((String) data.get(1).get(i));

        for( int i = 2; i < data.size(); i++) {
            builder.append("<tr>");
            for( int j = 0; j < data.get(i).size(); j++) {
                String ifAnyClass = "";
                if(j == 1){
                    if((homeWorkNumbers/100*20) <= Short.valueOf((String)data.get(i).get(j)))
                        ifAnyClass =" class=\"veryLow\"";
                    else if((homeWorkNumbers/100*10) <= Short.valueOf((String)data.get(i).get(j)))
                        ifAnyClass =" class=\"low\"";
                    else
                        ifAnyClass =" class=\"on-track\"";
                }
                try {
                    builder.append("<td" + ifAnyClass + ">").append(data.get(i).get(j)).append("</td>");
                }catch (Exception e){
                    System.out.println(e);
                }
            }
            builder.append("</tr>");
        }
        return builder.append(suffix).toString();
    }

    @Test(priority = 1)
    private void auditData() throws MessagingException, IOException {
        HashMap[] statusSheetDetails = setStatusSheetHeaderAndMenteeRowNumber();
        HashMap<String, Short> statusSheetHeader = statusSheetDetails[0];
        HashMap<String, Short> menteeRowNumber = statusSheetDetails[1];
        List<List<Object>> updateSheetData = new ArrayList(Collections.nCopies(menteeRowNumber.size(), 0));
        HashMap<String, Short> homeWorkGiven = new HashMap<>();
        getGivenHomeWork(homeWorkGiven);
        for (Map.Entry<String, List<Object>> eachMentee : getMenteeDetailsData().entrySet()) {
            try {
                if (eachMentee.getValue().size() > 2 && !((String) eachMentee.getValue().get(2)).trim().isEmpty()) {
                    String ownerName = (String) eachMentee.getValue().get(2),
                            repoName = (String) eachMentee.getValue().get(3),
                            menteeName = (String) eachMentee.getValue().get(1);
                    HashMap<String, Short> menteeCumulativeData = new HashMap<>();
                    List<List<Object>> auditData = new ArrayList<>();
                    getNonDSAUpdate(ownerName, repoName, menteeCumulativeData, auditData, "Selenium");
                    getNonDSAUpdate(ownerName, repoName, menteeCumulativeData, auditData, "TCE");
                    getNonDSAUpdate(ownerName, repoName, menteeCumulativeData, auditData, "API");
                    System.out.println(menteeName);
                    getDSAAndUpdate(ownerName, repoName, menteeCumulativeData, auditData, homeWorkGiven);
                    clearAndUpdateSheet(menteeName, auditData);

                    //update sheet curation
                    List<Object> menteeData = new ArrayList<>(Collections.nCopies(statusSheetHeader.size(), 0));

                    for (Map.Entry<String, Short> eachEntity : menteeCumulativeData.entrySet())
                        try{
                            menteeData.set(statusSheetHeader.get(eachEntity.getKey()), eachEntity.getValue());
                        }catch (Exception e){
                            System.out.println(e);
                        }

                    updateSheetData.set(menteeRowNumber.get(menteeName), menteeData);
                } else {
                    System.out.println("missing details for " + eachMentee.getKey());
                    updateSheetData.set(menteeRowNumber.get((String) eachMentee.getValue().get(1)), new ArrayList<>(Collections.nCopies(statusSheetHeader.size(), 0)));
                }
            }catch(Exception e){
                System.out.println("Failed for user "+ eachMentee);
                updateSheetData.set(menteeRowNumber.get((String) eachMentee.getValue().get(1)), new ArrayList<>(Collections.nCopies(statusSheetHeader.size(), 0)));
            }
        }

        for(List<Object> eachData : updateSheetData)
            System.out.println(eachData);
        GoogleSheet.clearAndUpdateSheet(getSheetId(),"'"+ getCompletionSheetId()+"'!C3:"+((char)('A'+statusSheetHeader.size()+1)), updateSheetData);
        GoogleGenericFunctions.GMail.sendEmail("Test email for git Audit", buildHomeWorkStatusHtml() , getFromEmailId(), getToEmailId());
    }
}
