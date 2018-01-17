package com.cosmicdan.turboshell.winapi;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;

import java.util.Arrays;
import java.util.List;

public interface WinUser extends com.sun.jna.platform.win32.WinUser {
	int SC_RESTORE = 0xF120;


	class MENUITEMINFO extends Structure {
		/** fMask values **/
		public static final int MIIM_STATE = 0x1;

		/** fState values **/
		public static final int MFS_DISABLED = 0x3;

		public int cbSize = size();
		public int fMask;
		public int fType;
		public int fState;
		public int wID;
		public WinDef.HMENU hSubMenu;
		public WinDef.HBITMAP hbmpChecked;
		public WinDef.HBITMAP hbmpUnchecked;
		public BaseTSD.ULONG_PTR dwItemData;
		public String dwTypeData;
		public int cch;
		public WinDef.HBITMAP hbmpItem;

		public MENUITEMINFO(int fMask) {
			this.fMask = fMask;
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList("cbSize", "fMask", "fType",
					"fState", "wID", "hSubMenu", "hbmpChecked",
					"hbmpUnchecked", "dwItemData", "dwTypeData", "cch",
					"hbmpItem");
		}
	}
}
