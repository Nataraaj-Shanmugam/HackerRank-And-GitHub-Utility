package google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GoogleGenericFunctions {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/Credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {


        InputStream in = GoogleGenericFunctions.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        List<String> scopes = new ArrayList<>();
        scopes.addAll(SheetsScopes.all());
        scopes.add(GmailScopes.GMAIL_SEND);
        return new AuthorizationCodeInstalledApp(
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in)), scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build()
                , receiver).authorize("user");
    }

    private static Object getService(String modules) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            if(modules.equals("Sheets"))
                return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName("SDET_Audit").build();
            else if(modules.equals("Gmail"))
                return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName("SDET_Audit").build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class GoogleSheet {
        private static Sheets getSheetService(){
            return (Sheets)GoogleGenericFunctions.getService("Sheets");
        }

        private static Sheets.Spreadsheets.Values getSheetData() {
            return getSheetService().spreadsheets().values();
        }

        public static List<List<Object>> getData(String sheetId, String sheetName, String startCell, String endCell) {
            try {
                String range = (startCell.trim().isEmpty() && endCell.trim().isEmpty()) ?
                        sheetName :
                        endCell.trim().isEmpty() ? sheetName + "!" + startCell :
                                sheetName + "!" + startCell + ":" + endCell;
                return getSheetData()
                        .get(sheetId, range)
                        .execute().getValues();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void createSheet(String sheetId, String sheetName) {
            try {
                getSheetService().spreadsheets().batchUpdate(sheetId,
                        new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(sheetName)))))
                ).execute().getSpreadsheetId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void createSheets(String sheetId, List<String> sheetNames) {
            try {
                ArrayList<Request> requests = new ArrayList<>();
                for (String sheetName : sheetNames)
                    requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(sheetName))));
                getSheetService().spreadsheets().batchUpdate(sheetId,
                        new BatchUpdateSpreadsheetRequest().setRequests(requests)
                ).execute().getSpreadsheetId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void clearSheetRange(String sheetId, String range) {
            try {
                BatchClearValuesRequest request = new BatchClearValuesRequest();
                request.setRanges(Collections.singletonList(range));
                getSheetService().spreadsheets().values().batchClear(sheetId, request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void updateSheet(String sheetId, String range, List<List<Object>> dataToUpdate) {
            try {
                BatchUpdateValuesRequest requestBody = new BatchUpdateValuesRequest();
                requestBody.setValueInputOption("USER_ENTERED");
                requestBody.setData(Collections.singletonList(new ValueRange().setRange(range).setValues(dataToUpdate)));
                getSheetService().spreadsheets().values().batchUpdate(sheetId, requestBody).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void clearAndUpdateSheet(String sheetId, String range, List<List<Object>> dataToUpdate) {
            clearSheetRange(sheetId,range);
            updateSheet(sheetId, range, dataToUpdate);
        }

        public static List<List<Object>> getData(String sheetId, String sheetName, String cell) {
            return getData(sheetId, sheetName, cell, "");
        }

        public static List<List<Object>> getData(String sheetId, String sheetName) {
            return getData(sheetId, sheetName, "", "");
        }

        public static HashSet<String> getAllSheetName(String sheetId) {
            HashSet<String> sheetNames = new HashSet<>();
            try {
                for (Sheet sheet : getSheetService().spreadsheets().get(sheetId).execute().getSheets())
                    sheetNames.add(sheet.getProperties().getTitle());
                sheetNames.remove("Master");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sheetNames;
        }
    }

    public static class GMail{

        private static Gmail getMailService(){
            return (Gmail)GoogleGenericFunctions.getService("Gmail");
        }

        public static void sendEmail(String subject, String message, String from, String to) throws MessagingException, IOException {

            Properties props = new Properties();
            MimeMessage email = new MimeMessage(Session.getDefaultInstance(props));
            email.addRecipients(Message.RecipientType.TO, to);
            email.setFrom(from);
            email.setSubject(subject);
            email.setContent(message, "text/html; charset=utf-8");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMsg = buffer.toByteArray();
            String encodeEmail = Base64.encodeBase64URLSafeString(rawMsg);
            com.google.api.services.gmail.model.Message msg = new com.google.api.services.gmail.model.Message();
            msg.setRaw(encodeEmail);

            try{
                msg = getMailService().users().messages().send("me", msg).execute();
                Assert.assertTrue(msg.getLabelIds().contains("SENT"));
            }catch (GoogleJsonResponseException e){
                System.out.println(e.getDetails());
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

}