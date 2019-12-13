package a.miracle.lib_zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

import a.miracle.lib_zxing.R;
import a.miracle.lib_zxing.camera.CameraManager;
import a.miracle.lib_zxing.utils.BitmapUtils;
import a.miracle.lib_zxing.utils.DisplayUtils;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 16L;
	private static final int OPAQUE = 0xFF;


	private final Paint paint;
	private final Paint paint_border;
	private final int line_border;
	private final int line_width;
	private final int line_height;
	private final int spc;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	private Bitmap scan_lineBitmap;
	private boolean isFirst;
	private int slideTop;
	private int slideBottom;
	/** 中间那条线每次刷新移动的距离 */
	private float speen_distance = 1;
	/** 扫描框中的中间线的宽度 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	/** 增量*/
	private float speed_index;
	private Path path;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint_border = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>(5);

		line_border = DisplayUtils.dp2px(1);
		line_width = DisplayUtils.dp2px(5);
		line_height = DisplayUtils.dp2px(20);
		spc = 0;

		paint_border.setStyle(Paint.Style.STROKE);
		paint_border.setStrokeWidth(line_border);
		paint_border.setColor(frameColor);
	}

	@Override
	public void onDraw(Canvas canvas) {
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}

		//初始化中间线图片
		if (scan_lineBitmap == null) {
			scan_lineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qr_scan_line);
			scan_lineBitmap = BitmapUtils.zoomBitmapScale(scan_lineBitmap, (frame.right - frame.left - line_width * 2) * 1.0f / scan_lineBitmap.getWidth(), 1);
		}

		if (path == null) {
			path = new Path();
			path.moveTo(frame.left, frame.top);
			path.lineTo(frame.right, frame.top);
			path.lineTo(frame.right, frame.bottom);
			path.lineTo(frame.left, frame.bottom);
			path.close();
		}

		//初始化中间线滑动的最上边和最下边
		if(!isFirst){
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
		canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
		canvas.drawRect(0, frame.bottom, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			paint.setColor(frameColor);
			// ┍┑┕┙┎┒┖┚

			canvas.drawPath(path, paint_border);

			//┎
			canvas.drawRect(spc + frame.left,
					spc + frame.top,
					spc + line_width + frame.left,
					spc + line_height + frame.top, paint);

			//┍
			canvas.drawRect(spc + frame.left,
					spc + frame.top,
					spc + line_height + frame.left,
					spc + line_width + frame.top, paint);

			//┒
			canvas.drawRect(-spc - line_width + frame.right,
					spc + frame.top,
					-spc + frame.right,
					spc + line_height + frame.top, paint);

			//┑
			canvas.drawRect(-spc - line_height + frame.right,
					spc + frame.top,
					-spc + frame.right,
					spc + line_width + frame.top, paint);

			//┖
			canvas.drawRect(spc + frame.left,
					-spc + -line_height + frame.bottom,
					spc + (line_width + frame.left),
					-spc + frame.bottom, paint);

			//┕
			canvas.drawRect(spc + frame.left,
					-spc - line_width + frame.bottom,
					spc + line_height + frame.left,
					-spc + frame.bottom, paint);

			//┚
			canvas.drawRect(-spc - line_width + frame.right,
					-spc - line_height + frame.bottom,
					-spc + frame.right,
					-spc + frame.bottom, paint);

			//┙
			canvas.drawRect(-spc - line_height + frame.right,
					-spc + -line_width + frame.bottom,
					-spc + frame.right,
					-spc + frame.bottom, paint);

			//绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			if(slideTop - frame.top < (frame.bottom - frame.top) * 0.5){
				speen_distance += (speed_index+0.2f);
			}else{
				speen_distance -= (speed_index-0.2f);
			}
			slideTop += speen_distance;
			if(slideTop >= frame.bottom - scan_lineBitmap.getHeight()){
				slideTop = frame.top;
				speed_index = 0;
				speen_distance = 1;
			}
			canvas.drawBitmap(scan_lineBitmap, frame.left + line_width, slideTop, null);

			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				// 小黄点
				/*possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}*/
			}
			// 小黄点
			/*if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}*/

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
