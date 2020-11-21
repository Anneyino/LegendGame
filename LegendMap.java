package LegendGames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LegendMap extends GeneralMap{
	
	private UnitPlace[][] currentMap;
    //mapLength with respect to y
    //mapWidth  with respect to x, we create the UnitPlace[width][length], that is UnitPlace[x][y]
	private int[] HeroPosition;
	
	private int laneWidth; // the width of a lane, default is 2
	private boolean isMobaMode; // to decide whether the map is moba or not
	private List<Hero> herolist; // the heroes in moba legend game.
	
	public LegendMap() {
		super("LegendMap",8,8);
		
		this.initRandomMap();
	}
	
	// this method to generate basic Legend Map
	public LegendMap(int width,int length) {
        super("LegendMap",width,length);
		this.setIsMoba(false);
		this.initRandomMap();
	}
	// this method to generate Moba Legend Map
	public LegendMap(int laneWid,int width,List<Hero> heroes) {
		super("Moba LegendMap",width,laneWid*3+2);
		this.setLaneWidth(laneWid);
		this.setIsMoba(true);
		this.setHeroList(heroes);
		this.initRandomMobaMap();
	}
	
	// init random map and set the hero position to (0,0)
	public void initRandomMap() {
		
		
		int mapSize = this.getMapLength()*this.getMapWidth();
		Double marketDouble = mapSize*0.3;
		int marketNum = marketDouble.intValue();
		Double commonCellDouble = mapSize*0.5;
		int commonCellNum = commonCellDouble.intValue();
		
		int nonAccessNum = mapSize - marketNum - commonCellNum; 
		List<UnitPlace> unitList = new ArrayList<UnitPlace>();
		
		for(int i=0;i<marketNum;i++) {
			unitList.add(new Market());
		}
		for(int i=0;i<commonCellNum;i++) {
			unitList.add(new CommonCell());
		}
		for(int i=0;i<nonAccessNum;i++) {
			unitList.add(new NonAccessPlace());
		}
		
		// shuffle the map
		exchange(unitList);
		
		int yLength = this.getMapLength();
		int xWidth = this.getMapWidth();
		
		UnitPlace[][] randomMap = new UnitPlace[xWidth][yLength];
		
		for(int i = 0;i < mapSize;i++) {
			int index1 = i/yLength;
			int index2 = i%yLength;
			randomMap[index1][index2] = unitList.get(i);
		}
		// set the first cell always be common Area, and heros are starting there.
		randomMap[0][0] = new CommonCell();
		int[] heropos = new int[] {0,0};
		this.setHeroPos(heropos);
		
		this.setCurrentMap(randomMap);
	}
	
	// init random Moba Map for Legend Game II
	public void initRandomMobaMap() {
		
		int randomSize = 3*this.laneWidth*(this.getMapWidth()-2); // get the size we need to randomly assign for the moba map
		
		Double BlushSize = randomSize*0.2;
		int BlushNum = BlushSize.intValue();
		Double CaveSize = randomSize*0.2;
		int CaveNum = CaveSize.intValue();
		Double KoulouSize = randomSize*0.2;
		int KoulouNum = KoulouSize.intValue();
		
		int PlainNum = randomSize - BlushNum - CaveNum - KoulouNum;
		
		List<UnitPlace> cellList = new ArrayList<UnitPlace>();
		
		for(int i = 0; i<BlushNum;i++) {
			cellList.add(new BlushCell());
		}
		
		for(int i = 0; i<CaveNum;i++) {
			cellList.add(new CaveCell());
		}
		
		for(int i = 0; i<KoulouNum;i++) {
			cellList.add(new KoulouCell());
		}
		
		for(int i = 0; i<PlainNum; i++) {
			cellList.add(new PlainCell());
		}
		
		// shuffle the map
		exchange(cellList);
		
		int Xwidth = this.getMapWidth();
		int Ylength = this.getMapLength();		
		UnitPlace[][] randomMobaMap = new UnitPlace[Xwidth][Ylength];
		// assign different cells to map
		for(int i=0;i<Xwidth;i++) {
			// assign the first row and last row
			if(i==0||i==Xwidth-1) {
				for(int j=0;j<Ylength;j++) {
					// assign inaccessible cells
					if(j==this.getLaneWidth()||j==2*this.getLaneWidth()+1) {
						randomMobaMap[i][j] = new NonAccessPlace();
					}else {
						// if the first line, then assign monster nexus cell
						if(i==0) {
							randomMobaMap[i][j] = new NexusCell(false);
						}else {
							// if the last line, then assign hero nexus cell
							for(int k = 0;k<3;k++) {
								if(j==k*(this.laneWidth+1)) {
									randomMobaMap[i][j] = new NexusCell(this.getHeroList().get(k));// assign kth hero to this nexus cell
									break;
								}else {
									randomMobaMap[i][j] = new NexusCell(true);// assign general hero base(not a rebirth point)
								}
							}
						}
					}
				}
			}else {
				// assign other rows
				for(int j = 0; j<Ylength;j++) {
					if(j==this.getLaneWidth()||j==2*this.getLaneWidth()+1) {
						randomMobaMap[i][j] = new NonAccessPlace();
					}else {
						randomMobaMap[i][j] = popFromCellList(cellList);// assign random cells from cell list
					}
					
				}
			}
			
		}
		
		this.setCurrentMap(randomMobaMap);


	}
	
	// method to shuffle the unitPlace List
	public static void exchange(List<UnitPlace> l) {
		if(l==null||l.isEmpty()) {
			return;
		}
		Random rand = new Random();
		
		for (int i = 0; i < l.size(); i++) {
			int j = rand.nextInt(l.size());
			if(j != i) {
				UnitPlace temp = l.get(j);
				l.set(j, l.get(i));
				l.set(i, temp);
			}
		}	
	}
	
	// method to pop cells from cell List
	public static UnitPlace popFromCellList(List<UnitPlace> ls) {
		UnitPlace targetCell = null;
		if(!ls.isEmpty()) {
            targetCell = ls.get(0);
			ls.remove(0);
		}
		
		return targetCell;
	}
	
	
	// method to check if heros are blocked 
	public boolean isHeroBlocked() {
		boolean flag = false;
		
		boolean upmovable = UpMovable();
		boolean downmovable = DownMovable();
		boolean leftmovable = LeftMovable();
		boolean rightmovable = RightMovable();
		
		if(!upmovable&&!downmovable&&!leftmovable&&!rightmovable) {
			flag = true;
		}
		
		
		return flag;
	}
	
	
	// method to check if hero can move up
	
	public boolean UpMovable() {
		boolean flag = false;
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroX>0&&currentMap[HeroX-1][HeroY].getIsAccessible()) {
			flag = true;
		}
		
		return flag;
	}
	
	// method to check if hero can move down
	
	public boolean DownMovable() {
		boolean flag = false;
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroX<this.getMapWidth()-1&&currentMap[HeroX+1][HeroY].getIsAccessible()) {
			flag = true;
		}
		return flag;
	}
	
	
	// method to check if hero can move left
	public boolean LeftMovable() {
		boolean flag = false;
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		
		if(HeroY>0&&currentMap[HeroX][HeroY-1].getIsAccessible()) {
			flag = true;
		}
		
		return flag;
	}
	// method to check if hero can move right
	public boolean RightMovable() {
		boolean flag = false;
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		
		if(HeroY<this.getMapLength()-1&&currentMap[HeroX][HeroY+1].getIsAccessible()) {
			flag = true;
		}
		
		return flag;
	}
	
	public void setCurrentMap(UnitPlace[][] m) {
		this.currentMap = m;
	}
	
	public UnitPlace[][] getCurrentMap(){
		return this.currentMap;
	}
	
	// override set method for maplength
	public void setMapLength(int len) {
		if(len>=8) {
			super.setMapLength(len);
		}else {
			super.setMapLength(8);
		}
	}
	
	// override set method for mapwidth
	public void setMapWidth(int wid) {
		if(wid>=8) {
			super.setMapWidth(wid);
		}else {
			super.setMapWidth(8);
		}
	}
	
	// set the width of each lane
	public void setLaneWidth(int wid) {
		if(wid>=2) {
			this.laneWidth = wid;
		}else {
			this.laneWidth = 2;
		}
	}
	
	public int getLaneWidth() {
		return this.laneWidth;
	}
	
	public void setIsMoba(boolean flag) {
		this.isMobaMode = flag;
	}
	
	public boolean getIsMoba() {
		return this.isMobaMode;
	}
	
	public void setHeroList(List<Hero> hlist) {
		this.herolist = hlist;
	}
	
	public List<Hero> getHeroList(){
		return this.herolist;
	}
	
	
	public void setHeroPos(int[] pos) {
		this.HeroPosition = pos;
	}
	
	public int[] getHeroPos() {
		return this.HeroPosition;
	}
	
	public void moveHeroUp() {
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroX>0) {
			if(currentMap[HeroX-1][HeroY].getIsAccessible()) {
				HeroPosition[0] = HeroX - 1;
			}else {
				System.out.println("Can't move there, it is inaccessible");
			}
		}else {
			HeroPosition[0] = 0; // if in bound, not move
		}
	}
	
	public void moveHeroDown() {
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroX<this.getMapWidth()-1) {
			if(currentMap[HeroX+1][HeroY].getIsAccessible()) {
				HeroPosition[0] = HeroX + 1;
			}else {
				System.out.println("Can't move there, it is inaccessible");
			}
		}else {
			HeroPosition[0] = this.getMapWidth()-1; // if in bound, not move
		}
	}
	
	public void moveHeroLeft() {
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroY>0) {
			if(currentMap[HeroX][HeroY-1].getIsAccessible()) {
				HeroPosition[1] = HeroY - 1;
			}else {
				System.out.println("Can't move there, it is inaccessible");
			}
			
		}else {
			HeroPosition[1] = 0; // if in bound, not move
		}
	}
	
	public void moveHeroRight() {
		int HeroX = HeroPosition[0];
		int HeroY = HeroPosition[1];
		if(HeroY<this.getMapLength()-1) {
			if(currentMap[HeroX][HeroY+1].getIsAccessible()) {
				HeroPosition[1] = HeroY + 1;
			}else {
				System.out.println("Can't move there, it is inaccessible");
			}

		}else {
			HeroPosition[1] = this.getMapLength()-1; // if in bound, not move
		}
	}
	
	public void showCurrentMap() {
		
		int width = this.getMapWidth();
		int length = this.getMapLength();
		
		for(int i=0;i<width+1;i++) {
			for(int j=0;j<length;j++) {
				System.out.print("+--");
				if(j==length-1) {
					System.out.println("+");
				}
			}
			if(i<width) {
				for(int k=0;k<length;k++) {
					if(HeroPosition[0]==i&&HeroPosition[1]==k) {
						System.out.print("|H "); // show heros position
					}else {
						System.out.print("|"+currentMap[i][k].getMark()+" ");
					}
					if(k==length-1) {
						System.out.println("|");
					}
				}
			}
		}
		System.out.println("");
		System.out.println("H for heroes, M for markets, X for inaccessibles");
		System.out.println("");
	}

	@Override
	public String toString() {
		StringBuilder ans= new StringBuilder();
		for (UnitPlace[] unitPlaces : currentMap) {
			for (int j = 0; j < currentMap[0].length; j++) {
				// String type = String.valueOf(unitPlaces[j].getType().charAt(0));
				String mark = unitPlaces[j].getMark();
				ans.append(mark).append(" - ").append(mark).append(" - ").append(mark).append("  ");
			}
			ans.append("\n");
			for (int j = 0; j < currentMap[0].length; j++) {
				ans.append("|").append(unitPlaces[j]).append("|");
			}
			ans.append("\n");
			for (int j = 0; j < currentMap[0].length; j++) {
				// String type = String.valueOf(unitPlaces[j].getType().charAt(0));
				String mark = unitPlaces[j].getMark();
				ans.append(mark).append(" - ").append(mark).append(" - ").append(mark).append("  ");
			}
			ans.append("\n\n");
		}
		return String.valueOf(ans);
	}
}
