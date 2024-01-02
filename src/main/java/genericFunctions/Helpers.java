package genericFunctions;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class Helpers {
    private static final String GROUPS_FILE_PATH = "G:\\Mentoring\\TestLeaf\\Utilities\\src\\main\\resources\\Groups.yaml";

    private Object readYaml (String yamlFile){
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Paths.get(yamlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        return  yaml.load(inputStream);
    }

    public Map<String, Object> getSDETData(String batch){
        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) readYaml(GROUPS_FILE_PATH);
        for(Map<String, Object> eachBatch: list){
            if(eachBatch.get("Batch").equals(batch)){
                return (Map<String, Object>) eachBatch.get("Groups");
            }
        }
        return null;
    }

    public Properties readProperties(String path){
        Properties prop = new Properties();
        try {
            InputStream input = new FileInputStream(path);
            // load the properties file
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
