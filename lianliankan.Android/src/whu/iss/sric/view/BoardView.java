package whu.iss.sric.view;

import java.util.ArrayList;
import java.util.List;

import whu.iss.sric.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
	 * xCount x�᷽���ͼ����+1
	 */
	protected static final int xCount = 9;
	/**
	 * yCount y�᷽���ͼ����+1
	 */
	protected static final int yCount = 12;
	/**
	 * map ��������Ϸ����
	 */
	protected int[][] map = new int[xCount][yCount];
	/**
	 * iconSize ͼ���С
	 */
	protected int iconSize;
	/**
	 * iconCounts ͼ�����Ŀ
	 */
	protected int iconCounts = 16;
	/**
	 * icons ���е�ͼƬ
	 */
	protected Bitmap[] icons = new Bitmap[iconCounts];

	/**
	 * path ������ͨ���·��
	 */
	private Point[] path = null;
	/**
	 * selected ѡ�е�ͼ��
	 */
	protected List<Point> selected = new ArrayList<Point>();

	private boolean shouldAnimate = false;
	private Bitmap animBitmap1;
	private Bitmap animBitmap2;
	private Point animPoint1;
	private Point animPoint2;
	private int size = 1;
	private Paint innerPaint;
	private Paint outerPaint;
	private Path mPath;
	private float iconSizeF;

	public BoardView(Context context, AttributeSet atts) {
		super(context, atts);

		calIconSize();
		initializePaint();

		Resources r = getResources();
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
	 * ����ͼ��ĳ���
	 */
	private void calIconSize() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		iconSize = dm.widthPixels / (xCount);
		iconSizeF = (float)dm.widthPixels / (xCount);
		// iconSize = (dm.widthPixels+106)/(xCount);
	}

	/**
	 * 
	 * @param key
	 *            �ض�ͼ��ı�ʶ
	 * @param d
	 *            drawable�µ���Դ
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

	public void initializePaint() {
		innerPaint = new Paint();
		outerPaint = new Paint();
		Path pathShapeOutter = new Path();
		Path pathShapeInner = new Path();
		// float[] intervals = new float[] { 20.0f, 20.0f };
		// float radius = 50.0f;
		float phase = iconSizeF / 8;
		float advance = iconSizeF / 4;
		float radiusOut = iconSizeF / 8;
		float radiusIn = iconSizeF / 12;
		
		pathShapeOutter.addCircle(0, 0, radiusOut, Direction.CCW);
		pathShapeInner.addCircle(0, 0, radiusIn, Direction.CCW);
		PathDashPathEffect.Style style = PathDashPathEffect.Style.ROTATE;

		
		innerPaint
				.setColor(getContext().getResources().getColor(R.color.brown));
		innerPaint.setStyle(Paint.Style.STROKE);
		innerPaint.setStrokeWidth(radiusIn);
	
		outerPaint.setColor(getContext().getResources().getColor(
				R.color.dark_brown));
		outerPaint.setStyle(Paint.Style.STROKE);
		outerPaint.setStrokeWidth(radiusOut);

		// Effects
		// Dashed line
		// DashPathEffect dashPathEffect = new DashPathEffect(intervals, phase);
		// Rounded corner line
		// CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);

		// Circle line

		PathDashPathEffect circleEffectInner = new PathDashPathEffect(
				pathShapeInner, advance, phase, style);
		PathDashPathEffect circleEffectOutter = new PathDashPathEffect(
				pathShapeOutter, advance, phase, style);

		innerPaint.setPathEffect(circleEffectInner);
		outerPaint.setPathEffect(circleEffectOutter);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/**
		 * ������ͨ·����Ȼ��·���Լ�����ͼ�����
		 */

		if (path != null && path.length >= 2) {

			mPath = new Path();

			Point mP = indextoScreen(path[0].x, path[0].y);
			mPath.moveTo(mP.x + iconSize / 2, mP.y + iconSize / 2);

			for (int i = 1; i < path.length; i++) {
				Point p1 = indextoScreen(path[i].x, path[i].y);
				mPath.lineTo(p1.x + iconSize / 2, p1.y + iconSize / 2);
			}
			// if (mPath != null) {
			// cpyPath = new Path(mPath);
			// canvas.drawPath(mPath, outerPaint);
			// canvas.drawPath(mPath, innerPaint);
			// }
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
			animPoint1 = indextoScreen(p.x, p.y);
			animBitmap1 = icons[map[p.x][p.y]];
			map[p.x][p.y] = 0;

			p = path[path.length - 1];
			animPoint2 = indextoScreen(p.x, p.y);
			animBitmap2 = icons[map[p.x][p.y]];
			map[p.x][p.y] = 0;
			selected.clear();
			path = null;

			shouldAnimate = true;
			// RunAnimate runAnimation = new RunAnimate(animBitmap1,
			// animBitmap2,
			// animPoint1, animPoint2, canvas);
			// runAnimation.start();
		}

		if (shouldAnimate) {

			if (size < iconSize) {
				canvas.drawPath(mPath, outerPaint);
				canvas.drawPath(mPath, innerPaint);

				canvas.drawBitmap(animBitmap1, null, new Rect(animPoint1.x
						+ size, animPoint1.y + size, animPoint1.x + iconSize
						- size, animPoint1.y + iconSize - size), null);

				canvas.drawBitmap(animBitmap2, null, new Rect(animPoint2.x
						+ size, animPoint2.y + size, animPoint2.x + iconSize
						- size, animPoint2.y + iconSize - size), null);

				size += 3;
				invalidate();
				// invalidate(new Rect(animPoint1.x, animPoint1.y, animPoint1.x
				// + iconSize
				// , animPoint1.y + iconSize));
				// invalidate(new Rect(animPoint2.x, animPoint2.y, animPoint2.x
				// + iconSize
				// , animPoint2.y + iconSize));
			} else {
				shouldAnimate = false;
				size = 1;
			}
		}

		/**
		 * �������̵�����ͼ�� ����������ڵ�ֵ����0ʱ����
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
		 * ����ѡ��ͼ�꣬��ѡ��ʱͼ��Ŵ���ʾ
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
	 * ���߷���
	 * 
	 * @param x
	 *            �����еĺ�����
	 * @param y
	 *            �����е�������
	 * @return ��ͼ���������е�����ת������Ļ�ϵ���ʵ����
	 */
	public Point indextoScreen(int x, int y) {
		return new Point(x * iconSize, y * iconSize);
	}

	/**
	 * ���߷���
	 * 
	 * @param x
	 *            ��Ļ�еĺ�����
	 * @param y
	 *            ��Ļ�е�������
	 * @return ��ͼ������Ļ�е�����ת���������ϵ���������
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

	class RunAnimate extends Thread {

		private Bitmap mBitmap1, mBitmap2;
		private Point mPoint1, mPoint2;
		private int size;
		private Canvas mCanvas;

		public RunAnimate(Bitmap b1, Bitmap b2, Point p1, Point p2, Canvas c) {
			this.mBitmap1 = b1;
			this.mBitmap2 = b2;
			this.mPoint1 = p1;
			this.mPoint2 = p2;
			size = 1;
			this.mCanvas = c;
		}

		public void run() {
			while (size < iconSize) {
				// Draw Bitmaps
				if (size < iconSize) {
					mCanvas.drawBitmap(mBitmap1, null, new Rect(mPoint1.x
							+ size, mPoint1.y + size, mPoint1.x + iconSize
							- size, mPoint1.y + iconSize - size), null);

					mCanvas.drawBitmap(mBitmap2, null, new Rect(mPoint2.x
							+ size, mPoint2.y + size, mPoint2.x + iconSize
							- size, mPoint2.y + iconSize - size), null);
					// postInvalidateDelayed(0);
				}

				size++;
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
