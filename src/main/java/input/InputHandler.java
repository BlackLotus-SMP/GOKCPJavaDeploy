package input;

import kcp.KCPExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputHandler {
    public static void processConsoleInput() {
        System.out.println("Starting console input handler...");
        while (true) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String command = bufferedReader.readLine().trim().toLowerCase();
                System.out.println(command);
                if (command.equals("stopkcp")) {
                    if (KCPExecutor.process != null) {
                        KCPExecutor.process.destroyForcibly();
                    }
                    break;
                } else if (command.equals("help")) {
                    System.out.println("stopkcp -> stop the process.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
