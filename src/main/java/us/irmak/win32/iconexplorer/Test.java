package us.irmak.win32.iconexplorer;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

public class Test {

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		File file = new File("C:\\Windows\\SystemResources\\imageres.dll.mun");
		try (FileInputStream stream = new FileInputStream(file)) {
			stream.skip(100000);
			byte[] data = stream.readNBytes(65000);
			System.out.println("data size: " + data.length);
		}
		
		System.out.println(System.currentTimeMillis()-start);
		
		start = System.currentTimeMillis();
		
		IconResource resource = new NativeIconResource(file);
		MultiResolutionImageToolkit toolkit = new MultiResolutionImageToolkit(resource);
		Image img = resource.getIconGroups().stream()
			.filter(gr -> gr.getResourceName().equals("1010"))
			.map(gr -> new BaseMultiResolutionImage(toolkit.getImages(gr)))
			.findAny().get();
			
		
		
		System.out.println("width"+img.getWidth(null));
		
		System.out.println(System.currentTimeMillis()-start);
		
		 img = resource.getIconGroups().stream()
					.filter(gr -> gr.getResourceName().equals("50"))
					.map(gr -> new BaseMultiResolutionImage(toolkit.getImages(gr)))
					.findAny().get();
		System.out.println("width"+img.getWidth(null));
		
		System.out.println(System.currentTimeMillis()-start);
		
		 img = resource.getIconGroups().stream()
					.filter(gr -> gr.getResourceName().equals("51"))
					.map(gr -> new BaseMultiResolutionImage(toolkit.getImages(gr)))
					.findAny().get();
		System.out.println("width"+img.getWidth(null));
		
		System.out.println(System.currentTimeMillis()-start);
	}

}
