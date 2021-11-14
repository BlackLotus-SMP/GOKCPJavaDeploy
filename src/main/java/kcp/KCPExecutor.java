package kcp;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class KCPExecutor {
    public void runBinWithConfig(String dataDir, String binPath, String configPath, String os) {
        Process process;
        String[] command = {os.equals("linux") ? String.format("./%s", binPath) : binPath, "-c", configPath};
        try {
            process = new ProcessBuilder(command).directory(new File(dataDir)).redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream())) {
                int c;
                while ((c = inputStreamReader.read()) >= 0) {
                    System.out.print((char) c);
                    System.out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
