package kcp;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class KCPExecutor {
    public static Process process;
    public void runBinWithConfig(String dataDir, String binPath, String configPath, String os) {
        String[] command = {!os.equals("windows") ? String.format("./%s", binPath) : binPath, "-c", configPath};
        try {
            process = new ProcessBuilder(command).directory(new File(dataDir)).redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream())) {
            int c;
            while ((c = inputStreamReader.read()) >= 0) {
                System.out.print((char) c);
                System.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
