package com.mybot;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinDef.DWORD;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessControl {
    private static final Logger logger = Logger.getLogger(ProcessControl.class.getName());
    // 스레드를 중지 및 재개하는 데 필요한 권한
    private static final int THREAD_SUSPEND_RESUME = 0x0002;

    public static HANDLE getThreadHandle(int pid) {
        Kernel32.ThreadEntry32 threadEntry = new Kernel32.ThreadEntry32();
        threadEntry.dwSize = new DWORD(threadEntry.size());

        HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(new DWORD(0x00000004), new DWORD(pid));

        if (snapshot == null || WinNT.INVALID_HANDLE_VALUE.equals(snapshot)) {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            logger.log(Level.SEVERE, "Failed to create snapshot for process with PID: {0}. Error code: {1}",
                    new Object[]{pid, errorCode});
            return null;
        }

        if (Kernel32.INSTANCE.Thread32First(snapshot, threadEntry)) {
            do {
                if (threadEntry.th32OwnerProcessID.intValue() == pid) {
                    // OpenThread를 사용하여 스레드 핸들을 가져옵니다.
                    HANDLE threadHandle = Kernel32.INSTANCE.OpenThread(THREAD_SUSPEND_RESUME, false, threadEntry.th32ThreadID.intValue());
                    Kernel32.INSTANCE.CloseHandle(snapshot);
                    return threadHandle;
                }
            } while (Kernel32.INSTANCE.Thread32Next(snapshot, threadEntry));
        } else {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            logger.log(Level.SEVERE, "Failed to retrieve first thread for process with PID: {0}. Error code: {1}",
                    new Object[]{pid, errorCode});
        }

        Kernel32.INSTANCE.CloseHandle(snapshot);
        logger.log(Level.SEVERE, "No threads found for process with PID: {0}", pid);
        return null;
    }

    public static boolean suspendProcess(int pid) {
        logger.log(Level.INFO, "Attempting to suspend process with PID: {0}", pid);
        HANDLE threadHandle = getThreadHandle(pid);

        if (threadHandle == null) {
            logger.log(Level.SEVERE, "Failed to get thread handle for PID: {0}", pid);
            return false;
        }

        int result = Kernel32.INSTANCE.SuspendThread(threadHandle);
        Kernel32.INSTANCE.CloseHandle(threadHandle);

        if (result < 0) {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            logger.log(Level.SEVERE, "Failed to suspend thread for process with PID: {0}. Error code: {1}",
                    new Object[]{pid, errorCode});
            return false;
        }

        logger.log(Level.INFO, "Successfully suspended process with PID: {0}. Result: {1}", new Object[]{pid, result});
        return true;
    }

    public static boolean resumeProcess(int pid) {
        logger.log(Level.INFO, "Attempting to resume process with PID: {0}", pid);
        HANDLE threadHandle = getThreadHandle(pid);
        if (threadHandle == null) {
            logger.log(Level.SEVERE, "Failed to get thread handle for PID: {0}", pid);
            return false;
        }

        int result = Kernel32.INSTANCE.ResumeThread(threadHandle);
        Kernel32.INSTANCE.CloseHandle(threadHandle);

        if (result < 0) {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            logger.log(Level.SEVERE, "Failed to resume thread for process with PID: {0}. Error code: {1}",
                    new Object[]{pid, errorCode});
            return false;
        }

        logger.log(Level.INFO, "Successfully resumed process with PID: {0}", pid);
        return true;
    }
}
