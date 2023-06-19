package groupData;

import java.util.HashMap;

public class GroupInformation {

    public enum BATCH{
        FOUR,
        FIFTH
    }

    public static String[][] batch4 = new String[][]{
            {"Gangadaran S", "Elizabeth Anburaj","Shankar","Karthikeyan G"},
            {"Barathi Priya", "Sathish", "Karupasami P", "Krishna Bharathi M", "Devipriya Raja"},
            {"Subash Prasad","Eswaran Anantharaman","Abilash","D SIVANANDA","Hemamalini G","Shilviya jayaraj"},
            {"VigneshKumar Tamilselvan","Priyanka G","R.BHARATH","Uma","Aravind Swamy","Bhuvanesh K A"},
            {"JOTHEESH KUMAR M","Nawin laxman","Pradeep S","Mohammed Asif Pakkirsa","Anish Nesaiyan","Krishnakumar"}
    };

    public static HashMap<String, String> groupData = new HashMap<>();
    public HashMap<String, String> getGroupData(BATCH batch){
        if(batch == BATCH.FOUR) setBatch4();
        return groupData;
    }

    private void setBatch4(){
        groupData.put("Gangadaran S","Group 1");
        groupData.put("Elizabeth Anburaj","Group 1");
        groupData.put("Barathi Priya","Group 2");
        groupData.put("Shankar","Group 1");
        groupData.put("Karthikeyan G","Group 1");
        groupData.put("Hema Murugesan","Group 2");
        groupData.put("Sathish","Group 2");
        groupData.put("Karupasami P","Group 2");
        groupData.put("Krishna Bharathi M","Group 2");
        groupData.put("Devipriya Raja","Group 2");
        groupData.put("Subash Prasad","Group 3");
        groupData.put("Eswaran Anantharaman","Group 3");
        groupData.put("Abilash","Group 3");
        groupData.put("D SIVANANDA","Group 3");
        groupData.put("Hemamalini G","Group 3");
        groupData.put("Shilviya jayaraj","Group 3");
        groupData.put("VigneshKumar Tamilselvan","Group 4");
        groupData.put("Priyanka G","Group 4");
        groupData.put("R.BHARATH","Group 4");
        groupData.put("Uma","Group 4");
        groupData.put("Aravind Swamy","Group 4");
        groupData.put("Bhuvanesh K A","Group 4");
        groupData.put("JOTHEESH KUMAR M","Group 5");
        groupData.put("Nawin laxman","Group 5");
        groupData.put("Pradeep S","Group 5");
        groupData.put("Mohammed Asif Pakkirsa","Group 5");
        groupData.put("Anish Nesaiyan","Group 5");
        groupData.put("Krishnakumar","Group 5");
    }

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        for(int i =0; i < batch4.length; i++){
            for(int j =0; j <batch4[i].length;j++){
                map.put(batch4[i][j], "Group "+i);
                System.out.println("groupData.put(\""+batch4[i][j]+"\",\"Group "+(i+1)+"\");");
            }
        }
        System.out.println(map);
    }
}
