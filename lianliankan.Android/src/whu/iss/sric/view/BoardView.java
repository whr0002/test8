package whu.iss.sric.view;

import java.util.ArrayList;
import java.util.List;

import whu.iss.sric.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathDashPathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BoardView extends View {

	/**
	 * xCount x轴方向的图标数+1
	 */
	protected static final int xCount = 9;
	/**
	 * yCount y轴方向的图标数+1
	 */
	protected static final int yCount = 12;
	/**
	 * map 连连看游戏棋盘
	 */
	protected int[][] map = new int[xCount][yCount];
	/**
	 * iconSize 图标大小
	 */
	protected int iconSize;
	/**
	 * iconCounts 图标的数目
	 */
	protected int iconCounts = 16;
	/**
	 * icons 所有的图片
	 */
	protected Bitmap[] icons = new Bitmap[iconCounts];

	/**
	 * path 可以连通点的路径
	 */
	private Point[] path = null;
	/**
	 * selected 选中的图标
	 */
	protected List<Point> selected = new ArrayList<Point>();

	public BoardView(Context context, AttributeSet atts) {
		super(context, atts);

		calIconSize();

		Resources r = getResources();
		// loadBitmaps(1, r.getDrawable(R.drawable.fruit_01));
		// loadBitmaps(2, r.getDrawable(R.drawable.fruit_02));
		// loadBitmaps(3, r.getDrawable(R.drawable.fruit_03));
		// loadBitmaps(4, r.getDrawable(R.drawable.fruit_04));
		// loadBitmaps(5, r.getDrawable(R.drawable.fruit_05));
		// loadBitmaps(6, r.getDrawable(R.drawable.fruit_06));
		// loadBitmaps(7, r.getDrawable(R.drawable.fruit_07));
		// loadBitmaps(8, r.getDrawable(R.drawable.fruit_08));
		// loadBitmaps(9, r.getDrawable(R.drawable.fruit_09));
		// loadBitmaps(10, r.getDrawable(R.drawable.fruit_10));
		// loadBitmaps(11, r.getDrawable(R.drawable.fruit_11));
		// loadBitmaps(12, r.getDrawable(R.drawable.fruit_12));
		// loadBitmaps(13, r.getDrawable(R.drawable.fruit_13));
		// loadBitmaps(14, r.getDrawable(R.drawable.fruit_14));
		// loadBitmaps(15, r.getDrawable(R.drawable.fruit_15));
		// loadBitmaps(16, r.getDrawable(R.drawable.fruit_17));
		// loadBitmaps(17, r.getDrawable(R.drawable.fruit_18));
		// loadBitmaps(18, r.getDrawable(R.drawable.fruit_19));

		loadBitmaps(1, r.getDrawable(R.drawable.shadow1));
		loadBitmaps(2, r.getDrawable(R.drawable.shadow2));
		loadBitmaps(3, r.getDrawable(R.drawable.shadow3));
		loadBitmaps(4, r.getDrawable(R.drawable.shadow4));
		loadBitmaps(5, r.getDrawable(R.drawable.shadow5));
		loadBitmaps(6, r.getDrawable(R.drawable.shadow6));
		loadBitmaps(7, r.getDrawable(R.drawable.shadow7));
		loadBitmaps(8, r.getDrawable(R.drawable.shadow8));
		loadBitmaps(9, r.getDrawable(R.drawable.shadow9));
		loadBitmaps(10, r.getDrawable(R.drawable.shadow10));
		loadBitmaps(11, r.getDrawable(R.drawable.shadow11));
		loadBitmaps(12, r.getDrawable(R.drawable.shadow12));
		loadBitmaps(13, r.getDrawable(R.drawable.shadow13));
		loadBitmaps(14, r.getDrawable(R.drawable.shadow14));
		loadBitmaps(15, r.getDrawable(R.drawable.shadow15));
	}

	/**
	 * 
	 * 计算图标的长宽
	 */
	private void calIconSize() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		iconSize = dm.widthPixels / (xCount);
		// iconSize = (dm.widthPixels+106)/(xCount);
	}

	/**
	 * 
	 * @param key
	 *            特定图标的标识
	 * @param d
	 *            drawable下的资源
	 */
	public void loadBitmaps(int key, Drawable d) {
		// Options options = new BitmapFactory.Options();
		// options.inScaled = false;
		// options.inDither = false;
		// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id,
		// options);
		// Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, iconSize, iconSize);
		d.draw(canvas);
		icons[key] = bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		/**
		 * 绘制连通路径，然后将路径以及两个图标清除
		 */

		if (path != null && path.length >= 2) {
			Path mPath = new Path();

			Path pathShapeOutter = new Path();
			// pathShapeOutter.addCircle(0, 0, iconSize/8, Direction.CCW);
			Path pathShapeInner = new Path();
			// pathShapeInner.addCircle(0, 0, iconSize/12, Direction.CCW);
			float phase = 0;
			float advance = iconSize / 4;
			PathDashPathEffect.Style style = PathDashPathEffect.Style.ROTATE;

			Paint innerPaint = new Paint();
			innerPaint.setColor(getContext().getResources().getColor(
					R.color.brown));
			innerPaint.setStyle(Paint.Style.STROKE);
			innerPaint.setStrokeWidth(5);

			Paint outerPaint = new Paint();
			outerPaint.setColor(getContext().getResources().getColor(
					R.color.dark_brown));
			outerPaint.setStyle(Paint.Style.STROKE);
			outerPaint.setStrokeWidth(10);

			// Effects
			float[] intervals = new float[] { 20.0f, 20.0f };
			DashPathEffect dashPathEffect = new DashPathEffect(intervals, phase);
			//
			// float radius = 50.0f;
			// CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);

//			PathDashPathEffect pathDashPathEffect = new PathDashPathEffect(
//					pathShapeInner, advance, phase, style);
//			pathDashPathEffect = new PathDashPathEffect(pathShapeOutter,
//					advance, phase, style);
			
//			innerPaint.setPathEffect(dashPathEffect);
//			outerPaint.setPathEffect(dashPathEffect);

			Point mP = indextoScreen(path[0].x, path[0].y);
			mPath.moveTo(mP.x + iconSize / 2, mP.y + iconSize / 2);
			
			for (int i = 1; i < path.length; i++) {
				Point p1 = indextoScreen(path[i].x, path[i].y);
				mPath.lineTo(p1.x + iconSize / 2, p1.y + iconSize / 2);
			}
			if (mPath != null) {
				canvas.drawPath(mPath, outerPaint);
				canvas.drawPath(mPath, innerPaint);
			}
			// for (int i = 0; i < path.length - 1; i++) {
			//
			// Point p1 = indextoScreen(path[i].x, path[i].y);
			// Point p2 = indextoScreen(path[i + 1].x, path[i + 1].y);
			//
			// mPath = new Path();
			// mPath.moveTo(p1.x + iconSize / 2, p1.y + iconSize / 2);
			// mPath.lineTo(p2.x + iconSize / 2, p2.y + iconSize / 2);
			// canvas.drawPath(mPath, outerPaint);
			// canvas.drawPath(mPath, innerPaint);
			// }

			Point p = path[0];
			map[p.x][p.y] = 0;
			p = path[path.length - 1];
			map[p.x][p.y] = 0;
			selected.clear();
			path = null;
		}
		/**
		 * 绘制棋盘的所有图标 当这个坐标内的值大于0时绘制
		 */
		for (int x = 0; x < map.length; x += 1) {
			for (int y = 0; y < map[x].length; y += 1) {
				if (map[x][y] > 0) {
					Point p = indextoScreen(x, y);
					canvas.drawBitmap(icons[map[x][y]], p.x, p.y, null);
				}
			}
		}

		/**
		 * 绘制选中图标，当选中时图标放大显示
		 */
		for (Point position : selected) {
			Point p = indextoScreen(position.x, position.y);
			if (map[position.x][position.y] >= 1) {
				canvas.drawBitmap(icons[map[position.x][position.y]], null,
						new Rect(p.x - 5, p.y - 5, p.x + iconSize + 5, p.y
								+ iconSize + 5), null);
			}
		}
	}

	/**
	 * 
	 * @param path
	 */
	public void drawLine(Point[] path) {
		this.path = path;
		this.invalidate();
	}

	/**
	 * 工具方法
	 * 
	 * @param x
	 *            数组中的横坐标
	 * @param y
	 *            数组中的纵坐标
	 * @return 将图标在数组中的坐标转成在屏幕上的真实坐标
	 */
	public Point indextoScreen(int x, int y) {
		return new Point(x * iconSize, y * iconSize);
	}

	/**
	 * 工具方法
	 * 
	 * @param x
	 *            屏幕中的横坐标
	 * @param y
	 *            屏幕中的纵坐标
	 * @return 将图标在屏幕中的坐标转成在数组上的虚拟坐标
	 */
	public Point screenToindex(int x, int y) {
		int ix = x / iconSize;
		int iy = y / iconSize;
		if (ix < xCount && iy < yCount) {
			return new Point(ix, iy);
		} else {
			return new Point(0, 0);
		}
	}
}
