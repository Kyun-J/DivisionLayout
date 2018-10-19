package app.dvkyun.division_layout_demo

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log

class test : ConstraintLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.i("mmmstart",System.currentTimeMillis().toString())
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i("mmmmeasure",System.currentTimeMillis().toString())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("mmmend",System.currentTimeMillis().toString())
    }
}