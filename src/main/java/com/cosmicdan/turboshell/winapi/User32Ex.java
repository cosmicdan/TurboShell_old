package com.cosmicdan.turboshell.winapi;

import com.sun.jna.*;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import lombok.extern.log4j.Log4j2;

/**
 * Contains User32 methods as a direct mapping instead of default interface mapping (for performance
 * reasons). For TurboShell, many natives are package private because they're wrapped by other classes.
 */
@Log4j2
@SuppressWarnings({"MethodParameterNamingConvention", "NativeMethodNamingConvention", "QuestionableName", "NativeMethod", "UnusedReturnValue"})
public class User32Ex {
	public static final User32Ex INSTANCE;

	// TODO: Javadoc (just link to originals like I have already done for some)
	/* simple natives without wrappers */

	/** See {@link User32#GetMessage} */
	public native int GetMessageW(WinUser.MSG lpMsg, WinDef.HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);
	public native WinDef.BOOL GetWindowPlacement(WinDef.HWND hwnd, WinUser.WINDOWPLACEMENT lpwndpl);
	/** See {@link User32#TranslateMessage} */
	public native boolean TranslateMessage(WinUser.MSG lpMsg);
	public native WinDef.LRESULT DispatchMessageW(WinUser.MSG lpMsg);
	/** Sets the show state of a window without waiting for the operation to complete */
	public native boolean ShowWindowAsync(WinDef.HWND hWnd, int nCmdShow);
	/** Places (posts) a message in the message queue without waiting for the thread to process the message */
	// CRASHES. Disabled for now. This has ben fixed in JNA master and should be included in the next release (after 4.5.1)
	//public native void PostMessageW(WinDef.HWND hWnd, int msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
	public native WinDef.HMENU GetSystemMenu(WinDef.HWND hWnd, boolean bRevert);
	public native int GetMenuState(WinDef.HMENU hMenu, int uId, int uFlags);
	public native boolean GetMenuItemInfoW(WinDef.HMENU hMenu, int uItem, boolean fByPosition, WinUser.MENUITEMINFO lpmii);
	public native boolean GetWindowRect(WinDef.HWND hWnd, WinDef.RECT rect);
	public native WinDef.HWND GetDesktopWindow();
	public native int GetWindowTextLengthW(WinDef.HWND hWnd);
	public native int GetWindowTextW(WinDef.HWND hWnd, char[] lpString, int nMaxCount);
	public native boolean IsWindow(WinDef.HWND hWnd);
	public native WinDef.HWND GetAncestor(WinDef.HWND hwnd, int gaFlags);
	public native WinDef.HWND GetWindow(WinDef.HWND hWnd, int uCmd);
	public native boolean IsWindowVisible(WinDef.HWND hWnd);

	/** See {@link User32#RegisterClassEx} */
	public native WinDef.ATOM RegisterClassExW(WinUser.WNDCLASSEX lpwcx) throws LastErrorException;

	public native WinDef.LRESULT CallWindowProcW(Pointer proc, WinDef.HWND hWnd, int uMsg, WinDef.WPARAM uParam, WinDef.LPARAM lParam);

	/* wrapped natives */
	native boolean SystemParametersInfoW(int uiAction, int uiParam, Structure pvParam, int fWinIni);
	native WinNT.HANDLE SetWinEventHook(int eventMin, int eventMax, WinDef.HMODULE hmodWinEventProc, WinUser.WinEventProc winEventProc, int processID, int threadID, int flags);
	native BaseTSD.LONG_PTR GetWindowLongPtrW(WinDef.HWND hWnd, int nIndex);
	public native BaseTSD.LONG_PTR SetWindowLongPtrW(WinDef.HWND hWnd, int nIndex, Callback wndProc) throws LastErrorException;

	static {
		INSTANCE = new User32Ex();
		Native.register("user32");
	}


}
