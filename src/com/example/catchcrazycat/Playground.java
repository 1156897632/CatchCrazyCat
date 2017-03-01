package com.example.catchcrazycat;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Playground extends SurfaceView implements OnTouchListener {
	
	boolean isWin = false;
//	int k = 1;
	private int WIDTH = 100;
	private static final int COL = 10;
	private static final int ROW = 10;
	private static final int BLOCKS = 10;

	private Dot matrix[][];
	private Dot cat;

	public Playground(Context context) {
		super(context);
		getHolder().addCallback(callback);
		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j] = new Dot(j, i);
			}
		}
		setOnTouchListener(this);
		initGame();
	}

	private Dot getDot(int x, int y) {
		return matrix[y][x];
	}
	
	private Boolean isAtEdge(Dot d){
		int x,y;
		x = d.getX();
		y = d.getY();
		if(x*y==0 || x==COL-1 || y==ROW-1){
			return true;
		}
		return false;
	}
	
	private Dot getNrighbour(Dot one,int dir){
		Dot temp = null;
		int x,y;
		x = one.getX();
		y = one.getY();
		switch(dir){
		case 1:
			temp = getDot(x-1, y);
			break;
		case 2:
			if(y%2==0){
				temp = getDot(x-1, y-1);
			}
			else{
				temp = getDot(x, y-1);
			}
			break;
		case 3:
			if(y%2==0){
				temp = getDot(x, y-1);
			}
			else{
				temp = getDot(x+1, y-1);
			}
			break;
		case 4:
			temp = getDot(x+1, y);
			break;
		case 5:
			if(y%2==0){
				temp = getDot(x, y+1);
			}
			else{
				temp = getDot(x+1, y+1);
			}
			break;
		case 6:
			if(y%2==0){
				temp = getDot(x-1, y+1);
			}
			else{
				temp = getDot(x, y+1);
			}
			break;
		}
		return temp;
	}
	
	private int getDistance(Dot one,int dir){
		int distance = 0;
		if(isAtEdge(one)){
			return 0;
		}
		Dot now,next;
		now = one;
		while(true){
			next = getNrighbour(now, dir);
			if(next.getStatus()==Dot.STATUS_ON){
				return distance*(-1);
			}
			else if(isAtEdge(next)){
				return distance+1;
			}
			distance++;
			now = next;
		}
	}
	
	private void CatMoveTo(Dot d){
		d.setStatus(Dot.STATUS_IN);
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
		cat.setXY(d.getX(), d.getY());
	}
	
	private void move(){
//		if(isAtEdge(cat)){//到达边界，游戏结束
//			lose();
//			return;
//		}
		Vector<Dot> avaliable = new Vector<Dot>();
		Vector<Dot> positive = new Vector<Dot>();
		HashMap<Dot, Integer>  hashmap = new HashMap<Dot, Integer>();
		for (int i = 1; i < 7; i++) {//计算cat周围的情况
			if(getNrighbour(cat, i).getStatus() == Dot.STATUS_OFF){
				hashmap.put(getNrighbour(cat, i), i);
				avaliable.add(getNrighbour(cat, i));
				if(getDistance(cat, i) > 0){//直接到边界的路
					positive.add(getNrighbour(cat, i));
				}
			}
		}
		if(avaliable.size() == 0){//直接被围住
//			win();
			isWin = true;
		}
		else{//没有直接被围住，游戏继续
			System.out.println("没有直接被围住，游戏继续");
			Dot best = null;
			if(positive.size() != 0){//有直接到达边界的路
				System.out.println("有直接到达边界的路");
				int min = 9999;
				for (int i = 0; i < positive.size(); i++) {
					int a = getDistance(cat, hashmap.get(positive.get(i)));
					if(a < min){
						min = a;
						best = positive.get(i);
					}
				}
			}
			else{//没有直接到达边界的路
				System.out.println("没有直接到达边界的路");
				int min = 0;
				for (int i = 0; i < avaliable.size(); i++) {
					int a = getDistance(cat, hashmap.get(avaliable.get(i)));
					if(a < min){
						min = a;
						System.out.println("最短距离的方向："+hashmap.get(avaliable.get(i)));
						System.out.println("最长距离："+min);
						best = avaliable.get(i);
					}
				}
			}
			CatMoveTo(best);
			System.out.println("cat move to the best next");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
	}
	
	private void lose(){
		Toast.makeText(getContext(), "you lose!", Toast.LENGTH_SHORT).show();
	}
	
	private void win(){
		Toast.makeText(getContext(), "you win!", Toast.LENGTH_SHORT).show();
	}
	
	
	// 绘制
	public void redraw() {
		Canvas c = getHolder().lockCanvas();
		c.drawColor(Color.LTGRAY);
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		for (int i = 0; i < ROW; i++) {
			int offset = 0;
			if (i % 2 != 0) {
				offset = WIDTH / 2;
			}
			for (int j = 0; j < COL; j++) {
				Dot one = getDot(j, i);
				switch (one.getStatus()) {
				case Dot.STATUS_OFF:
					paint.setColor(0xFFEEEEEE);
					break;
				case Dot.STATUS_ON:
					paint.setColor(0xFFFFAA00);
					break;
				case Dot.STATUS_IN:
					paint.setColor(0xFFFF0000);
					break;
				}
				c.drawOval(new RectF(one.getX() * WIDTH + offset, one.getY()
						* WIDTH, (one.getX() + 1) * WIDTH + offset,
						(one.getY() + 1) * WIDTH), paint);
			}
		}
		getHolder().unlockCanvasAndPost(c);
	}

	Callback callback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			redraw();
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			WIDTH = arg2 / (COL + 1);
			redraw();
		}
	};

	private void initGame() {
		System.out.println("初始化游戏");
		isWin = false;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				matrix[i][j].setStatus(SCREEN_STATE_OFF);
			}
		}
		cat = new Dot(4, 5);
		getDot(4, 5).setStatus(Dot.STATUS_IN);
//		System.out.println("cat set ok");
		for (int i = 0; i < BLOCKS;) {
			int x = (int) ((Math.random() * 1000) % ROW);
			int y = (int) ((Math.random() * 1000) % COL);
			if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
				getDot(x, y).setStatus(Dot.STATUS_ON);
				i++;
//				System.out.println("blocks:" + i);
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_UP) {
			// Toast.makeText(getContext(), e.getX()+":"+e.getY(),
			// Toast.LENGTH_SHORT).show();
			int x = 0, y;
			y = (int) (e.getY() / WIDTH);
			if (y % 2 == 0) {
				x = (int) (e.getX() / WIDTH);
			} else if (y % 2 != 0) {
				x = (int) ((e.getX() - WIDTH / 2) / WIDTH);
			}
			
			if (x+1>COL || y+1>ROW) {//在游戏区域外点击
				initGame();
			}
			else if(getDot(x,y).getStatus() == Dot.STATUS_OFF){//在游戏区域内点击
				getDot(x, y).setStatus(Dot.STATUS_ON);
				move();
			}
			redraw();
			if(isAtEdge(cat)){//到达边界，游戏结束
				lose();
			}
			if(isWin){
				win();
			}
		}
		return true;
	}
}
