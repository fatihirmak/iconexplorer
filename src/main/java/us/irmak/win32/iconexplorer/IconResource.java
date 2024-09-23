package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.EnumResNameProc;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HRSRC;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinUser;

import us.irmak.win32.iconexplorer.jna.Shell32Extension;
import us.irmak.win32.iconexplorer.jna.User32Extension;
import us.irmak.win32.iconexplorer.jna.User32Extension.GroupIconDirectory;

public class IconResource implements Closeable {
	private static final Kernel32 kernel32 = Kernel32.INSTANCE;
	private static final Shell32Extension shell32 = Shell32Extension.INSTANCE;
	private static final GDI32 gdi32 = GDI32.INSTANCE;
	private static final User32Extension user32 = User32Extension.INSTANCE;
	
	private HMODULE hmodule;
	private int iconCount;
	private List<IconGroup> iconGroups;
	private File resourceFile;
	
	public IconResource(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath() + " doesn't exist.");
		}
		resourceFile = file;
		
		hmodule = kernel32.LoadLibraryEx(file.getAbsolutePath(), null, User32Extension.LOAD_LIBRARY_AS_IMAGE_RESOURCE);
		iconCount = shell32.ExtractIconEx(file.getAbsolutePath(), 0, null, null, 0);
		
		final List<Short> ids = new ArrayList<>();
		EnumResNameProc proc = new EnumResNameProc() {
			@Override
			public boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam) {
				long nv = Pointer.nativeValue(name);
				if (nv >> 16 == 0) {
					ids.add((short) nv);
				}
				return true;
			}
		};
		kernel32.EnumResourceNames(hmodule, ResourceType.ICON_GROUP.getPointer(), proc, null);
		
		List<IconGroup> list = ids.stream().map(this::createIconGroup).collect(Collectors.toList());
		iconCount = list.size();
		iconGroups = Collections.unmodifiableList(list);
	}
	
	public static int getIconCount(File file) {
		return shell32.ExtractIconEx(file.getAbsolutePath(), 0, null, null, 0);
	}
	
	private IconGroup createIconGroup(short resourceName) {
		Pointer pointer = Util.loadResource(hmodule, Util.findResource(hmodule, resourceName, ResourceType.ICON_GROUP));
		GroupIconDirectory grpdir = Structure.newInstance(GroupIconDirectory.class, pointer);
		return new IconGroup(resourceName, grpdir);
	}
	
	public int size() {
		return iconCount;
	}
	
	public List<IconGroup> getIconGroups() {
		return iconGroups;
	}
	
	@Override
	public void close() {
		kernel32.CloseHandle(hmodule);
		hmodule = null;
	}
	
	public BufferedImage getImage(Icon icon) {
		if (hmodule == null) throw new IllegalStateException(
				String.format("Handle to '%s' is closed.", resourceFile.getAbsoluteFile()));
		
		HICON hicon = loadIconHandle(icon);
		ICONINFO info = ImageUtils.getIconInfo(hicon);
		try {
			return ImageUtils.createImage(info.hbmColor, icon.getBitCount() == 32 ? null : info.hbmMask);
		} finally {
			gdi32.DeleteObject(info.hbmColor);
			gdi32.DeleteObject(info.hbmMask);
			user32.DestroyIcon(hicon);
		}
		//return ImageUtils.createImage(info.hbmMask, 1);
	}
	
	private HICON loadIconHandle(Icon icon) {
		int id = icon.getResourceId();
		HRSRC hrsrc = Util.findResource(hmodule, id, ResourceType.ICON);
		try {
			Pointer pointer = Util.loadResource(hmodule, hrsrc);
			int dwSize = kernel32.SizeofResource(hmodule, hrsrc);
			return user32.CreateIconFromResourceEx(pointer, dwSize, true, 0x00030000,
			                                        0, 0, WinUser.LR_DEFAULTCOLOR);
		} finally {
			kernel32.CloseHandle(hrsrc);
		}
	}
}
