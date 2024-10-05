package us.irmak.win32.iconexplorer.pe;

public enum ResourceType {
	CURSOR(1), BITMAP(2), ICON(3), MENU(4), DIALOG(5), STRING(6), FONTDIR(7), FONT(8), ACCELERATOR(9),
	RCDATA(10), MESSAGETABLE(11), GROUP_CURSOR(12), GROUP_ICON(14);
	
	private int id;
	private ResourceType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
