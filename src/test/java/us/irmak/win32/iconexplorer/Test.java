package us.irmak.win32.iconexplorer;

import java.util.Arrays;

import javax.imageio.ImageIO;

public class Test {
	@org.junit.Test
	public void test() {
		String writerNames[] = ImageIO.getWriterFormatNames();
		Arrays.asList(writerNames).forEach(System.out::println);
	}
}
