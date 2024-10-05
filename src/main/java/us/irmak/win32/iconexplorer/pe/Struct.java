package us.irmak.win32.iconexplorer.pe;

import java.io.InputStream;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.function.Supplier;

public class Struct {
	protected ByteBuffer data;
	public static <T extends Struct> T load(Class<T> cls, InputStream stream) {
		try {
			ByteBuffer data = ByteBuffer.wrap(stream.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
			T instance = cls.getDeclaredConstructor(ByteBuffer.class).newInstance(data);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Can't instantinate the structure", e);
		}
	}
	
	protected Struct() {
		
	}
	
	protected void readValues() {
		String[] fields = Optional.ofNullable(this.getClass().getAnnotation(FieldOrder.class))
												.map(FieldOrder::value).orElse(new String[0]);
		if (fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				try {
					Field field = this.getClass().getDeclaredField(fields[i]);
					readField(field, data);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private void readField(Field field, ByteBuffer buffer) throws IllegalArgumentException, IllegalAccessException {
		Class<?> cls = field.getType();
		if (cls.isArray()) {
			Class<?> type = cls.getComponentType();
			Object array = field.get(this);
			int arraySize = Array.getLength(array);
			if (type.isPrimitive()) {
				for (int i = 0; i < arraySize; i++) {
					Array.set(array, i, getValue(type, buffer));
				}
			}
		} else {
			field.set(this, getValue(cls, data));
		}
	}
	
	private Object getValue(Class<?> cls, ByteBuffer buffer) {
		if (cls.isPrimitive()) {
			String name = cls.getName();
			String methodName = "get";
			if (!"byte".equals(name)) {
				methodName += name.substring(0, 1).toUpperCase() + name.substring(1);
			}
			try {
				return ByteBuffer.class.getMethod(methodName).invoke(buffer);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (Struct.class.isAssignableFrom(cls)) {
			try {
				return cls.getConstructor(ByteBuffer.class).newInstance(buffer);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("Can't read value of an object that's not descendent of Struct");
		}
	}
	
	Class<?> wrap(Class<?> primitive) {
		try {
			return MethodType.methodType(primitive).wrap().returnType();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	int getPrimitiveSize(Class<?> cls) {
		
		Class<?> type = MethodType.methodType(cls).wrap().returnType();
		try {
			return type.getField("BYTES").getInt(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Struct(ByteBuffer stream) {
		this.data = stream;
	}
	
	protected byte[] readRemaining() {
		int remaining = data.remaining();
		byte[] bytes = new byte[remaining];
		data.get(bytes);
		return bytes;
	}
	
	protected byte readByte() {
		return data.get();
	}
	
	protected short readShort() {
		return data.getShort();
	}
	
	protected int readInt() {
		return data.getInt();
	}
	
	protected byte[] read(int offset, int length) {
		byte[] bytes = new byte[length];
		data.get(bytes, offset, length);
		return bytes;
	}
	
	protected byte[] readBytes(int size) {
		return read(0, size);
	}
	
	protected ByteBuffer subBuffer(int offset, int length) {
		return data.slice(offset, length).order(ByteOrder.LITTLE_ENDIAN);
	}
	
	protected MarkStream markAndSet(int position) {
		return new MarkStream(position, data);
	}
	
	class MarkStream {
		private int position;
		
		public MarkStream(int position, ByteBuffer buffer) {
			super();
			this.position = position;
		}

		public <T> FinalAction<T> apply(Supplier<T> supplier) {
			return new FinalAction<T>(position, supplier);
		}
	}
	
	class FinalAction<T> {
		private int position;
		Supplier<T> supplier;

		public FinalAction(int position, Supplier<T> supplier) {
			super();
			this.position = position;
			this.supplier = supplier;
		}

		public T reset() {
			int pos = data.position();
			data.position(position);
			try {
				return supplier.get();
			} finally {
				data.position(pos);
			}
		}
	}
}
