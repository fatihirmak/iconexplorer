# Windows Icon Explorer
This library can discover and export Icon files from icon sources (DLL, EXE). 

## Quick Use
Double click on the jar file to execute, or run the `java -jar iconexplorer.jar`. The GUI will open. You can open any file, or a directory to explore through the file menu.
You can export all the icons, and specify a pattern for file name output:

```
{f} | {filename} - name of the file that was opened (user32.dll for ex)
{r} | {resourceid} - resource id of the icon group
{b} | {bpp} - density of the image bits per pixel (1, 4, 8, 16, 24, 32)
{w} | {width} - width of the image in pixels
{h} | {height} - height of the image in pixels
```
A pattern like `{f}\{r}\{w}x{h}-{b}bpp.png` would create images in 2 level folders: user32.dll\101\32x32-4bpp.png


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

## Building the library
This library is built on top of JNA and uses Windows functions to retrieve Icon data from icon resource files. One of the
standard WinAPI functions to extract icon from a resource file is `ExtractIcon` and `ExtractIconEx`. They can capture
small and large sized icons (Default icon sizes are configured in Windows). However these are ancient functions that can't support
the complex icon format which can include images with different sizes or different density (bits per pixel / bpp). Modern
Windows icons contain images with sizes from 16px to 256px, and density from 1bit to 32bit. 

Icon files consist of 2 part: A directory that contains multiple versions of the same image, and icon images<sup>1</sup>: 

```c
typedef struct GRPICONDIR
{
    WORD idReserved;
    WORD idType;
    WORD idCount;
    GRPICONDIRENTRY idEntries[];
} GRPICONDIR;

#pragma pack( 2 )
typedef struct GRPICONDIRENTRY
{
    BYTE  bWidth;
    BYTE  bHeight;
    BYTE  bColorCount;
    BYTE  bReserved;
    WORD  wPlanes;
    WORD  wBitCount;
    DWORD dwBytesInRes;
    WORD  nId;
} GRPICONDIRENTRY;
```

Unfortunately these aren't defined in JNA. So the first section is creating a definition for both. I created `jna.GroupIconDirectory` and
`jna.GroupIconDirectoryEntry` classes that matches those WinAPI structures respectively. GroupIconDirectory is a simple 
JNA structure, however GroupIconDirectoryEntry required extra handling because it aligns structure fields into 2 bytes (#pragma pack(2)).
So I needed to override getNativeAlignment to align the single byte fields to 2 bytes. I couldn't find a better way to handle alignment
of structures in JNA. 

```java
@Override
protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
    return Math.min(2, super.getNativeAlignment(type, value, isFirstElement));
}
```


## References
<sup>1</sup> - [The format of icon resources](https://devblogs.microsoft.com/oldnewthing/20120720-00/?p=7083)