package com.cosmicdan.turboshell.winapi;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import lombok.extern.log4j.Log4j2;

/**
 * Contains User32 methods as a direct mapping instead of default interface mapping (for performance
 * reasons). For TurboShell, many natives are package private because they're wrapped by other classes.
 */
@Log4j2
@SuppressWarnings({"MethodParameterNamingConvention", "NativeMethodNamingConvention", "QuestionableName", "NativeMethod"})
public class User32Ex {
	public static final User32Ex INSTANCE;

	/* simple natives */
	public native int GetMessageW(WinUser.MSG lpMsg, WinDef.HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);

	/* wrapped natives */
	native int MessageBoxW(WinDef.HWND hWnd, WString text, WString lpCaption, int uType);
	native boolean SystemParametersInfoW(int uiAction, int uiParam, Structure pvParam, int fWinIni);
	native WinNT.HANDLE SetWinEventHook(int eventMin, int eventMax, WinDef.HMODULE hmodWinEventProc, WinUser.WinEventProc winEventProc, int processID, int threadID, int flags);




	static {
		INSTANCE = new User32Ex();
		Native.register("user32");
	}
}
