package us.irmak.win32.iconexplorer.pe;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

public class StructTest {

	@FieldOrder({"byteValue", "charValue", "shortValue", "floatValue", "intValue", "doubleValue", 
					"longValue", "byteArray", "charArray", "inner"})
	public static class TestStruct extends Struct {
		byte byteValue;
		char charValue;
		short shortValue;
		float floatValue;
		int intValue;
		double doubleValue;
		long longValue;
		
		byte[] byteArray = new byte[2];
		char[] charArray = new char[2];
		
		InnerTestStruct inner;
		
		public TestStruct() {
			super();
		}
		public TestStruct(ByteBuffer stream) {
			super(stream);
			readValues();
		}
	}
	
	@FieldOrder({"byteValue"})
	public static class InnerTestStruct extends Struct {
		byte byteValue;
		public InnerTestStruct() {
			super();
		}
		public InnerTestStruct(ByteBuffer stream) {
			super(stream);
			readValues();
		}
	}
	
	@Test
	public void test() {
		byte[] data = new byte[] {
				1, 
				0, 99, 
				0, 4, 
				(byte) 0x41, (byte) 0xD0, 0, 0, 
				0, 0, 0, 6, 
				0, 0, 0, 0, 0, 0, 0, 7, 
				0, 0, 0, 0, 0, 0, 0, 8, 
				70, 71, 
				0, 98, 0, 97,
				9};
		TestStruct test = new TestStruct(ByteBuffer.wrap(data));
		
		assertEquals(1, test.byteValue);
		assertEquals('c', test.charValue);
		assertEquals(4, test.shortValue);
		assertEquals(26.0f, test.floatValue, 0.001);
		assertEquals(6, test.intValue);
		assertEquals(8, test.longValue);
		assertEquals(70, test.byteArray[0]);
		assertEquals(71, test.byteArray[1]);
		assertEquals('b', test.charArray[0]);
		assertEquals('a', test.charArray[1]);
		assertEquals(9, test.inner.byteValue);
	}

}
