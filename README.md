# 文字滚动View
**[GitHub](http://)**
### 效果([无法显示效果点击这里](https://gitee.com/BDWen/TextScrollDome))
![项目中应用](https://images.gitee.com/uploads/images/2020/0629/122327_ae87d793_1032805.gif "ezgif.com-resize.gif")![demo](https://images.gitee.com/uploads/images/2020/0629/122414_c00033e9_1032805.gif "ezgif.com-resize (1).gif")

 **原理其实就是把文字逐行绘制出来，利用属性动画改变绘制的位置来实现滚动**

**代码写的有点粗糙，分享出来希望能帮到大家**

# 使用
 **直接复制下面代码或者[下载Demo](https://github.com/Levine1992/TextScrollDome.git)把文件复制到自己项目也可以**

```
package com.example.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TextScrollView extends View {
    private ArrayList<String> strings = new ArrayList<>();
    private ArrayList<StaticLayout> mStaticLayouts = new ArrayList<>();
    private TextPaint mTextPaint;
    private ValueAnimator mValueAnimator;
    private int mOffset;
    private int mOffsetScroll;
    private int mScrollPosition = 0;
    private int mPosition = 0;
    private int mOffsetY = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mCheckSizeSum = 0;
    private float spacingmult = 1;
    private float spacingadd = 0;
    private int mScrollDuration = 500;
    private int mScrollInterval = 1500;
    private boolean isDownScroll = false;

    private Runnable mNextScrollRun = new Runnable() {
        @Override
        public void run() {
            mOffsetScroll = mOffsetScroll + mOffset;
            mScrollPosition++;
            startScroll();
        }
    };
    private Runnable mCheckSizeRun = () -> setData(strings);

    public TextScrollView(Context context) {
        super(context);
        init();
    }

    public TextScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        //默认文字大小颜色
        mTextPaint = new TextPaint();
        //文字颜色
        mTextPaint.setColor(Color.parseColor("#CCFFFFFF"));
        mTextPaint.setAntiAlias(true);
        //文字大小dp
        mTextPaint.setTextSize(dp2px(13));
    }

    public int dp2px(final float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTextSizeDp(int size) {
        mTextPaint.setTextSize(dp2px(size));
    }

    /**
     * 设置文字颜色
     *
     * @param color 例如 #CCFFFFFF
     */
    public void setTextColor(String color) {
        mTextPaint.setColor(Color.parseColor(color));
    }

    /**
     * 设置数据
     */
    public void setData(ArrayList<String> strings) {
        mOffset = 0;
        mOffsetScroll = 0;
        mScrollPosition = 0;
        mPosition = 0;
        mOffsetY = 0;
        removeCallbacks(mNextScrollRun);
        removeCallbacks(mCheckSizeRun);
        mStaticLayouts.clear();
        this.strings = strings;
        if (mWidth == 0) {
            if (mCheckSizeSum > 3) return;
            postDelayed(mCheckSizeRun, 50);
            mCheckSizeSum++;
            return;
        }
        if (mValueAnimator != null) mValueAnimator.cancel();
        clearAnimation();
        for (String string : this.strings) {
            mStaticLayouts.add(createStaticLayout(string));
        }
        start();
    }

    public void setScrollDirection(boolean isDown) {
        isDownScroll = isDown;
        start();
    }

    private StaticLayout createStaticLayout(String text) {
        return new StaticLayout(text, mTextPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, spacingmult, spacingadd, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStaticLayouts.size() == 0) return;
        canvas.save();
        int textY = mOffsetScroll;
        for (int i = 0; i < strings.size(); i++) {
            StaticLayout staticLayout = mStaticLayouts.get(i);
            if (i == 0) {
                canvas.translate(0, textY + mOffset);
            } else {
                canvas.translate(0, textY);
            }
            staticLayout.draw(canvas);
            textY = staticLayout.getHeight();
        }
        canvas.restore();
    }

    private void start(){
        if (mStaticLayouts.size() != 0) offsetY(mHeight);
    }

    private void offsetY(int viewHeight) {
        if (mValueAnimator != null && mValueAnimator.isStarted()) return;
        int height = 0;
        for (StaticLayout staticLayout : mStaticLayouts) {
            height = height + staticLayout.getHeight();
            if (height > viewHeight) {
                mOffsetY = height - viewHeight;
                mPosition = mStaticLayouts.indexOf(staticLayout);
                break;
            }
        }
        //如果能全部显示就不滚动
        if (height <= viewHeight) {
            invalidate();
            return;
        }
        mValueAnimator = ValueAnimator.ofInt(-mOffsetY);
        mValueAnimator.setDuration(mScrollDuration);
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.removeAllListeners();
        mValueAnimator.addUpdateListener(animation -> {
            Object value = animation.getAnimatedValue();
            mOffset = (int) value;
            invalidate();
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(mNextScrollRun, mScrollInterval);
            }
        });
        mValueAnimator.start();
    }


    private void startScroll() {
        if (mValueAnimator != null && mValueAnimator.isStarted()) return;
        if (mScrollPosition > mStaticLayouts.size() - 1) {
            mScrollPosition--;
        }
        if (mScrollPosition > 0) {
            String txt = strings.get(0);
            strings.add(txt);
            mStaticLayouts.add(createStaticLayout(txt));
            mOffsetScroll = mStaticLayouts.get(0).getHeight() + mOffsetScroll;
            strings.remove(0);
            mStaticLayouts.remove(0);
        }
        if (mPosition > mStaticLayouts.size() - 1) mPosition = mStaticLayouts.size() - 1;
        int scrollHeight = mStaticLayouts.get(mPosition).getHeight();
        mValueAnimator = ValueAnimator.ofInt(isDownScroll ? scrollHeight : -scrollHeight);
        mValueAnimator.setDuration(mScrollDuration);
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.removeAllListeners();
        mValueAnimator.addUpdateListener(animation -> {
            Object value = animation.getAnimatedValue();
            mOffset = (int) value;
            invalidate();
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(mNextScrollRun, mScrollInterval);
            }
        });
        mValueAnimator.start();
    }
}

```
