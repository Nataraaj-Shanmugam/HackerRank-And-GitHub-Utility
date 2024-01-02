package gitHubHelper;

import genericFunctions.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInformation {

    public enum BATCH{
        FOUR("SDET 4"),
        FIFTH("SDET 5");

        private final String value;

        // Constructor
        BATCH(String value) {
            this.value = value;
        }

        // Getter
        public String getValue() {
            return value;
        }
    }

    public static HashMap<String, String> groupData = new HashMap<>();

    public HashMap<String, String> getGroupData(BATCH batch){
        for(Map.Entry<String, Object> map :  new Helpers().getSDETData(batch.value).entrySet()){
            for(String names : (List<String>)map.getValue())
                groupData.put(names, map.getKey());
        }
        return groupData;
    }
}
