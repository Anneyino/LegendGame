package LegendGames;
import Games.*;

import java.util.List;

public class UnitPlace {
	private String name;
	private int x_pos;
	private int y_pos;
	private String mark;
	private boolean isAccessible;
	private String type;
	private Hero heroHere;
	private Monster monsterHere;
	private boolean hasHero;
	private boolean hasMonster;
	
	public UnitPlace() {
		this.setName("a unit place for legend");
		this.setXPos(0);
		this.setYPos(0);
		this.setMark("D");//default
		this.setType("UnitPlace");
	}
	
	public UnitPlace(String n, String m, boolean f) {
		this.setName(n);
		this.setMark(m);
		this.setIsAccessible(f);
		this.setXPos(0);
		this.setYPos(0);
		this.setType("UnitPlace");

		
	}
	public UnitPlace(String n, String m, boolean f, int x, int y) {
		this.setName(n);
		this.setXPos(x);
		this.setYPos(y);
		this.setMark(m);
		this.setIsAccessible(f);
		this.setType("UnitPlace");
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setXPos(int x) {
		if(x>=0) {
			this.x_pos = x;
		}else {
			this.x_pos = 0;
			System.out.println("x position must be over 0! Now apply 0 to x");
		}
	}
	
	public int getXPos() {
		return this.x_pos;
	}
	
	public void setYPos(int y) {
		if(y>=0) {
			this.y_pos = y;
		}else {
			this.y_pos = 0;
			System.out.println("y position must be over 0! Now apply 0 to y");
		}
	}
	
	public int getYPos() {
		return this.y_pos;
	}
	
	public void setMark(String m) {
		this.mark = m;
	}
	
	public String getMark() {
		return this.mark;
	}
	
	public void setIsAccessible(boolean flag) {
		this.isAccessible = flag;
	}
	
	public boolean getIsAccessible() {
		return this.isAccessible;
	}
	
	public void setType(String t) {
		this.type = t;
	}
	
	public String getType() {
		return this.type;
	}

	@Override
	public String toString() {
		if(isAccessible){
			return "xxx";
		}
		else{
			String heroSide="   ";
			String monsterSide="   ";
			if(hasHero){
				heroSide=" "+heroHere.getName();
			}
			if(hasMonster){
				monsterSide=monsterHere.getName()+" ";
			}
			return heroSide+" "+monsterSide;
		}
	}
}
