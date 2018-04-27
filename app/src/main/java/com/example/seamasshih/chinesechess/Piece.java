package com.example.seamasshih.chinesechess;

import android.graphics.Point;

public class Piece {
    private Point site = new Point();
    private int camp;
    private String chessName;
    private int index;

    public Point getSite(){
        return site;
    }
    public void setSite(int x , int y){
        site.x = x;
        site.y = y;
    }
    public int getCamp(){
        return camp;
    }
    public void setCamp(int camp){
        this.camp = camp;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex(int chessIndex){
        index = chessIndex;
        switch (chessIndex){
            case 0:
                chessName = (camp == 0 ? "帥":"將");
                break;
            case 1:
                chessName = (camp == 0 ? "仕":"士");
                break;
            case 2:
                chessName = (camp == 0 ? "相":"象");
                break;
            case 3:
                chessName = (camp == 0 ? "俥":"車");
                break;
            case 4:
                chessName = (camp == 0 ? "傌":"馬");
                break;
            case 5:
                chessName = (camp == 0 ? "炮":"包");
                break;
            case 6:
                chessName = (camp == 0 ? "兵":"卒");
                break;
        }
    }
    public String getPieceName(){
        return chessName;
    }

}
