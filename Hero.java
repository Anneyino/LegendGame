package LegendGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hero implements Cloneable{
	private String name;
	private int id;
	private int level;
	private String character;// can be Warrior, Paladin and Sorcerer now
	private double HP; // this hp is not capped
	private double mana; // this mana is not capped
	private double defense; // the defense of the hero
	private double money;// the money will be initialized while creating the new hero
	private int exp; // the exp will be initialized while creating the new hero
	private boolean isAlivel; // the status to store hero's alive condition
	private Map<String,Double> attributes; // strength,dexterity and agility
	private Map<String,Item> Equipments; // current equipments
	private List<Item> backpack; // the items hero have
	private List<Spell> spellBag; // a hero masters some spells
	
	private GrowBehavior growBehavior; // the grow behavior of hero
	
	public Hero() {
		this.setName("A hero");
		this.setId(0);
		this.setLevel(1);
		this.setCharacter("general hero");
		this.setHp(100);
		this.setMana(100);
		this.setDefense(300);
		this.setMoney(1000);
		this.setExp(1);
		this.setIsAlive(true); // you should never init a dead hero...
		this.setAttributes(500, 500, 500);
		this.Equipments = new HashMap<String,Item>();
		this.setEquipments(null, null);
		this.setBackpack(new ArrayList<Item>());
		this.setSpellBag(new ArrayList<Spell>());
		
		this.setGrowBehavior(new GrowForWarrior());
	}
	
	//Name/id/mana/strength/dexterity/agility/starting money/starting experience
	public Hero(String n,int id, double mp, double stren, double dex, double agi, double mony, int ex) {
		this.setName(n);
		this.setId(id);
		this.setLevel(1);
		this.setCharacter("general hero");
		this.setHp(100);
		this.setMana(1000);
		this.setDefense(300);
		this.setMoney(mony);
		this.setExp(ex);
		this.setIsAlive(true); // you should never init a dead hero...
		this.setAttributes(stren, dex, agi);
		this.Equipments = new HashMap<String,Item>();
		this.setEquipments(null, null);
		this.setBackpack(new ArrayList<Item>());
		this.setSpellBag(new ArrayList<Spell>());
		
		this.setGrowBehavior(new GrowForWarrior());
	}
	
	// set the growbehavior
	public void setGrowBehavior(GrowBehavior gb) {
		this.growBehavior = gb;
	}
	
	//override clone method
	@Override
	public Hero clone() {
		Hero he = null;
		try {
			he = (Hero)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return he;
	}
	
	
	// level up method for hero, only when IsLevelUp is true can it be called.
	public void LevelUp() {
		this.growBehavior.grow(this.attributes);// increase attributes
		this.increaseLevel(); // increase Lv
		this.setHp(100*this.getLevel()); // increase HP
		
		Double mp = this.getMana();
		mp = mp * 1.1;
		int new_mp = new Double(mp).intValue();
		this.setMana(new_mp);// increase Mana
		
	}
	
	// method to check is the hero ready to level up
	public boolean IsLevelUp() {
		if(this.exp>=this.totalExpToLevelUp()) {
			return true;
		}else {
			return false;
		}
	}
	
	// method to calculate total exp hero need to level up
	private int totalExpToLevelUp() {
		int totalExp = 0;
		for(int i=1;i<=this.getLevel();i++) {
			totalExp += 10*i;
		}
		return totalExp;
	}
	public void setName(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setId(int i) {
		if(i>=0) {
			this.id = i;
		}else {
			this.id = 0;
		}// the id must be over 0 
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setLevel(int lv) {
		if(lv>=1) {
			this.level = lv;
		}else {
			this.level = 1;
		}// the level must be over 1
	}
	
	public void increaseLevel() {
	    int currentLv = this.getLevel();
	    currentLv += 1;
	    this.setLevel(currentLv);
	}
	
	public void setCharacter(String type) {
		this.character = type;
	}
	
	public String getCharacter() {
		return this.character;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setHp(double h) {
		if(h>=0) {
			this.HP = h;
		}else {
			this.HP = 0;
		}// the hp must be over 0
	}
	
	public double getHp() {
		return this.HP;
	}
	
	public void incHp(double inc) {
		double currentHp = this.getHp();
		this.setHp(currentHp+inc);
	}
	
	public void decHp(double dec) {
		double currentHp = this.getHp();
		this.setHp(currentHp-dec);
	}
	
	public boolean isDead() {
		if(this.getHp()==0) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setMana(double m) {
		if(m>=0) {
			this.mana = m;
		}else {
			this.mana = 0;
		}// the mana must be over 0
	}
	
	public double getMana() {
		return this.mana;
	}
	
	public void incMana(double inc) {
		double currrentMana = this.getMana();
		this.setMana(currrentMana+inc);
	}
	
	public void decMana(double dec) {
		double currentMana = this.getMana();
		this.setMana(currentMana-dec);
	}
	
	public void setDefense(double def) {
		if(def>=0) {
			this.defense = def;
		}else {
			this.defense = 0;
		}
	}
	
	public double getDefense() {
		return this.defense;
	}
	
	public void incDefense(double inc) {
		double currentDefense = this.getDefense();
		this.setDefense(currentDefense+inc);
	}
	
	public void setMoney(double my) {
		if(my>=0) {
			this.money = my;
		}else {
			this.money = 0;
		}// the money must be over 0
	}
	
	public void decMoney(double dec) {
		double result = this.getMoney() - dec;
		
		this.setMoney(result);
	}
	
	public void incMoney(double inc) {
		double result = this.getMoney() + inc;
		
		this.setMoney(result);
	}
	
	public double getMoney() {
		return this.money;
	}
	
	public void setExp(int ex) {
		if(ex>=0) {
			this.exp = ex;
		}else {
			this.exp = 0;
		} // the exp must be over 0
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public void incExp(int inc) {
		int currentExp = this.getExp();
		this.setExp(currentExp+inc);
	}
	
	
	public void setIsAlive(boolean doa) {
		this.isAlivel = doa;
	}
	
	public boolean getIsAlive() {
		return this.isAlivel;
	}
	public void setAttributes(double strength, double dexterity, double agility) {
		this.attributes = new HashMap<String,Double>();
		this.attributes.put("Strength", strength);
		this.attributes.put("Dexterity", dexterity);
		this.attributes.put("Agility", agility);
	}
	
	public void incStrength(double inc) {
		double currentStrength = this.getAttributes().get("Strength");
		this.attributes.put("Strength", currentStrength+inc);
	}
	
	public void incDexterity(double inc) {
		double currentDexterity = this.getAttributes().get("Dexterity");
		this.attributes.put("Dexterity", currentDexterity+inc);
	}
	
	public void incAgility(double inc) {
		double currentAgility = this.getAttributes().get("Agility");
		this.attributes.put("Agility", currentAgility+inc);
	}
	
	public Map<String,Double> getAttributes(){
		return this.attributes;
	}
	
	public void setEquipments(Weapon wp, Armor ar) {
		this.Equipments.put("Weapon", wp);
		this.Equipments.put("Armor", ar);
	}
	
	public Map<String,Item> getEquipments(){
		return this.Equipments;
	}
	
	public boolean setWeapon(Weapon wp) {
		if(this.level>=wp.getLevelLimit()) {
			this.Equipments.put("Weapon", wp);
			return true; // equip success
		}else {
			return false; // equip fail
		}
	}
	
	public boolean setArmor(Armor ar) {
		if(this.level>=ar.getLevelLimit()) {
			this.Equipments.put("Armor", ar);
			return true; // equip success
		}else {
			return false; // equip fail
		}
	}
	
	public void setBackpack(List<Item> bag) {
		this.backpack = bag;
	}
	
	public List<Item> getBackpack(){
		return this.backpack;
	}
	
	public void addToBackPack(Item it) {
		this.backpack.add(it);
	}
	
	public void removeFromBackPack(Item it) {
		this.backpack.remove(it);
	}
	
	public void removeFromBackPack(int id) {
		this.backpack.remove(id);
	}
	
	public void setSpellBag(List<Spell> bag) {
		this.spellBag = bag;
	}
	
	public List<Spell> getSpellBag(){
		return this.spellBag;
	}
	
	public void addToSpellBag(Spell sp) {
		this.spellBag.add(sp);
	}
	public void removeFromSpellBag(Spell sp) {
		this.spellBag.remove(sp);
	}

}
