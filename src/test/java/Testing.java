import google.GoogleGenericFunctions;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class Testing {

    String  toEmail = "testleaf-sdet-batch-4-mentors@googlegroups.com",
            fromEmail ="nataraajshanmugam08@gmail.com";
    @Test
    public void sendEmail() throws MessagingException, IOException {
        List<List<Object>> data = GoogleGenericFunctions.GoogleSheet.getData("17oVcButb1QtnjAAkJn9KYHmCV7wjkREv44dcMPxD7fA", "Completion Status");
        System.out.println(data);
//        GoogleGenericFunctions.GMail.sendEmail("Test email for git Audit", "Testing using git actions" , fromEmail, fromEmail);
    }


}
