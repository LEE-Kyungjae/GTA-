package com.mybot;import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessFinder {
    public static int findProcessId(String processName) {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(processName)) {
                    String[] splitLine = line.split("\\s+");
                    return Integer.parseInt(splitLine[1]);  // 프로세스 ID 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
