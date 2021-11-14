package kcp;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class KCPConfig {
    public void createConfigFile(String filePath) {
        HashMap<String, String> defaultConfig = new HashMap<>();
        defaultConfig.put("localaddr", ":7584");
        defaultConfig.put("remoteaddr", "127.0.0.1:5684");
        defaultConfig.put("key", "myEpicKey");
        defaultConfig.put("crypt", "aes-192");
        defaultConfig.put("mode", "fast3");
        JSONObject jsonObject = new JSONObject(defaultConfig);
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
