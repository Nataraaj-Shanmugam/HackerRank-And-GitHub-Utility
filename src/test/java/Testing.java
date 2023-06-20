import google.GoogleGenericFunctions;
import org.testng.annotations.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Testing {

    String prefixHTML = "<html><head><style>\n" +
            "table, th, td {\n" +
            "  border: 1px solid black;\n" +
            "  border-collapse: collapse;\n" +
            "}\n" +
            "</style></head><body><p> Hi All, <br><br> Please find the current completion status of HomeWork,<br><br></p>",
            suffix = "<p>Regards,<br> Nataraaj</p><body></html>";

    @Test
    private void buildHtml(){
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("Name","HM yet to Complete","Selenium","API","TCE","Week1","Week2","Week3","Week4","Week5","Week6","Week7","Week8","Week9","Week10"));
        data.add(Arrays.asList("R.BHARATH","24","6","0","0","18","11","12","6","5","4","0","0","0","0"));
        data.add(Arrays.asList("Sathish","86","0","0","0","0","0","0","0","0","0","0","0","0","0"));

        StringBuilder builder = new StringBuilder();
        builder.append("<table style=\"width:100%\">");
        builder.append("<tr>");
        for(String each:data.get(0))
            builder.append("<th>").append(each).append("</th>");
        builder.append("</tr>");

        builder.append("<tr>");
        for(String each:data.get(1))
            builder.append("<td>").append(each).append("</td>");
        builder.append("</tr>");

        builder.append("<tr>");
        for(String each:data.get(2))
            builder.append("<td>").append(each).append("</td>");
        builder.append("</tr>");
        builder.append("</table>");

        System.out.println(builder.toString());

    }

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


    String sheetId = "17oVcButb1QtnjAAkJn9KYHmCV7wjkREv44dcMPxD7fA";

    //    @Test
    private void setStatusSheetHeaderAndMenteeRowNumber(){
        HashMap<String, Short> header = new HashMap<>();
        HashMap<String, Short> menteeRowNumber = new HashMap<>();
        short index = 0;
        for(Object eachColumnHeader : GoogleGenericFunctions.GoogleSheet.getData(sheetId, "Completion Status", "B1", "AC1").get(0))
            header.put((String)eachColumnHeader, index++);

        index = 0;
        for( List<Object> eachRow : GoogleGenericFunctions.GoogleSheet.getData(sheetId, "Completion Status", "A3", "A"))
            menteeRowNumber.put((String)eachRow.get(0), index++);

        System.out.println(header);
        System.out.println(menteeRowNumber);
        System.out.println(Arrays.deepToString(new HashMap[]{header,menteeRowNumber}));
    }

}
