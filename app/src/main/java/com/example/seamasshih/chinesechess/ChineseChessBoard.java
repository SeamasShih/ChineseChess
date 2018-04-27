package com.example.seamasshih.chinesechess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

class ChineseChessBoard extends View {
    private Piece[] pieces = new Piece[32];
    private Paint[] paintChess = new Paint[2];
    private Paint paintWhite = new Paint();
    private Paint[] paintText = new Paint[2];
    private Paint paintBoard = new Paint();
    private Paint paintSelect = new Paint();
    private Paint paintCanMove = new Paint();
    private Path pathChess = new Path();
    private Path pathBoard = new Path();
    private Path pathBoardDecorOne = new Path();
    private Path pathBoardDecorTwo = new Path();
    private Resources resources = this.getResources();
    private DisplayMetrics dm = resources.getDisplayMetrics();
    private int screenWidth = dm.widthPixels;
    private int screenHeight = dm.heightPixels;
    private int edgeW = screenWidth/10;
    private int edgeH = screenHeight/6;
    private float widthFE = (screenWidth - edgeW*2)/8.0f;
    private float heightFE = (screenHeight - edgeH*2)/9.0f;
    private float adjustTextY;
    private float radiusChess = (edgeH > edgeW ? edgeW/2f : edgeH/2f);
    private int nowCamp = 0;
    private int nowChess;
    private int chess;
    private final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;
    private boolean isChoosing = false;
    private Point choose = new Point();
    private int[][] isNull = new int[9][10];
    private ArrayList<Point> canMove = new ArrayList<Point>();
    private int winner = -1;
    private String[] winText = {"紅方勝利","黑方勝利"};
    private String stringRiver = "楚河";
    private String stringSide = "漢界";
    private Context context;
    private SoundPool sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
    int click;
    int play;


    public ChineseChessBoard(Context context , AttributeSet attributeSet) {
        super(context , attributeSet);
        this.context = context;
        for (int i = 0 ; i < paintChess.length ; i++) {
            paintChess[i] = new Paint();
            paintText[i] = new Paint();
        }
        init();
    }

    private void init(){
        click = sound.load(context, R.raw.click, 1);
        play = sound.load(context, R.raw.short_punch1,1);
        for (int i = 0 ; i < pieces.length ; i++)
            pieces[i] = new Piece();
        for (int i = 0 ; i < pieces.length ; i++) {
            pieces[i].setCamp(i/16);
            if (i%16 > 10)
                pieces[i].setIndex(6);
            else
                pieces[i].setIndex((i%16+1)/2);
        }
        setChessSite();
        paintBoard.setColor(Color.rgb(0xAA,0xAA,100));
        paintBoard.setStyle(Paint.Style.FILL);
        paintSelect.setColor(Color.GREEN);
        paintSelect.setStyle(Paint.Style.STROKE);
        paintSelect.setStrokeWidth(5);
        paintCanMove.setColor(Color.YELLOW);
        paintCanMove.setStyle(Paint.Style.STROKE);
        paintCanMove.setStrokeWidth(5);
        paintWhite.setColor(Color.rgb(200,200,200));
        paintWhite.setAntiAlias(true);
        paintWhite.setStyle(Paint.Style.FILL);
        paintChess[0].setColor(Color.RED);
        paintChess[0].setStyle(Paint.Style.STROKE);
        paintChess[0].setAntiAlias(true);
        paintChess[0].setStrokeWidth(3);
        paintText[0].setColor(Color.RED);
        paintText[0].setTextSize(60);
        paintText[0].setTextAlign(Paint.Align.CENTER);
        paintChess[1].setColor(Color.BLACK);
        paintChess[1].setStyle(Paint.Style.STROKE);
        paintChess[1].setAntiAlias(true);
        paintChess[1].setStrokeWidth(3);
        paintText[1].setColor(Color.BLACK);
        paintText[1].setTextSize(60);
        paintText[1].setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrix = paintText[0].getFontMetrics();
        adjustTextY = (fontMetrix.bottom - fontMetrix.top)/2 - fontMetrix.bottom;

        pathBoard.addRect(0,0,widthFE*8,heightFE*9, Path.Direction.CCW);
        for (int i = 1 ; i < 8 ; i++) {
            pathBoard.moveTo(widthFE * i, 0);
            pathBoard.lineTo(widthFE * i , heightFE*4);
            pathBoard.moveTo(widthFE * i, heightFE*5);
            pathBoard.lineTo(widthFE * i , heightFE*9);
        }
        for (int i = 1 ; i < 9 ; i++) {
            pathBoard.moveTo(0, heightFE*i);
            pathBoard.lineTo(widthFE*8, heightFE*i);
        }
        pathBoard.moveTo(widthFE*3,0);
        pathBoard.lineTo(widthFE*5,heightFE*2);
        pathBoard.moveTo(widthFE*5,0);
        pathBoard.lineTo(widthFE*3,heightFE*2);
        pathBoard.moveTo(widthFE*3,heightFE*9);
        pathBoard.lineTo(widthFE*5,heightFE*7);
        pathBoard.moveTo(widthFE*5,heightFE*9);
        pathBoard.lineTo(widthFE*3,heightFE*7);

        float s = 0.1f;
        float l = 0.2f;
        pathBoardDecorOne.moveTo(s*widthFE,l*heightFE);
        pathBoardDecorOne.lineTo(s*widthFE,s*heightFE);
        pathBoardDecorOne.lineTo(l*widthFE,s*heightFE);
        pathBoardDecorOne.moveTo(s*widthFE,-l*heightFE);
        pathBoardDecorOne.lineTo(s*widthFE,-s*heightFE);
        pathBoardDecorOne.lineTo(l*widthFE,-s*heightFE);
        pathBoardDecorTwo.moveTo(-s*widthFE,l*heightFE);
        pathBoardDecorTwo.lineTo(-s*widthFE,s*heightFE);
        pathBoardDecorTwo.lineTo(-l*widthFE,s*heightFE);
        pathBoardDecorTwo.moveTo(-s*widthFE,-l*heightFE);
        pathBoardDecorTwo.lineTo(-s*widthFE,-s*heightFE);
        pathBoardDecorTwo.lineTo(-l*widthFE,-s*heightFE);

        pathChess.addCircle(0,0,radiusChess, Path.Direction.CCW);

        for (int i = 0 ; i < 9 ; i++){
            for (int j = 0 ; j < 10 ; j++){
                isNull[i][j] = -1;
            }
        }
        for (int i = 0 ; i < pieces.length ; i++)
            isNull[pieces[i].getSite().x][pieces[i].getSite().y] = pieces[i].getCamp();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (isFastClick()) {
            float x = event.getX();
            float y = event.getY();
            int nx = (int) ((x - edgeW) / widthFE);
            int ny = (int) ((y - edgeH) / heightFE);
            int lx = (int) (x - nx * widthFE - edgeW);
            int ly = (int) (y - ny * heightFE - edgeH);
            nx = (lx < widthFE / 2 ? nx : nx + 1);
            ny = (ly < heightFE / 2 ? ny : ny + 1);
            if (winner != -1){
                winner = -1;
                nowCamp = 0;
                init();
                invalidate();
            }
            else if (!(nx < 0 || nx > 8 || ny < 0 || ny > 9)) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (isChoosing) {
                        if (isNull[nx][ny] == nowCamp){
                            canMove.clear();
                            choose.x = nx;
                            choose.y = ny;
                            for (int i = 0 ; i < pieces.length ; i++){
                                if (pieces[i] == null) continue;
                                if (pieces[i].getSite().x == choose.x && pieces[i].getSite().y == choose.y) {
                                    nowChess = pieces[i].getIndex();
                                    chess = i;
                                    break;
                                }
                            }
                            whereICanMove();
                            sound.play(click,(float) 1, (float) 1, 0, 0, 1);
                            invalidate();
                            return super.onTouchEvent(event);
                        }
                        for (int i = 0 ; i < canMove.size() ; i++){
                            if (nx == canMove.get(i).x && ny == canMove.get(i).y){
                                for (int j = 0 ; j < pieces.length ; j++){
                                    if (pieces[j] == null) continue;
                                    if (pieces[j].getSite().x == nx && pieces[j].getSite().y == ny){
                                        if (j == 0 || j == 16) winner = nowCamp;
                                        pieces[j] = null;
                                    }
                                }
                                isNull[choose.x][choose.y] = -1;
                                isNull[nx][ny] = nowCamp;
                                pieces[chess].setSite(nx,ny);
                                isChoosing = false;
                                nowCamp = 1 - nowCamp;
                                canMove.clear();
                                sound.play(play,1,1,0,0,1);
                                break;
                            }
                        }
                    }
                    else {
                        if (isNull[nx][ny] == nowCamp) {
                            isChoosing = true;
                            choose.x = nx;
                            choose.y = ny;
                            for (int i = 0 ; i < pieces.length ; i++){
                                if (pieces[i] == null) continue;
                                if (pieces[i].getSite().x == choose.x && pieces[i].getSite().y == choose.y) {
                                    nowChess = pieces[i].getIndex();
                                    chess = i;
                                    break;
                                }
                            }
                            sound.play(click,(float) 0.8, (float) 0.8, 0, 0, 1);
                            whereICanMove();
                        }
                    }
                }
                invalidate();
            }
            else {
                canMove.clear();
                isChoosing = false;
                invalidate();
            }
//        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(edgeW,edgeH);
        canvas.drawPath(pathBoard,paintBoard);
        canvas.drawPath(pathBoard,paintChess[nowCamp]);

        canvas.save();
        canvas.translate(0,heightFE*3);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.translate(widthFE,-heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE*2,0);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE*2,0);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,-heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,heightFE);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.restore();

        canvas.save();
        canvas.translate(0,heightFE*6);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.translate(widthFE,heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,-heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE*2,0);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE*2,0);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,heightFE);
        canvas.drawPath(pathBoardDecorOne,paintChess[nowCamp]);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.translate(widthFE,-heightFE);
        canvas.drawPath(pathBoardDecorTwo,paintChess[nowCamp]);
        canvas.restore();

        canvas.save();
        canvas.translate(2*widthFE,4.5f*heightFE);
        canvas.drawText(stringRiver,0,adjustTextY,paintText[nowCamp]);
        canvas.restore();
        canvas.save();
        canvas.translate(6*widthFE,4.5f*heightFE);
        canvas.rotate(180);
        canvas.drawText(stringSide,0,adjustTextY,paintText[nowCamp]);
        canvas.restore();

        for (int i = 0 ; i < pieces.length ; i++){
            if (pieces[i] == null) continue;
            canvas.save();
            canvas.translate(pieces[i].getSite().x * widthFE,pieces[i].getSite().y * heightFE);
            if (pieces[i].getCamp() == 1)
                canvas.rotate(180);
            canvas.drawPath(pathChess,paintWhite);
            canvas.drawPath(pathChess,paintChess[pieces[i].getCamp()]);
            canvas.drawText(pieces[i].getPieceName(),0,adjustTextY,paintText[pieces[i].getCamp()]);

            canvas.restore();
        }

        if (isChoosing) {
            canvas.save();
            canvas.translate(choose.x * widthFE , choose.y * heightFE);
            canvas.drawPath(pathChess, paintSelect);
            canvas.restore();
            for (int i = 0 ; i < canMove.size() ; i ++){
                canvas.save();
                canvas.translate(canMove.get(i).x * widthFE , canMove.get(i).y * heightFE);
                canvas.drawPath(pathChess, paintCanMove);
                canvas.restore();
            }
        }
        canvas.translate(-edgeW,-edgeH);
        if (winner != -1){
            canvas.drawRoundRect(screenWidth*0.2f ,screenHeight*0.4f , screenWidth*0.8f,screenHeight*0.6f,5,5,paintSelect);
            canvas.drawRoundRect(screenWidth*0.2f ,screenHeight*0.4f , screenWidth*0.8f,screenHeight*0.6f,5,5,paintWhite);
            canvas.translate(screenWidth/2,screenHeight/2);
            canvas.drawText(winText[winner],0,adjustTextY,paintText[winner]);
        }
    }

    private boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    private void setChessSite(){
        for (int i = 0 ; i < pieces.length ; i++) {
            switch (i){
                case 0:
                    pieces[i].setSite(4,9);
                    break;
                case 1:
                    pieces[i].setSite(3,9);
                    break;
                case 2:
                    pieces[i].setSite(5,9);
                    break;
                case 3:
                    pieces[i].setSite(2,9);
                    break;
                case 4:
                    pieces[i].setSite(6,9);
                    break;
                case 5:
                    pieces[i].setSite(0,9);
                    break;
                case 6:
                    pieces[i].setSite(8,9);
                    break;
                case 7:
                    pieces[i].setSite(1,9);
                    break;
                case 8:
                    pieces[i].setSite(7,9);
                    break;
                case 9:
                    pieces[i].setSite(1,7);
                    break;
                case 10:
                    pieces[i].setSite(7,7);
                    break;
                case 11:
                    pieces[i].setSite(0,6);
                    break;
                case 12:
                    pieces[i].setSite(2,6);
                    break;
                case 13:
                    pieces[i].setSite(4,6);
                    break;
                case 14:
                    pieces[i].setSite(6,6);
                    break;
                case 15:
                    pieces[i].setSite(8,6);
                    break;
                case 16:
                    pieces[i].setSite(4,0);
                    break;
                case 17:
                    pieces[i].setSite(3,0);
                    break;
                case 18:
                    pieces[i].setSite(5,0);
                    break;
                case 19:
                    pieces[i].setSite(2,0);
                    break;
                case 20:
                    pieces[i].setSite(6,0);
                    break;
                case 21:
                    pieces[i].setSite(0,0);
                    break;
                case 22:
                    pieces[i].setSite(8,0);
                    break;
                case 23:
                    pieces[i].setSite(1,0);
                    break;
                case 24:
                    pieces[i].setSite(7,0);
                    break;
                case 25:
                    pieces[i].setSite(1,2);
                    break;
                case 26:
                    pieces[i].setSite(7,2);
                    break;
                case 27:
                    pieces[i].setSite(0,3);
                    break;
                case 28:
                    pieces[i].setSite(2,3);
                    break;
                case 29:
                    pieces[i].setSite(4,3);
                    break;
                case 30:
                    pieces[i].setSite(6,3);
                    break;
                case 31:
                    pieces[i].setSite(8,3);
                    break;

            }
        }
    }

    private boolean isKingFaceKing(int x , int y){
        pieces[chess].setSite(x,y);
        int k = isNull[x][y];
        isNull[x][y] = nowCamp;
        isNull[choose.x][choose.y] = -1;
        if (pieces[0].getSite().x == pieces[16].getSite().x){
            for (int i = pieces[16].getSite().y+1 ; i < pieces[0].getSite().y ; i++)
                if (isNull[pieces[0].getSite().x][i] != -1) {
                    pieces[chess].setSite(choose.x,choose.y);
                    isNull[x][y] = k;
                    isNull[choose.x][choose.y] = nowCamp;
                    return false;
                }
            pieces[chess].setSite(choose.x,choose.y);
            isNull[x][y] = k;
            isNull[choose.x][choose.y] = nowCamp;
            return true;
        }
        pieces[chess].setSite(choose.x,choose.y);
        isNull[x][y] = k;
        isNull[choose.x][choose.y] = nowCamp;
        return false;
    }

    private void whereICanMove(){
        int i;
        switch (nowChess){
            case 0:
                if (nowCamp == 1) {
                    if (choose.x > 3)
                        if (isNull[choose.x - 1][choose.y] != 1 && !isKingFaceKing(choose.x - 1, choose.y))
                            canMove.add(new Point(choose.x - 1, choose.y));
                    if (choose.x < 5)
                        if (isNull[choose.x + 1][choose.y] != 1 && !isKingFaceKing(choose.x + 1, choose.y))
                            canMove.add(new Point(choose.x + 1, choose.y));
                    if (choose.y > 0)
                        if (isNull[choose.x][choose.y - 1] != 1 && !isKingFaceKing(choose.x, choose.y - 1))
                            canMove.add(new Point(choose.x, choose.y - 1));
                    if (choose.y < 2)
                        if (isNull[choose.x][choose.y + 1] != 1 && !isKingFaceKing(choose.x, choose.y + 1))
                            canMove.add(new Point(choose.x, choose.y + 1));
                }
                else {
                    if (choose.x > 3)
                        if (isNull[choose.x - 1][choose.y] != 0 && !isKingFaceKing(choose.x - 1, choose.y))
                            canMove.add(new Point(choose.x - 1, choose.y));
                    if (choose.x < 5)
                        if (isNull[choose.x + 1][choose.y] != 0 && !isKingFaceKing(choose.x + 1, choose.y))
                            canMove.add(new Point(choose.x + 1, choose.y));
                    if (choose.y > 7)
                        if (isNull[choose.x][choose.y - 1] != 0 && !isKingFaceKing(choose.x, choose.y - 1))
                            canMove.add(new Point(choose.x, choose.y - 1));
                    if (choose.y < 9)
                        if (isNull[choose.x][choose.y + 1] != 0 && !isKingFaceKing(choose.x, choose.y + 1))
                            canMove.add(new Point(choose.x, choose.y + 1));
                }
                break;
            case 1:
                if (choose.y == 0 || choose.y == 2){
                    if (isNull[4][1] != 1 && !isKingFaceKing(4,1))
                        canMove.add(new Point(4,1));
                }
                else if(choose.y == 1){
                    if (isNull[3][0] != 1 && !isKingFaceKing(3,0))
                        canMove.add(new Point(3,0));
                    if (isNull[5][0] != 1 && !isKingFaceKing(5,0))
                        canMove.add(new Point(5,0));
                    if (isNull[3][2] != 1 && !isKingFaceKing(3,2))
                        canMove.add(new Point(3,2));
                    if (isNull[5][2] != 1 && !isKingFaceKing(5,2))
                        canMove.add(new Point(5,2));
                }
                else if (choose.y == 7 || choose.y == 9) {
                    if (isNull[4][8] != 0 && !isKingFaceKing(4,8))
                        canMove.add(new Point(4, 8));
                }
                else if (choose.y == 8){
                    if (isNull[3][7] != 0 && !isKingFaceKing(3,7))
                        canMove.add(new Point(3,7));
                    if (isNull[5][7] != 0 && !isKingFaceKing(5,7))
                        canMove.add(new Point(5,7));
                    if (isNull[3][9] != 0 && !isKingFaceKing(3,9))
                        canMove.add(new Point(3,9));
                    if (isNull[5][9] != 0 && !isKingFaceKing(5,9))
                        canMove.add(new Point(5,9));
                }
                break;
            case 2:
                if (nowCamp == 1) {
                    if (!(choose.x - 2 < 0 || choose.y - 2 < 0))
                        if (isNull[choose.x - 2][choose.y - 2] != isNull[choose.x][choose.y] && isNull[choose.x - 1][choose.y - 1] == -1 && !isKingFaceKing(choose.x - 2, choose.y - 2))
                            canMove.add(new Point(choose.x - 2, choose.y - 2));
                    if (!(choose.x + 2 > 8 || choose.y - 2 < 0))
                        if (isNull[choose.x + 2][choose.y - 2] != isNull[choose.x][choose.y] && isNull[choose.x + 1][choose.y - 1] == -1 && !isKingFaceKing(choose.x + 2, choose.y - 2))
                            canMove.add(new Point(choose.x + 2, choose.y - 2));
                    if (!(choose.x - 2 < 0 || choose.y + 2 > 5))
                        if (isNull[choose.x - 2][choose.y + 2] != isNull[choose.x][choose.y] && isNull[choose.x - 1][choose.y + 1] == -1 && !isKingFaceKing(choose.x - 2, choose.y + 2))
                            canMove.add(new Point(choose.x - 2, choose.y + 2));
                    if (!(choose.x + 2 > 8 || choose.y + 2 > 5))
                        if (isNull[choose.x + 2][choose.y + 2] != isNull[choose.x][choose.y] && isNull[choose.x + 1][choose.y + 1] == -1 && !isKingFaceKing(choose.x + 2, choose.y + 2))
                            canMove.add(new Point(choose.x + 2, choose.y + 2));
                }
                else {
                    if (!(choose.x - 2 < 0 || choose.y - 2 < 4))
                        if (isNull[choose.x - 2][choose.y - 2] != isNull[choose.x][choose.y] && isNull[choose.x - 1][choose.y - 1] == -1 && !isKingFaceKing(choose.x - 2, choose.y - 2))
                            canMove.add(new Point(choose.x - 2, choose.y - 2));
                    if (!(choose.x + 2 > 8 || choose.y - 2 < 4))
                        if (isNull[choose.x + 2][choose.y - 2] != isNull[choose.x][choose.y] && isNull[choose.x + 1][choose.y - 1] == -1 && !isKingFaceKing(choose.x + 2, choose.y - 2))
                            canMove.add(new Point(choose.x + 2, choose.y - 2));
                    if (!(choose.x - 2 < 0 || choose.y + 2 > 9))
                        if (isNull[choose.x - 2][choose.y + 2] != isNull[choose.x][choose.y] && isNull[choose.x - 1][choose.y + 1] == -1 && !isKingFaceKing(choose.x - 2, choose.y + 2))
                            canMove.add(new Point(choose.x - 2, choose.y + 2));
                    if (!(choose.x + 2 > 8 || choose.y + 2 > 9))
                        if (isNull[choose.x + 2][choose.y + 2] != isNull[choose.x][choose.y] && isNull[choose.x + 1][choose.y + 1] == -1 && !isKingFaceKing(choose.x + 2, choose.y + 2))
                            canMove.add(new Point(choose.x + 2, choose.y + 2));
                }
                break;
            case 3:
                i = 1;
                while (true){
                    if (choose.x+i == 9) break;
                    else if (isNull[choose.x+i][choose.y] == -1 && !isKingFaceKing(choose.x + i, choose.y)) {
                        canMove.add(new Point(choose.x + i, choose.y));
                        i++;
                    }
                    else if (isNull[choose.x+i][choose.y] != isNull[choose.x][choose.y] && !isKingFaceKing(choose.x + i, choose.y)) {
                        canMove.add(new Point(choose.x+i, choose.y));
                        break;
                    }
                    else    break;
                }
                i = 1;
                while (true){
                    if (choose.x-i == -1) break;
                    else if (isNull[choose.x-i][choose.y] == -1 && !isKingFaceKing(choose.x-i, choose.y)) {
                        canMove.add(new Point(choose.x-i, choose.y));
                        i++;
                    }
                    else if (isNull[choose.x-i][choose.y] != isNull[choose.x][choose.y] && !isKingFaceKing(choose.x-i, choose.y)) {
                        canMove.add(new Point(choose.x-i, choose.y));
                        break;
                    }
                    else    break;
                }
                i = 1;
                while (true){
                    if (choose.y+i == 10) break;
                    else if (isNull[choose.x][choose.y+i] == -1) {
                        canMove.add(new Point(choose.x, choose.y+i));
                        i++;
                    }
                    else if (isNull[choose.x][choose.y+i] != isNull[choose.x][choose.y]) {
                        canMove.add(new Point(choose.x, choose.y+i));
                        break;
                    }
                    else    break;
                }
                i = 1;
                while (true){
                    if (choose.y-i == -1) break;
                    else if (isNull[choose.x][choose.y-i] == -1) {
                        canMove.add(new Point(choose.x, choose.y-i));
                        i++;
                    }
                    else if (isNull[choose.x][choose.y-i] != isNull[choose.x][choose.y]) {
                        canMove.add(new Point(choose.x, choose.y-i));
                        break;
                    }
                    else    break;
                }
                break;
            case 4:
                if (isKingFaceKing((choose.x+1)%9,choose.y))
                    break;
                if (choose.x-2 >= 0 && choose.y-1 >= 0)
                    if (isNull[choose.x-2][choose.y-1] != isNull[choose.x][choose.y] && isNull[choose.x-1][choose.y] == -1)
                        canMove.add(new Point(choose.x-2, choose.y-1));
                if (choose.x-1 >= 0 && choose.y-2 >= 0)
                    if (isNull[choose.x-1][choose.y-2] != isNull[choose.x][choose.y] && isNull[choose.x][choose.y-1] == -1)
                        canMove.add(new Point(choose.x-1, choose.y-2));
                if (choose.x-2 >= 0 && choose.y+1 <= 9)
                    if (isNull[choose.x-2][choose.y+1] != isNull[choose.x][choose.y] && isNull[choose.x-1][choose.y] == -1)
                        canMove.add(new Point(choose.x-2, choose.y+1));
                if (choose.x-1 >= 0 && choose.y+2 <= 9)
                    if (isNull[choose.x-1][choose.y+2] != isNull[choose.x][choose.y] && isNull[choose.x][choose.y+1] == -1)
                        canMove.add(new Point(choose.x-1, choose.y+2));
                if (choose.x+2 <= 8 && choose.y-1 >= 0)
                    if (isNull[choose.x+2][choose.y-1] != isNull[choose.x][choose.y] && isNull[choose.x+1][choose.y] == -1)
                        canMove.add(new Point(choose.x+2, choose.y-1));
                if (choose.x+1 <= 8 && choose.y-2 >= 0)
                    if (isNull[choose.x+1][choose.y-2] != isNull[choose.x][choose.y] && isNull[choose.x][choose.y-1] == -1)
                        canMove.add(new Point(choose.x+1, choose.y-2));
                if (choose.x+2 <= 8 && choose.y+1 <= 9)
                    if (isNull[choose.x+2][choose.y+1] != isNull[choose.x][choose.y] && isNull[choose.x+1][choose.y] == -1)
                        canMove.add(new Point(choose.x+2, choose.y+1));
                if (choose.x+1 <= 8 && choose.y+2 <= 9)
                    if (isNull[choose.x+1][choose.y+2] != isNull[choose.x][choose.y] && isNull[choose.x][choose.y+1] == -1)
                        canMove.add(new Point(choose.x+1, choose.y+2));
                break;
            case 5:
                i = 1;
                while (true){
                    if (isKingFaceKing((choose.x+1)%9,choose.y))
                        break;
                    if (choose.x+i == 9) break;
                    else if (isNull[choose.x+i][choose.y] == -1) {
                        canMove.add(new Point(choose.x + i, choose.y));
                        i++;
                    }
                    else {
                        while (true){
                            i++;
                            if (choose.x+i == 9) break;
                            if (isNull[choose.x+i][choose.y] == -1) continue;
                            if (isNull[choose.x+i][choose.y] != isNull[choose.x][choose.y]) {
                                canMove.add(new Point(choose.x + i, choose.y));
                                break;
                            }
                            else if (choose.x+i == 9) break;
                        }
                        break;
                    }
                }
                i = 1;
                while (true){
                    if (isKingFaceKing((choose.x+1)%9,choose.y))
                        break;
                    if (choose.x-i == -1) break;
                    else if (isNull[choose.x-i][choose.y] == -1) {
                        canMove.add(new Point(choose.x-i, choose.y));
                        i++;
                    }
                    else {
                        while (true){
                            i++;
                            if (choose.x-i == -1) break;
                            if (isNull[choose.x-i][choose.y] == -1) continue;
                            if (isNull[choose.x-i][choose.y] != isNull[choose.x][choose.y]) {
                                canMove.add(new Point(choose.x - i, choose.y));
                                break;
                            }
                            else if (choose.x-i == -1) break;
                        }
                        break;
                    }
                }
                i = 1;
                while (true){
                    if (choose.y+i == 10) break;
                    else if (isNull[choose.x][choose.y+i] == -1) {
                        canMove.add(new Point(choose.x, choose.y+i));
                        i++;
                    }
                    else {
                        while (true){
                            i++;
                            if (choose.y+i == 10) break;
                            if (isNull[choose.x][choose.y+i] == -1) continue;
                            if (isNull[choose.x][choose.y+i] != isNull[choose.x][choose.y] && !isKingFaceKing(choose.x , choose.y+i)) {
                                canMove.add(new Point(choose.x, choose.y+i));
                                break;
                            }
                            else if (choose.y+i == 10) break;
                        }
                        break;
                    }
                }
                i = 1;
                while (true){
                    if (choose.y-i == -1) break;
                    else if (isNull[choose.x][choose.y-i] == -1) {
                        canMove.add(new Point(choose.x, choose.y-i));
                        i++;
                    }
                    else {
                        while (true){
                            i++;
                            if (choose.y-i == -1) break;
                            if (isNull[choose.x][choose.y-i] == -1) continue;
                            if (isNull[choose.x][choose.y-i] != isNull[choose.x][choose.y] && !isKingFaceKing(choose.x , choose.y-i)) {
                                canMove.add(new Point(choose.x, choose.y-i));
                                break;
                            }
                            else if (choose.y-i == -1) break;
                        }
                        break;
                    }
                }
                break;
            case 6:
                if (nowCamp == 1){
                    if (choose.y >= 5){
                        if (isNull[choose.x - 1][choose.y] != 1 && !isKingFaceKing(choose.x - 1, choose.y))
                            canMove.add(new Point(choose.x - 1, choose.y));
                        if (isNull[choose.x + 1][choose.y] != 1 && !isKingFaceKing(choose.x + 1, choose.y))
                            canMove.add(new Point(choose.x + 1, choose.y));
                        if (isNull[choose.x][choose.y + 1] != 1)
                            canMove.add(new Point(choose.x, choose.y + 1));
                    }
                    else {
                        if (isNull[choose.x][choose.y + 1] != 1)
                            canMove.add(new Point(choose.x, choose.y + 1));
                    }

                }
                else {
                    if (choose.y <= 4){
                        if (isNull[choose.x - 1][choose.y] != 0 && !isKingFaceKing(choose.x - 1, choose.y))
                            canMove.add(new Point(choose.x - 1, choose.y));
                        if (isNull[choose.x + 1][choose.y] != 0 && !isKingFaceKing(choose.x + 1, choose.y))
                            canMove.add(new Point(choose.x + 1, choose.y));
                        if (isNull[choose.x][choose.y - 1] != 0)
                            canMove.add(new Point(choose.x, choose.y - 1));
                    }
                    else {
                        if (isNull[choose.x][choose.y - 1] != 0)
                            canMove.add(new Point(choose.x, choose.y - 1));
                    }

                }
                break;
        }
    }

}