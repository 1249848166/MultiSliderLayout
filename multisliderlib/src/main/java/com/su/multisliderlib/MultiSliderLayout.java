package com.su.multisliderlib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.List;

public class MultiSliderLayout extends RelativeLayout {

    private List<View> topViews, bottomViews, leftViews, rightViews;//副页
    private View mainView;//主页

    public static class Ratio {
        public static final float WRAP_CONTENT = -1f;
        public static final float MATCH_PARENT = -2f;
        private float left_widthRatio;
        private float right_widthRatio;
        private float top_heightRatio;
        private float bottom_heightRatio;

        public Ratio(float left_widthRatio, float right_widthRatio, float top_heightRatio, float bottom_heightRatio) {
            this.left_widthRatio = left_widthRatio;
            this.right_widthRatio = right_widthRatio;
            this.top_heightRatio = top_heightRatio;
            this.bottom_heightRatio = bottom_heightRatio;
        }

        public float getLeftWidthRatio() {
            return left_widthRatio;
        }

        public float getRightWidthRatio() {
            return right_widthRatio;
        }

        public float getTopHeightRatio() {
            return top_heightRatio;
        }

        public float getBottomHeightRatio() {
            return bottom_heightRatio;
        }
    }

    private Ratio ratio;//副页占主页宽高的占比
    private int x_offset = 0, y_offset = 0;//横向和纵向偏移
    private int screenWidth = 0, screenHeight = 0;//屏幕宽高
    private int left_spanWidth = 0, top_spanHeight = 0;//副页宽高
    private int right_spanWidth = 0, bottom_spanHeight = 0;//副页宽高
    private int wMeasureSpec;//主页宽度测量参数
    private int hMeasureSpec;//主页高度测量参数
    private int left_wrMeasureSpec;//副页宽度测量参数
    private int right_wrMeasureSpec;//副页宽度测量参数
    private int top_hrMeasureSpec;//副页高度测量参数
    private int bottom_hrMeasureSpec;//副页高度测量参数
    private Scroller scroller;//滚动对象

    public enum ScrollState {//状态枚举
        Scrolling, UnScrolling
    }

    private ScrollState scrollState = ScrollState.UnScrolling;//状态
    private int scrollMillisDuration = 500;//回滚动时间间隔
    private int leftDepth = 0;//左边菜单层数
    private int rightDepth = 0;//右边菜单层数
    private int topDepth = 0;//顶部菜单层数
    private int bottomDepth = 0;//底部菜单层数
    private int leftIndex = 0;//左侧菜单当前位置
    private int rightIndex = 0;//右侧菜单当前位置
    private int topIndex = 0;//顶部菜单当前位置
    private int bottomIndex = 0;//底部菜单当前位置

    public enum MenuType {
        Left, Right, Top, Bottom
    }

    public interface MultiSliderListener {
        void onStateChange(ScrollState state);

        void scrollProgress(float progress);

        void onMenuSelected(MenuType type, int index);
    }

    private MultiSliderListener multiSliderListener;

    public void setMultiSliderListener(MultiSliderListener multiSliderListener) {
        this.multiSliderListener = multiSliderListener;
    }

    public MultiSliderLayout(Context context) {
        this(context, null);
    }

    public MultiSliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiSliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        scroller = new Scroller(getContext());
        setScrollX(0);
        setScrollY(0);
    }

    public void setScrollMillisDuration(int scrollMillisDuration) {
        this.scrollMillisDuration = scrollMillisDuration;
    }

    public void setRatio(Ratio ratio) {
        this.ratio = ratio;
        if (ratio.getLeftWidthRatio() != Ratio.MATCH_PARENT && ratio.getLeftWidthRatio() != Ratio.WRAP_CONTENT) {
            left_spanWidth = (int) (screenWidth * ratio.getLeftWidthRatio());
        }
        if (ratio.getRightWidthRatio() != Ratio.MATCH_PARENT && ratio.getRightWidthRatio() != Ratio.WRAP_CONTENT) {
            right_spanWidth = (int) (screenWidth * ratio.getRightWidthRatio());
        }
        if (ratio.getTopHeightRatio() != Ratio.MATCH_PARENT && ratio.getTopHeightRatio() != Ratio.WRAP_CONTENT) {
            top_spanHeight = (int) (screenHeight * ratio.getTopHeightRatio());
        }
        if (ratio.getBottomHeightRatio() != Ratio.MATCH_PARENT && ratio.getBottomHeightRatio() != Ratio.WRAP_CONTENT) {
            bottom_spanHeight = (int) (screenHeight * ratio.getBottomHeightRatio());
        }
    }

    public void setFragments(final List<View> topViews, final List<View> bottomViews,
                             final List<View> leftViews, final List<View> rightViews,
                             final View mainView) {
        setTopViews(topViews);
        setBottomViews(bottomViews);
        setLeftViews(leftViews);
        setRightViews(rightViews);
        setMainView(mainView);
        requestLayout();
    }

    private void setMainView(final View mainView) {
        this.mainView = mainView;
        if (mainView != null) {
            this.addView(mainView);
        }
    }

    private void setTopViews(List<View> topViews) {
        this.topViews = topViews;
        if (topViews != null && topViews.size() > 0) {
            topDepth = topViews.size();
            for (final View view : topViews) {
                if (view != null) {
                    this.addView(view);
                }
            }
        }
    }

    private void setBottomViews(List<View> bottomViews) {
        this.bottomViews = bottomViews;
        if (bottomViews != null && bottomViews.size() > 0) {
            bottomDepth = bottomViews.size();
            for (final View view : bottomViews) {
                if (view != null) {
                    this.addView(view);
                }
            }
        }
    }

    private void setLeftViews(List<View> leftViews) {
        this.leftViews = leftViews;
        if (leftViews != null && leftViews.size() > 0) {
            leftDepth = leftViews.size();
            for (final View view : leftViews) {
                if (view != null) {
                    this.addView(view);
                }
            }
        }
    }

    private void setRightViews(List<View> rightViews) {
        this.rightViews = rightViews;
        if (rightViews != null && rightViews.size() > 0) {
            rightDepth = rightViews.size();
            for (final View view : rightViews) {
                if (view != null) {
                    this.addView(view);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = screenWidth;
        int h = screenHeight;
        int left_wr = 0, right_wr = 0;
        int top_hr = 0, bottom_hr = 0;
        wMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        hMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        if (ratio.getLeftWidthRatio() == Ratio.MATCH_PARENT) {
            left_wr = w;
            left_wrMeasureSpec = MeasureSpec.makeMeasureSpec(left_wr, MeasureSpec.EXACTLY);
        } else if (ratio.getLeftWidthRatio() == Ratio.WRAP_CONTENT) {
            for (int i = 0; i < leftDepth; i++) {
                View v = leftViews.get(i);
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    int c = vg.getChildCount(), j = 0;
                    for (j = 0; j < c; j++) {
                        if (vg.getChildAt(j).getMeasuredWidth() > left_wr) {
                            left_wr = vg.getChildAt(j).getMeasuredWidth();
                        }
                    }
                } else {
                    if (v.getMeasuredWidth() > left_wr) {
                        left_wr = v.getMeasuredWidth();
                    }
                }
            }
        } else {
            left_wr = (int) (w * ratio.getLeftWidthRatio());
            left_wrMeasureSpec = MeasureSpec.makeMeasureSpec(left_wr, MeasureSpec.EXACTLY);
        }

        if (ratio.getRightWidthRatio() == Ratio.MATCH_PARENT) {
            right_wr = w;
            right_wrMeasureSpec = MeasureSpec.makeMeasureSpec(right_wr, MeasureSpec.EXACTLY);
        } else if (ratio.getRightWidthRatio() == Ratio.WRAP_CONTENT) {
            for (int i = 0; i < rightDepth; i++) {
                View v = rightViews.get(i);
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    int c = vg.getChildCount(), j = 0;
                    for (j = 0; j < c; j++) {
                        if (vg.getChildAt(j).getMeasuredWidth() > right_wr) {
                            right_wr = vg.getChildAt(j).getMeasuredWidth();
                        }
                    }
                } else {
                    if (v.getMeasuredWidth() > right_wr) {
                        right_wr = v.getMeasuredWidth();
                    }
                }
            }
        } else {
            right_wr = (int) (w * ratio.getRightWidthRatio());
            right_wrMeasureSpec = MeasureSpec.makeMeasureSpec(right_wr, MeasureSpec.EXACTLY);
        }

        if (ratio.getTopHeightRatio() == Ratio.MATCH_PARENT) {
            top_hr = h;
            top_hrMeasureSpec = MeasureSpec.makeMeasureSpec(top_hr, MeasureSpec.EXACTLY);
        } else if (ratio.getTopHeightRatio() == Ratio.WRAP_CONTENT) {
            for (int i = 0; i < topDepth; i++) {
                View v = topViews.get(i);
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    int c = vg.getChildCount(), j = 0;
                    for (j = 0; j < c; j++) {
                        if (vg.getChildAt(j).getMeasuredHeight() > top_hr) {
                            top_hr = vg.getChildAt(j).getMeasuredHeight();
                        }
                    }
                } else {
                    if (v.getMeasuredHeight() > top_hr) {
                        top_hr = v.getMeasuredHeight();
                    }
                }
            }
        } else {
            top_hr = (int) (h * ratio.getTopHeightRatio());
            top_hrMeasureSpec = MeasureSpec.makeMeasureSpec(top_hr, MeasureSpec.EXACTLY);
        }

        if (ratio.getBottomHeightRatio() == Ratio.MATCH_PARENT) {
            bottom_hr = h;
            bottom_hrMeasureSpec = MeasureSpec.makeMeasureSpec(bottom_hr, MeasureSpec.EXACTLY);
        } else if (ratio.getBottomHeightRatio() == Ratio.WRAP_CONTENT) {
            for (int i = 0; i < bottomDepth; i++) {
                View v = bottomViews.get(i);
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    int c = vg.getChildCount(), j = 0;
                    for (j = 0; j < c; j++) {
                        if (vg.getChildAt(j).getMeasuredHeight() > bottom_hr) {
                            bottom_hr = vg.getChildAt(j).getMeasuredHeight();
                        }
                    }
                } else {
                    if (v.getMeasuredHeight() > bottom_hr) {
                        bottom_hr = v.getMeasuredHeight();
                    }
                }
            }
        } else {
            bottom_hr = (int) (h * ratio.getBottomHeightRatio());
            bottom_hrMeasureSpec = MeasureSpec.makeMeasureSpec(bottom_hr, MeasureSpec.EXACTLY);
        }

        if (topViews != null && topViews.size() > 0) {
            for (View view : topViews) {
                if (view != null) {
                    view.measure(wMeasureSpec, top_hrMeasureSpec);
                }
            }
        }
        if (bottomViews != null && bottomViews.size() > 0) {
            for (View view : bottomViews) {
                if (view != null) {
                    view.measure(wMeasureSpec, bottom_hrMeasureSpec);
                }
            }
        }
        if (leftViews != null && leftViews.size() > 0) {
            for (View view : leftViews) {
                if (view != null) {
                    view.measure(left_wrMeasureSpec, hMeasureSpec);
                }
            }
        }
        if (rightViews != null && rightViews.size() > 0) {
            for (View view : rightViews) {
                if (view != null) {
                    view.measure(right_wrMeasureSpec, hMeasureSpec);
                }
            }
        }
        if (mainView != null)
            mainView.measure(wMeasureSpec, hMeasureSpec);

        left_spanWidth = left_wr;
        right_spanWidth = right_wr;
        top_spanHeight = top_hr;
        bottom_spanHeight = bottom_hr;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (topViews != null && topViews.size() > 0) {
            for (View view : topViews) {
                if (view != null) {
                    int i = topViews.indexOf(view);
                    if (view instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) view;
                        int j = 0, count = vg.getChildCount();
                        spanHeight = 0;
                        for (j = 0; j < count; j++) {
                            int itemHeight = vg.getChildAt(j).getMeasuredHeight();
                            vg.getChildAt(j).layout(0, j * itemHeight, screenWidth, (j + 1) * itemHeight);
                            if (spanHeight < itemHeight)
                                spanHeight = itemHeight;
                        }
                    }
                    view.layout(l, t - spanHeight * (i + 1), r, t - spanHeight * i);
                }
            }
        }
        if (bottomViews != null && bottomViews.size() > 0) {
            for (View view : bottomViews) {
                if (view != null) {
                    int i = bottomViews.indexOf(view);
                    view.layout(l, b + spanHeight * i, r, b + spanHeight * (i + 1));
                    if (view instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) view;
                        int j = 0, count = vg.getChildCount();
                        for (j = 0; j < count; j++) {
                            int itemHeight = vg.getChildAt(j).getMeasuredHeight();
                            vg.getChildAt(j).layout(0, j * itemHeight, screenWidth, (j + 1) * itemHeight);
                        }
                    }
                }
            }
        }
        if (leftViews != null && leftViews.size() > 0) {
            for (View view : leftViews) {
                if (view != null) {
                    int i = leftViews.indexOf(view);
                    view.layout(l - spanWidth * (i + 1), t, l - spanWidth * i, b);
                    if (view instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) view;
                        int j = 0, count = vg.getChildCount();
                        for (j = 0; j < count; j++) {
                            int itemHeight = vg.getChildAt(j).getMeasuredHeight();
                            vg.getChildAt(j).layout(0, j * itemHeight, spanWidth, (j + 1) * itemHeight);
                        }
                    }
                }
            }
        }
        if (rightViews != null && rightViews.size() > 0) {
            for (View view : rightViews) {
                if (view != null) {
                    int i = rightViews.indexOf(view);
                    view.layout(r + spanWidth * i, t, r + spanWidth * (i + 1), b);
                    if (view instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) view;
                        int j = 0, count = vg.getChildCount();
                        for (j = 0; j < count; j++) {
                            int itemHeight = vg.getChildAt(j).getMeasuredHeight();
                            vg.getChildAt(j).layout(0, j * itemHeight, spanWidth, (j + 1) * itemHeight);
                        }
                    }
                }
            }
        }
        if (mainView != null)
            mainView.layout(l, t, r, b);
    }

    private int touchX = 0, touchY = 0;
    private int lastTouchX = 0, lastTouchY = 0;
    private int dx = 0, dy = 0;
    private boolean isLeftMenuShow;
    private boolean isRightMenuShow;
    private boolean isTopMenuShow;
    private boolean isBottomMenuShow;
    private int spanWidth, spanHeight;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = (int) event.getX();
                lastTouchY = (int) event.getY();
                touchX = (int) event.getX();
                touchY = (int) event.getY();
                if (isLeftMenuShow) {
                    if (touchX < left_spanWidth) {
                        return leftViews.get(leftIndex - 1).dispatchTouchEvent(event);
                    }
                } else if (isRightMenuShow) {
                    if (touchX > screenWidth - right_spanWidth) {
                        //return rightViews.get(rightIndex - 1).dispatchTouchEvent(event);
                    }
                } else if (isTopMenuShow) {
                    if (touchY < top_spanHeight) {
                        return topViews.get(topIndex - 1).dispatchTouchEvent(event);
                    }
                } else if (isBottomMenuShow) {
                    if (touchY > screenHeight - bottom_spanHeight) {
                        return bottomViews.get(bottomIndex - 1).dispatchTouchEvent(event);
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                touchX = (int) event.getX();
                touchY = (int) event.getY();
                dx = touchX - lastTouchX;
                dy = touchY - lastTouchY;
                lastTouchX = touchX;
                lastTouchY = touchY;
                if (scrollState == ScrollState.UnScrolling) {
                    if (Math.abs(dx) > Math.abs(dy)) {//横向滑动
                        if (isTopMenuShow) {
                            return topViews.get(topIndex - 1).dispatchTouchEvent(event);
                        } else if (isBottomMenuShow) {
                            return bottomViews.get(bottomIndex - 1).dispatchTouchEvent(event);
                        }
                        x_offset = x_offset + dx;
                        setScrollX(-x_offset);
                        if (x_offset > 0) {
                            spanWidth = left_spanWidth;
                        } else {
                            spanWidth = right_spanWidth;
                        }
                        if (multiSliderListener != null)
                            multiSliderListener.scrollProgress((float) (x_offset % spanWidth) / spanWidth);//滚动进度
                        if (-x_offset > spanWidth * rightDepth) {
                            setScrollX(spanWidth * rightDepth);
                            x_offset = -spanWidth * rightDepth;
                        }
                        if (-x_offset < -spanWidth * leftDepth) {
                            setScrollX(-spanWidth * leftDepth);
                            x_offset = spanWidth * leftDepth;
                        }
                        if (scrollState != ScrollState.Scrolling) {
                            scrollState = ScrollState.Scrolling;
                            if (multiSliderListener != null)
                                multiSliderListener.onStateChange(ScrollState.Scrolling);//状态改变
                        }
                    } else {//纵向滑动
                        if (isLeftMenuShow) {
                            if (touchX > left_spanWidth) {
                                return false;
                            }
                            return leftViews.get(leftIndex - 1).dispatchTouchEvent(event);
                        } else if (isRightMenuShow) {
                            if (touchX < screenWidth - right_spanWidth) {
                                return false;
                            }
                            return rightViews.get(rightIndex - 1).dispatchTouchEvent(event);
                        }
                        y_offset = y_offset + dy;
                        setScrollY(-y_offset);
                        if (y_offset > 0) {
                            spanHeight = top_spanHeight;
                        } else {
                            spanHeight = bottom_spanHeight;
                        }
                        if (multiSliderListener != null)
                            multiSliderListener.scrollProgress((float) (y_offset % spanHeight) / spanHeight);//滚动进度
                        if (-y_offset > spanHeight * topDepth) {
                            setScrollY(spanHeight * topDepth);
                            y_offset = -spanHeight * topDepth;
                        }
                        if (-y_offset < -spanHeight * bottomDepth) {
                            setScrollY(-spanHeight * bottomDepth);
                            y_offset = spanHeight * bottomDepth;
                        }
                        if (scrollState != ScrollState.Scrolling) {
                            scrollState = ScrollState.Scrolling;
                            if (multiSliderListener != null)
                                multiSliderListener.onStateChange(ScrollState.Scrolling);//状态改变
                        }
                    }
                }
                requestLayout();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //处理横向回滚
                //右边
                if (-x_offset > spanWidth * (rightIndex - 0.5) && -x_offset < spanWidth * (rightIndex)) {
                    scroller.startScroll(getScrollX(), 0, spanWidth * (rightIndex - 1) + spanWidth - getScrollX(), 0, scrollMillisDuration);
                }
                if (-x_offset > spanWidth * (rightIndex) && -x_offset <= spanWidth * (rightIndex + 0.5)) {
                    scroller.startScroll(getScrollX(), 0, spanWidth * (rightIndex) - getScrollX(), 0, scrollMillisDuration);
                }

                //左边
                System.out.println();
                if (-x_offset < -spanWidth * (leftIndex - 0.5) && -x_offset > -spanWidth * (leftIndex)) {
                    scroller.startScroll(getScrollX(), 0, -spanWidth * (leftIndex - 1) - spanWidth - getScrollX(), 0, scrollMillisDuration);
                }
                if (-x_offset < -spanWidth * (leftIndex) && -x_offset >= -spanWidth * (leftIndex + 0.5)) {
                    scroller.startScroll(getScrollX(), 0, -spanWidth * (leftIndex) - getScrollX(), 0, scrollMillisDuration);
                }

                //处理纵向滚动
                //底部
                if (-y_offset > spanHeight * (bottomIndex - 0.5) && -y_offset < spanHeight * (bottomIndex)) {
                    scroller.startScroll(0, getScrollY(), 0, spanHeight * (bottomIndex - 1) + spanHeight - getScrollY(), scrollMillisDuration);
                }
                if (-y_offset > spanHeight * (bottomIndex) && -y_offset <= spanHeight * (bottomIndex + 0.5)) {
                    scroller.startScroll(0, getScrollY(), 0, spanHeight * (bottomIndex) - getScrollY(), scrollMillisDuration);
                }
                //顶部
                if (-y_offset < -spanHeight * (topIndex - 0.5) && -y_offset > -spanHeight * (topIndex)) {
                    scroller.startScroll(0, getScrollY(), 0, -spanHeight * (topIndex - 1) - spanHeight - getScrollY(), scrollMillisDuration);
                }
                if (-y_offset < -spanHeight * (topIndex) && -y_offset >= -spanHeight * (topIndex + 0.5)) {
                    scroller.startScroll(0, getScrollY(), 0, -spanHeight * (topIndex) - getScrollY(), scrollMillisDuration);
                }

                requestLayout();
                lastTouchY = 0;
                lastTouchX = 0;
                touchX = 0;
                touchY = 0;
                dx = 0;
                dy = 0;
                return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!scroller.computeScrollOffset()) {
            scrollState = ScrollState.UnScrolling;
            if (multiSliderListener != null)
                multiSliderListener.onStateChange(ScrollState.UnScrolling);//状态改变
            checkIndex();
            return;
        }
        scrollState = ScrollState.Scrolling;
        int cx = scroller.getCurrX();
        int cy = scroller.getCurrY();
        x_offset = -cx;
        y_offset = -cy;
        scrollTo(cx, cy);
        requestLayout();
    }

    private void checkIndex() {
        //设置当前下标
        //左边菜单
        for (int i = 0; i < leftDepth; i++) {
            if (-x_offset < -spanWidth * (i + 0.5) && -x_offset >= -spanWidth * (i + 1)) {
                int lastInd = leftIndex;
                leftIndex = i + 1;
                if (multiSliderListener != null && leftIndex != 0 && lastInd != leftIndex)
                    multiSliderListener.onMenuSelected(MenuType.Left, leftIndex);//菜单选择
            } else if (-x_offset <= -spanWidth * (i) && -x_offset > -spanWidth * (i + 0.5)) {
                int lastInd = leftIndex;
                leftIndex = i;
                if (multiSliderListener != null && leftIndex != 0 && lastInd != leftIndex)
                    multiSliderListener.onMenuSelected(MenuType.Left, leftIndex);//菜单选择
            }
        }
        //右边菜单
        for (int i = 0; i < rightDepth; i++) {
            if (-x_offset > spanWidth * (i + 0.5) && -x_offset <= spanWidth * (i + 1)) {
                int lastInd = rightIndex;
                rightIndex = i + 1;
                if (multiSliderListener != null && rightIndex != 0 && lastInd != rightIndex)
                    multiSliderListener.onMenuSelected(MenuType.Right, rightIndex);//菜单选择
            } else if (-x_offset >= spanWidth * (i) && -x_offset < spanWidth * (i + 0.5)) {
                int lastInd = rightIndex;
                rightIndex = i;
                if (multiSliderListener != null && rightIndex != 0 && lastInd != rightIndex)
                    multiSliderListener.onMenuSelected(MenuType.Right, rightIndex);//菜单选择
            }
        }
        //顶部菜单
        for (int i = 0; i < topDepth; i++) {
            if (-y_offset < -spanHeight * (i + 0.5) && -y_offset >= -spanHeight * (i + 1)) {
                int lastInd = topIndex;
                topIndex = i + 1;
                if (multiSliderListener != null && topIndex != 0 && lastInd != topIndex)
                    multiSliderListener.onMenuSelected(MenuType.Top, topIndex);//菜单选择
            } else if (-y_offset <= -spanHeight * (i) && -y_offset > -spanHeight * (i + 0.5)) {
                int lastInd = topIndex;
                topIndex = i;
                if (multiSliderListener != null && topIndex != 0 && lastInd != topIndex)
                    multiSliderListener.onMenuSelected(MenuType.Top, topIndex);//菜单选择
            }
        }
        //底部菜单
        for (int i = 0; i < bottomDepth; i++) {
            if (-y_offset > spanHeight * (i + 0.5) && -y_offset <= spanHeight * (i + 1)) {
                int lastInd = bottomIndex;
                bottomIndex = i + 1;
                if (multiSliderListener != null && bottomIndex != 0 && lastInd != bottomIndex)
                    multiSliderListener.onMenuSelected(MenuType.Bottom, bottomIndex);//菜单选择
            } else if (-y_offset >= spanHeight * (i) && -y_offset < spanHeight * (i + 0.5)) {
                int lastInd = bottomIndex;
                bottomIndex = i;
                if (multiSliderListener != null && bottomIndex != 0 && lastInd != bottomIndex)
                    multiSliderListener.onMenuSelected(MenuType.Bottom, bottomIndex);//菜单选择
            }
        }
        if (leftIndex > 0)
            isLeftMenuShow = true;
        else {
            isLeftMenuShow = false;
        }
        if (rightIndex > 0)
            isRightMenuShow = true;
        else {
            isRightMenuShow = false;
        }
        if (topIndex > 0)
            isTopMenuShow = true;
        else {
            isTopMenuShow = false;
        }
        if (bottomIndex > 0)
            isBottomMenuShow = true;
        else {
            isBottomMenuShow = false;
        }
        Log.e("aaa", leftIndex + "," + rightIndex + "," + topIndex + "," + bottomIndex);
        Log.e("bbb", isLeftMenuShow + "," + isRightMenuShow + "," + isTopMenuShow + "," + isBottomMenuShow);
    }
}
