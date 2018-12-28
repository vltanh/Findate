package com.example.narubibi.findate.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.example.narubibi.findate.R;

public class MyButton extends android.support.v7.widget.AppCompatButton {
    private Paint mButtonColor;
    Context myContext;

    public MyButton(Context context) {
        super(context);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        myContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setBackgroundDrawable(ContextCompat.getDrawable(myContext, R.drawable.rounded_shape));
        setTextColor(Color.parseColor("#eceff1"));
        super.onDraw(canvas);
    }
}
