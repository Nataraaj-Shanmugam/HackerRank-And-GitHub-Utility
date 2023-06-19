import google.GoogleGenericFunctions;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Testing {

    //    @Test
    public void test(){
        try {
            String body = "<html><body>" +
                    "<button>Testing</button>" +
                    "</body></html>";
            GoogleGenericFunctions.GMail.sendEmail("Test Subject", body, "nataraajshanmugam08@gmail.com", "nat81192@gmail.com,nataraajshanmugam08@gmail.com");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    private void temp(){
        List<List<Object>> data = GoogleGenericFunctions.GoogleSheet.getData("17oVcButb1QtnjAAkJn9KYHmCV7wjkREv44dcMPxD7fA", "DSA HW Question", "B2", "B");
        HashMap<String, Short> weekData = new HashMap<>();
        short index = 1;
        for(List<Object> a : data){
            if(!weekData.containsKey((String)a.get(0)))
                weekData.put((String)a.get(0), index++);
        }
        System.out.println(weekData);
    }
}
