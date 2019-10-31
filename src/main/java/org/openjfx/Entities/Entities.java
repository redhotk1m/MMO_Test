package org.openjfx.Entities;

public class Entities {
    private int x, y, level, hp, mp;


    public Entities(int x, int y, int level, int hp, int mp){
        this.x = x;
        this.y = y;
        this.level = level;
        this.hp = hp;
        this.mp = mp;
    }

    public void move(){

    }

    public void addEntityToView(){

    }

    public void tick(){

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }
}
