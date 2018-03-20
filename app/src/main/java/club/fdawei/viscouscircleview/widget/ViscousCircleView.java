package club.fdawei.viscouscircleview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import club.fdawei.viscouscircleview.R;

/**
 * Created by david on 2018/3/19.
 */

public class ViscousCircleView extends View {

  private Paint paint;
  private int color;
  private int bigCircleRadius;
  private int smallCircleRadius;
  private int maxSpacing;
  private int during;//运动周期
  private float zoom;

  private long startTime;
  private int position;
  private float circleRadius;

  public ViscousCircleView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViscousCircleView);

    color = ta.getColor(R.styleable.ViscousCircleView_viewColor,
        context.getResources().getColor(R.color.colorPrimary));
    bigCircleRadius = ta.getDimensionPixelSize(R.styleable.ViscousCircleView_bigCircleRadius, 60);
    smallCircleRadius =
        ta.getDimensionPixelSize(R.styleable.ViscousCircleView_smallCircleRadius, 30);
    maxSpacing = ta.getDimensionPixelSize(R.styleable.ViscousCircleView_maxSpacing, 150);
    during = ta.getInteger(R.styleable.ViscousCircleView_during, 1500);
    zoom = ta.getFloat(R.styleable.ViscousCircleView_zoom, 0.9f);

    ta.recycle();
  }

  @Override protected void onDraw(Canvas canvas) {

    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }

    drawView(canvas);

    postInvalidateDelayed(50);
  }

  private void drawView(Canvas canvas) {
    int width = getMeasuredWidth();
    int height = getMeasuredHeight();

    if (paint == null) {
      paint = new Paint();
      paint.setColor(color);
      paint.setAntiAlias(true);
    }

    calculate();

    canvas.drawCircle(width * 0.5f, height * 0.5f, circleRadius, paint);
    canvas.drawCircle(width * 0.5f + position, height * 0.5f, smallCircleRadius, paint);
    if (Math.abs(position) < maxSpacing * 0.7f) {
      Path path = new Path();
      PointF controlPoint = new PointF(width * 0.5f + position * 0.5f, height * 0.5f);
      PointF pointA = new PointF(width * 0.5f + position, height * 0.5f + smallCircleRadius);
      PointF pointB = new PointF(width * 0.5f, height * 0.5f + circleRadius);
      PointF pointC = new PointF(width * 0.5f, height * 0.5f - circleRadius);
      PointF pointD = new PointF(width * 0.5f + position, height * 0.5f - smallCircleRadius);
      path.moveTo(pointA.x, pointA.y);
      path.quadTo(controlPoint.x, controlPoint.y, pointB.x, pointB.y);
      path.lineTo(pointC.x, pointC.y);
      path.quadTo(controlPoint.x, controlPoint.y, pointD.x, pointD.y);
      canvas.drawPath(path, paint);
    }
  }

  private void calculate() {
    double scale = getScale();
    position = (int) (-maxSpacing * scale);
    circleRadius = (float) (bigCircleRadius * (1 - (1 - zoom) * Math.abs(scale)));
  }

  private float getScale() {
    long t = System.currentTimeMillis();
    int x = (int) (t % during);
    int step = during / 4;
    if (x >= 0 && x < step) {
      return (float) x / step;
    } else if (x >= step && x < step * 3) {
      return 2 - (float) x / step;
    } else {
      return (float) x / step - 4;
    }
  }
}
