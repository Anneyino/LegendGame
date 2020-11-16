package LegendGames;

public class NonAccessPlace extends UnitPlace{
	
	public NonAccessPlace() {
		super("Inaccessible Place","X",false);
		this.setType("NonAccessPlace");
	}
	
	public NonAccessPlace(int x, int y) {
		super("Inaccessible Place", "X", false, x, y);
		this.setType("NonAccessPlace");
	}

}
