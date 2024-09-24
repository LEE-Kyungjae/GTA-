package com.mybot;
import com.sun.jna.Native;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.util.Arrays;
import java.util.List;

public interface Kernel32 extends Library {
    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

    HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
    boolean CloseHandle(HANDLE hObject);

    // 스레드 핸들 관련 함수
    HANDLE OpenThread(int dwDesiredAccess, boolean bInheritHandle, int dwThreadId);
    int SuspendThread(HANDLE hThread);
    int ResumeThread(HANDLE hThread);

    HANDLE CreateToolhelp32Snapshot(DWORD dwFlags, DWORD th32ProcessID);
    boolean Thread32First(HANDLE hSnapshot, ThreadEntry32 lpte);
    boolean Thread32Next(HANDLE hSnapshot, ThreadEntry32 lpte);

    // 스레드에 대한 정보 구조체 정의
    class ThreadEntry32 extends Structure {
        public DWORD dwSize;
        public DWORD cntUsage;
        public DWORD th32ThreadID;
        public DWORD th32OwnerProcessID;
        public DWORD tpBasePri;
        public DWORD tpDeltaPri;
        public DWORD dwFlags;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwSize", "cntUsage", "th32ThreadID", "th32OwnerProcessID", "tpBasePri", "tpDeltaPri", "dwFlags");
        }
    }

    int GetLastError();
}