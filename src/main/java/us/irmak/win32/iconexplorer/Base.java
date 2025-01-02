package us.irmak.win32.iconexplorer;

public class Base {
	FilterItem<Integer> item;
	BaseTest baseTest = new BaseTest(item);
	
	public static void main(String[] args) {
		
		Base base = new Base();
		System.out.println(base.baseTest.item);
	}
	
	public Base() {
		item = new FilterItem<>("label", 3);
	}
	
	public static class BaseTest {
		FilterItem<Integer> item;

		public BaseTest(FilterItem<Integer> item) {
			this.item = item;
		}
		
		
	}

}
