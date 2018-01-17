package com.cosmicdan.turboshell.winapi;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * This class acts as a friendly wrapper for calling JNA MessageBox natives used by TurboShell
 */
@Log4j2
public class MessageBox {
	// button choices
	public static final int MB_ABORTRETRYIGNORE = 0x00000002;
	public static final int MB_CANCELTRYCONTINUE = 0x00000006;
	public static final int MB_HELP = 0x00004000;
	public static final int MB_OK = 0x00000000;
	public static final int MB_OKCANCEL = 0x00000001;
	public static final int MB_RETRYCANCEL = 0x00000005;
	public static final int MB_YESNO = 0x00000004;
	public static final int MB_YESNOCANCEL = 0x00000003;
	// icon
	public static final int MB_ICONEXCLAMATION = 0x00000030;
	public static final int MB_ICONWARNING = 0x00000030;
	public static final int MB_ICONINFORMATION = 0x00000040;
	public static final int MB_ICONASTERISK = 0x00000040;
	public static final int MB_ICONQUESTION = 0x00000020;
	public static final int MB_ICONSTOP = 0x00000010;
	public static final int MB_ICONERROR = 0x00000010;
	public static final int MB_ICONHAND = 0x00000010;
	// default button
	public static final int MB_DEFBUTTON1 = 0x00000000;
	public static final int MB_DEFBUTTON2 = 0x00000100;
	public static final int MB_DEFBUTTON3 = 0x00000200;
	public static final int MB_DEFBUTTON4 = 0x00000300;
	// modality
	public static final int MB_APPLMODAL = 0x00000000;
	public static final int MB_SYSTEMMODAL = 0x00001000;
	public static final int MB_TASKMODAL = 0x00002000;
	// other options
	public static final int MB_DEFAULT_DESKTOP_ONLY = 0x00020000;
	public static final int MB_RIGHT = 0x00080000;
	public static final int MB_RTLREADING = 0x00100000;
	public static final int MB_SETFOREGROUND = 0x00010000;
	public static final int MB_TOPMOST = 0x00040000;
	public static final int MB_SERVICE_NOTIFICATION = 0x00200000;
	// return values
	public static final int MB_IDABORT = 3;
	public static final int MB_IDCANCEL = 2;
	public static final int MB_IDCONTINUE = 11;
	public static final int MB_IDIGNORE = 5;
	public static final int MB_IDNO = 7;
	public static final int MB_IDOK = 1;
	public static final int MB_IDRETRY = 4;
	public static final int MB_IDTRYAGAIN = 10;
	public static final int MB_IDYES = 6;

	@Getter
	private final int result;

	/**
	 * Display a messagebox.<br/>
	 * @param hWnd Handle to the owner window. Can be null.
	 * @param title Title of the messagebox.
	 * @param text Text content of the messagebox.
	 * @param uType Flags to use. Any combination of MB_* constants in this class.
	 * @return One of the MB_ID* constants indicating the user action. Note that IDCANCEL is the result of ESC (if the box has a Cancel button).
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms645505(v=vs.85).aspx">MessageBox at MSDN</a>
	 */
	public MessageBox(WinDef.HWND hWnd, String title, String text, int uType) {
		WString wText = new WString(text);
		WString wLpCaption = new WString(title);
		result = User32Ex.INSTANCE.MessageBoxW(hWnd, wText, wLpCaption, uType);
	}
}
