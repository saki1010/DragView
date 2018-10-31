

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Author: zb
 * Date: 2017/11/2
 * Description:
 */

public class DragTextView extends android.support.v7.widget.AppCompatTextView {
    //相对于父控件的触摸位置，用于处理拖拽
    private float xDown,yDown,xUp,yUp;
    private int extra;
    private DisplayMetrics dm;
    public DragTextView(Context context) {
        this(context, null, 0);
    }
    public DragTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public DragTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dm = new DisplayMetrics();//获取屏幕宽高
        Activity activity = (Activity) context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    private float downX,downY;
    private int bottomBarY = 0;   //底部导航高度--若无可设为0
    private int WH = 135;    //固定宽高--控件的宽高

    //重写触摸的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isBreak = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getRawX();
                float upY = event.getRawY();
                //判断是拖拽还是点击
                if (getRange(downX,downY,upX,upY) <= 5){
                    this.callOnClick();
                    isBreak = false;
                }else{
                    isBreak = true;
                }
                isOver();
                break;
        }
        return isBreak;
    }
    
    private int parentHeight, parentWidth;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup parent = (ViewGroup) getParent();
        parentHeight = parent.getHeight();
        parentWidth = parent.getWidth();
    }

    private double getRange(float sx,float sy,float ex,float ey){
        float rangX = Math.abs(sx - ex);
        float rangY = Math.abs(sy - ey);
        return Math.sqrt(rangX*rangX + rangY*rangY);
    }
    /** 按下 **/
    private void onTouchDown(MotionEvent event){
        xDown = event.getX();
        yDown = event.getY();
        xUp = event.getRawX();
        yUp = event.getRawY();
        extra = (int) (yUp - yDown - getTop()); //控件父布局相对屏幕顶部的额外高度
    }
    /** 拖拽 **/
    private void onTouchMove(MotionEvent event) {
        int left, right, top, bottom;
        top = (int) (yUp - yDown) - extra;
        left = (int) (xUp - xDown);
        bottom = top + WH;
        right = left + WH;
//        L.i("L="+left+"#T="+top+"#R="+right+"#B="+bottom);
        if (parentHeight > 0 && parentHeight > 0){
            if (top < 0 || left < 0 || bottom > parentHeight || right > parentWidth){
                return;
            }
        }
        this.setFrame(left, top, right, bottom);
        setLayout();
        xUp = event.getRawX();
        yUp = event.getRawY();
    }

    /**
     * 拖拽时判断是否越界
     */
    private void isOver() {
        int width = this.getWidth();
        int height = this.getHeight();
        int left = getLeft(),right=getRight(),top=getTop(),bottom=getBottom();
        if (this.getBottom() < height){ //滑到顶部
            bottom = height;
            top = bottom - getHeight();
        }
        if (this.getRight() <  width){
            right = width;
            left = right - getWidth();
        }
        if (this.getTop() > dm.heightPixels - extra - bottomBarY - WH){
            top = dm.heightPixels - extra - bottomBarY - WH;
            bottom = top + WH;
        }
        if (this.getLeft() > dm.widthPixels - WH){
            left = dm.widthPixels - WH;
            right = left + WH;
        }

        //计算控件相对父布局的位置;超出规定区域返回
        if (this.getBottom() < height || this.getLeft() > dm.widthPixels - WH ||
                this.getRight() <width || this.getTop() > dm.heightPixels - extra - bottomBarY - WH) {
            setFrame(left, top, right, bottom);
        }
        setLayout();
    }

    private void setLayout(){
        RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(WH, WH);
        lpFeedback.leftMargin = this.getLeft();
        lpFeedback.topMargin = this.getTop();
//        lpFeedback.setMargins(this.getLeft(), this.getTop(), 0, 0);
        this.setLayoutParams(lpFeedback);
    }
}
