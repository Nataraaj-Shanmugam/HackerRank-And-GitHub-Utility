import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Testing {

    String  toEmail = "testleaf-sdet-batch-4-mentors@googlegroups.com",
            fromEmail ="nataraajshanmugam08@gmail.com";
    @Test
    public void sendEmail() throws MessagingException, IOException {
        findMatrix(new int[]{1,3,4,1,2,3,1});
    }
    public List<List<Integer>> findMatrix(int[] nums) {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        for(int each : nums)
            map.put(each, map.getOrDefault(each , 0)+1);
        List<List<Integer>> output = new ArrayList<>();
        while(map.size() > 0){
            List<Integer> temp = new ArrayList<>();
            for(int each : map.keySet()){
                temp.add(each);
                if(map.get(each) == 1) map.remove(each);
                else map.put(each, map.get(each)-1);
            }
            output.add(temp);
        }
        return output;
    }
}
