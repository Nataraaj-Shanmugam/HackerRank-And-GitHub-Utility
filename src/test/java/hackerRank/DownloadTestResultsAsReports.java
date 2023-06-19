package hackerRank;

import genericFunctions.RestFunctions;
import groupData.GroupInformation;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DownloadTestResultsAsReports extends RestFunctions {


    static String testID = "1596357",
            key = "Bearer 8b86284be493b95a2daf1951768062a3abb2b6a00f8259287ebfdc6654f28516",
            path = "C:\\Users\\Others\\Desktop\\Assessment Report\\"+testID;

    //todo - optimise below code
    private ChromeOptions setCustomCapabilities(){
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.default_directory", path);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--headless");
        return options;
    }

    //todo - put questions, answers as well
    @Test
    public void downloadReport() throws InterruptedException {
        ChromeDriver driver = new ChromeDriver(setCustomCapabilities());

        HashMap<String, String> userData = new GroupInformation().getGroupData(GroupInformation.BATCH.FOUR);
        for(  LinkedHashMap<String, String> each : getCandidateIds()) {
            String downloadUrl = getDownloadURL(each.get("id"));
            driver.get(downloadUrl);
            Path downloadFile = null;
            int count = 1;
            do{
                try {
                    downloadFile = Paths.get(path + "\\" + downloadUrl.split("filename%3D")[1].split("&")[0]);
                }catch (Exception e){
                    count++;
                }
            }while (downloadFile== null && count < 3);
            if(downloadFile == null) {
                System.out.println("unable to download file for " + each.get("full_name"));
                continue;
            }
            Thread.sleep(1000);
            moveFileToGroupPath(downloadFile, each.get("full_name"), userData.get(each.get("full_name")));

        }

        driver.quit();
    }

    private ArrayList<LinkedHashMap> getCandidateIds(){
        return getJsonResponse(getCall(null, key, TOKEN_TYPE.TOKEN, "https://www.hackerrank.com/x/api/v3/tests/"+testID+"/candidates?limit=40&offset=0&fields=id,full_name")).get("data");
    }

    private String getDownloadURL(String candidateId){
        return getCall(null, key, TOKEN_TYPE.TOKEN, "https://www.hackerrank.com/x/api/v3/tests/"+testID+"/candidates/"+candidateId+"/pdf?format=url").asPrettyString();
    }

    private void moveFileToGroupPath(Path source, String name, String group){
        try {
            Path dest =Paths.get(source.getParent()+ "\\"+group+"\\"+name+".pdf");
            if(dest.getParent() != null && Files.notExists(dest.getParent()))
                Files.createDirectories(dest.getParent());

            Files.move(source,dest , StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
