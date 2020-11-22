import java.util.List;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Round {//a single round in a fight
    List<Hero> heroes;
    List<Monster> monsters;
    public Round(List<Hero> heroes, List<Monster> monsters) {
        this.heroes=heroes;
        this.monsters=monsters;
    }
    public void aNewRound(LegendMap world){//things happened ina complete round
        for(Hero h:heroes){
            boolean finishFlag=false;
            UnitPlace tile=world.getCurrentMap()[h.x][h.y];
            switch (tile.getType()){
                case "NexusCell"-> marketEvent(h,(NexusCell)tile);
                case "KoulouCell"-> ((KoulouCell)tile).addBuff(h);
                case "CaveCell"-> ((CaveCell)tile).addBuff(h);
                case "BlushCell"-> ((BlushCell)tile).addBuff(h);
            }
            if(h.isDead()){
                h.revive(world);
            }
            else{
                if(!hasEnemy(h,world)){
                    h.eventsWhenNoFight(world);
                }
                else{
                    Monster m=showCurrentTarget(h,world);
                    InputChecker checker=new InputChecker();
                    String indicator;
                    do{
                        System.out.println(h.getName()+ ", now, please press W/w, A/a, S/s, D/d, to go up, left, down or right, press c/C to change equipment, press b/B to check your inventories, press p/P to consume a potion, press e/E to attack, press r/R to cast a spell or press i/I to get information about heroes.");
                        indicator = checker.moveWithEnemyChecker(world,h);
                        switch (indicator){
                            case "i","I"->System.out.println(world.getHeroList());//get information of all heroes
                            case "c","C"->{//change equipment
                                h.changeEquipment();
                                finishFlag=true;
                                //return world.getCurrentMap()[h.x][h.y];
                            }
                            case "g","G"->//check bag
                                    h.showBag();
                            case "p","P"->{//use potion
                                h.consumePotion();
                                finishFlag=true;
                                //return world.getCurrentMap()[x][y];
                            }
                            case "e", "E"->{
                                heroAttack(h, m);
                                finishFlag=true;
                            }
                            case "r", "R"->{
                                castASpell(h, checker, m);
                                finishFlag=true;
                            }
                            case "b", "B"->{
                                tile=world.getCurrentMap()[h.x][h.y];
                                tile.setHasHero(false);
                                h.x=world.getMapWidth()-1;
                                h.y=h.belonging*world.getLaneWidth();
                                tile=world.getCurrentMap()[h.x][h.y];
                                tile.setHasHero(true);
                                tile.setHeroHere(h);
                                finishFlag=true;
                            }
                            case "t", "T"->{
                                System.out.println("which lane do you want to teleport to?");
                                h.laneNo=Integer.parseInt(checker.numberChecker(3))-1;
                                tile=world.getCurrentMap()[h.x][h.y];
                                tile.setHasHero(false);
                                int[] laneInfo=world.getPositionInfo()[h.laneNo];
                                h.x=max(laneInfo[2],laneInfo[3]);
                                h.y=h.laneNo*world.getLaneWidth();
                                tile=world.getCurrentMap()[h.x][h.y];
                                tile.setHasHero(true);
                                tile.setHeroHere(h);
                                finishFlag=true;
                            }
                            default -> {
                                tile=world.getCurrentMap()[h.x][h.y];
                                removeBuff(h, tile);

                                h.move(world, indicator);

                                tile=world.getCurrentMap()[h.x][h.y];
                                switch (tile.getType()){
                                    case "KoulouCell"-> ((KoulouCell)tile).addBuff(h);
                                    case "CaveCell"-> ((CaveCell)tile).addBuff(h);
                                    case "BlushCell"-> ((BlushCell)tile).addBuff(h);
                                }
                                finishFlag=true;
                            }
                        }
                    }
                    while (!finishFlag);
                    return;
                }
            }
        }
        for(Monster m:monsters){
            if(hasEnemy(m,world)){
                monsterAttack(m,world);
            }
        }
        for(Hero h:heroes){
            UnitPlace tile=world.getCurrentMap()[h.x][h.y];
            removeBuff(h, tile);
        }

        endingOfARound();
    }

    private void removeBuff(Hero h, UnitPlace tile) {
        switch (tile.getType()){
            case "KoulouCell"-> ((KoulouCell)tile).removeBuff(h);
            case "CaveCell"-> ((CaveCell)tile).removeBuff(h);
            case "BlushCell"-> ((BlushCell)tile).removeBuff(h);
        }
    }

    private Monster showCurrentTarget(Hero h, LegendMap world) {
        for(int i=0;i<world.getLaneWidth()-1;i++){
            UnitPlace tile=world.getCurrentMap()[h.x][h.laneNo*world.getLaneWidth()+i];
            if(tile.getHasMonster()){
                System.out.println(h.getName()+", your current target is "+tile.getMonsterHere().getName());
                return tile.getMonsterHere();
            }
        }
        System.out.println("Error! No monster nearby.");
        return monsters.get(0);
    }
    private Hero showCurrentTarget(Monster h, LegendMap world) {
        for(int i=0;i<world.getLaneWidth()-1;i++){
            UnitPlace tile=world.getCurrentMap()[h.x][h.laneNo*world.getLaneWidth()+i];
            if(tile.getHasHero()){
                return tile.getHeroHere();
            }
        }
        System.out.println("Error! No hero nearby.");
        return heroes.get(0);
    }

    private void endingOfARound() {//all move counters back to 0, heroes get some hp and mana back
        for(Hero h:heroes){
            h.setHp(h.getHp()*1.1);
            h.setMana(h.getMana()*1.1);
        }
    }

    private void monsterAttack(Monster m,LegendMap world) {//the procedure of a monster attacking a hero
        Hero h= showCurrentTarget(m,world);
        if(new Random().nextInt(2000)<=h.getAttributes().get("Agility")){
            System.out.println(LegendGame.ANSI_YELLOW+h.getName()+", you have dodged the attack from "+m.getName()+"."+LegendGame.ANSI_RESET);
            System.out.println(LegendGame.ANSI_YELLOW+"You now have "+h.getHp()+" hp."+LegendGame.ANSI_RESET);
        }
        else {
            int heroArmorDefend=h.getEquipments().get("Armor")==null?0:((Armor)h.getEquipments().get("Armor")).getDefend();
            int hurt= min((int) ((m.getDamage()-h.getDefense()-heroArmorDefend)*0.05),(int)h.getHp());
            System.out.println(LegendGame.ANSI_YELLOW+h.getName()+", you have been attacked by "+m.getName()+", it deals "+hurt+" damage to you. "+LegendGame.ANSI_RESET);
            h.setHp(h.getHp()-hurt);
            if (h.isDead()){
                System.out.println(LegendGame.ANSI_RED+"You are knocked out now."+LegendGame.ANSI_RESET);
            }
            else {
                System.out.println(LegendGame.ANSI_YELLOW+"You now have "+h.getHp()+" hp."+LegendGame.ANSI_RESET);
            }
        }
    }

    private void castASpell(Hero h, InputChecker inputChecker, Monster m) {//the procedure of a hero cast a spell towards a monster
        if(h.getSpellBag().isEmpty()){
            System.out.println("You have learnt no spells!");
        }
        else {
            System.out.println("You have learnt following spells.\n" +
                    h.getSpellBag().toString()+"\nPlease choose which one to cast.");
            int whichSpell=Integer.parseInt(inputChecker.numberChecker(h.getSpellBag().size()));
            Spell spell= h.getSpellBag().get(whichSpell-1);
            if(h.getMana()<spell.getManaCost()){
                System.out.println("You don't have enough mana for this spell!");
                return;
            }

            if(new Random().nextInt(100)<= m.getDodgeRate()){
                System.out.println(LegendGame.ANSI_YELLOW+ h.getName()+", you have missed. The monster now have "+ m.getHp()+" hp."+LegendGame.ANSI_RESET);
            }
            else{
                int hurt = min((int) ((h.getAttributes().get("Dexterity")+10000)*spell.getDamage()*0.0001- m.getDefense()*0.05), (int)m.getHp());
                m.setHp(m.getHp()-hurt);
                switch (spell.getType()) {
                    case "FireSpell" -> {
                        m.setDefense(m.getDefense()*0.9);
                        System.out.println("the defense of the monster has been reduced by 10%");
                    }
                    case "IceSpell" -> {
                        m.setDamage(m.getDamage()*0.9);
                        System.out.println("the damage of the monster has been reduced by 10%");
                    }

                    case "LightningSpell" -> {
                        m.setDodgeRate(m.getDodgeRate()*0.9);
                        System.out.println("the dodge chance of the monster has been reduced by 10%");
                    }
                }
                System.out.println(LegendGame.ANSI_YELLOW+ h.getName()+", you have dealt "+hurt+" damage to the monster."+LegendGame.ANSI_RESET);
                if(m.getHp()==0){
                    System.out.println("The monster is dead.");
                }
                else {
                    System.out.println("The monster now have "+ m.getHp()+" hp.");
                }
            }
        }
    }

    private void heroAttack(Hero h, Monster m) {//the procedure of a hero attacking a monster
        if(new Random().nextInt(100)<= m.getDodgeRate()){
            System.out.println(LegendGame.ANSI_YELLOW+ h.getName()+", you have missed. The monster now have "+ m.getHp()+" hp."+LegendGame.ANSI_RESET);
        }
        else{
            int heroWeaponDamage=h.getEquipments().get("Weapon")==null?0:((Weapon)h.getEquipments().get("Weapon")).getDamage();
            int hurt = min((int) ((h.getAttributes().get("Strength") + heroWeaponDamage - m.getDefense()) * 0.05), (int)m.getHp());
            m.setHp(m.getHp()-hurt);
            System.out.println(LegendGame.ANSI_YELLOW+ h.getName()+", you have dealt "+hurt+" damage to the monster."+LegendGame.ANSI_RESET);
            if(m.getHp()==0){
                System.out.println("The monster is dead.");
            }
            else {
                System.out.println("The monster now have "+ m.getHp()+" hp.");
            }
        }
    }

    private boolean hasEnemy(Hero h, LegendMap world){
        for(int i=0;i<world.getLaneWidth()-1;i++){
            UnitPlace tile=world.getCurrentMap()[h.x][h.laneNo*world.getLaneWidth()+i];
            if(tile.getHasMonster()){
                return true;
            }
        }
        return false;
    }
    private boolean hasEnemy(Monster h, LegendMap world){
        for(int i=0;i<world.getLaneWidth()-1;i++){
            UnitPlace tile=world.getCurrentMap()[h.x][h.laneNo*world.getLaneWidth()+i];
            if(tile.getHasHero()){
                return true;
            }
        }
        return false;
    }
    private void marketEvent(Hero hero,Market market){//the things happened when it's a market tile
        System.out.println("Welcome to the market! You can buy or sell things in the market!\n" +
                "Press b/B to show the item list in the market, money you have and start buying.\n" +
                "Press s/S to show your bag and start to sell them. Note you can only sell at half of their price in the market!\n" +
                "Press e/E to exit the market.");
        InputChecker inputChecker=new InputChecker();
        String order=inputChecker.marketChecker();
        while (!order.equals("e")&&!order.equals("E")) {
            switch (order) {
                case "s":
                case "S":
                    if (hero.getBackpack().size() == 0) {
                        System.out.println("There is nothing in your bag! ");
                    } else {
                        System.out.println(hero.getBackpack());
                        int indexToSell = Integer.parseInt(inputChecker.numberChecker(hero.getBackpack().size()));
                        market.SellItems(hero,indexToSell);
                    }
                    break;
                case "B":
                case "b":
                    System.out.println("Please choose whether you want to buy things in 1. a general market without spell or 2. a spell market.");
                    switch (inputChecker.numberChecker(2)){
                        case "1"->{
                            market.showGeneralItems();
                            System.out.println("You have " + hero.getMoney() + " money.");
                            int indexToBuy = Integer.parseInt(inputChecker.numberChecker(market.generalItemMap.size()));
                            market.PurchaseItems(hero,indexToBuy);
                        }
                        case "2"->{
                            market.showSpells();
                            System.out.println("You have " + hero.getMoney() + " money.");
                            int indexToBuy = Integer.parseInt(inputChecker.numberChecker(market.getSpellMap().size()));
                            market.PurchaseSpells(hero,indexToBuy);
                        }
                    }
            }
            System.out.println("Do you want to buy or sell more things?");
            order=inputChecker.marketChecker();
        }
        System.out.println("Thanks for your patronage.");

    }

}
