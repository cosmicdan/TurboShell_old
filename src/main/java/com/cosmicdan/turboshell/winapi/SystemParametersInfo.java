package com.cosmicdan.turboshell.winapi;

import com.sun.jna.Structure;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * See <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms724947(v=vs.85).aspx">SystemParametersInfo at MSDN</a>
 */
@Log4j2
public class SystemParametersInfo {
	public static final int SPI_SETWORKAREA = 0x002F;
	public static final int SPIF_UPDATEINIFILE = 0x0001;
	public static final int SPIF_SENDWININICHANGE = 0x0002;

	@Getter
	private final boolean success;

	public SystemParametersInfo(int uiAction, int uiParam, Structure pvParam, int fWinIni) {
		success = User32Ex.INSTANCE.SystemParametersInfoW(uiAction, uiParam, pvParam, fWinIni);
	}
}
