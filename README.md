# Windows Icon Explorer
This library can discover and export Icon files from icon sources (DLL, EXE). 

## Quick Use
Double click on the jar file to execute, or run the `java -jar iconexplorer.jar`. The GUI will open. You can open any file, or a directory to explore through the file menu.

![Icon Explorer Screen Shot](https://fatihirmak.dev/media/public/image/iconexplorer.png)

## API
```java
File dllFile = new File("user32.dll");
try (IconResource resource = new IconResource(dllFile) {
	System.out.format("There are %d icons in %s file.\n", resource.size(), dllFile.getName());
	List<IconGroup> groups = resource.getIconGroups();
	groups.forEach(group -> {
		List<Icon> icons = group.getIcons();
		System.out.format("There are %d sizes in the Icon group %d.\n", icons.size(), group.getResourceName());	
		icons.forEach(icon -> {
			System.out.println("Icon id: %d, width: %d, height: %d, bpp: %d", icon.getResourceId(), icon.getWidth(),
												icon.getHeight(), icon.getBitCount());
			BufferedImage image = resource.getImage(icon);
		});
	});
}
```

## Background
This library is built on top of JNA and uses Windows functions to retrieve Icon data from icon resource files. One of the
standard WinAPI functions to extract icon from a resource file is `ExtractIcon` and `ExtractIconEx`. They can capture
small and large sized icons (Default icon sizes are configured in Windows). However these are ancient functions that can't support
the complex icon format which can include images with different sizes or different density (bits per pixel / bpp). Modern
Windows icons contain images with sizes from 16px to 256px, and density from 1bit to 32bit. 