package app.dvkyun.divisionlayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup


class Division_Layout : ViewGroup {


    private lateinit var group_list : Array<String>


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    class LayoutParams : MarginLayoutParams {

        companion object {
            val VIRTICAL = 0
            val HORIZONTAL = 1
            val DEFAULT_GROUP = "-1"
        }

        var orientation = VIRTICAL
        var group = DEFAULT_GROUP
        var value : Float = 0.toFloat()

        constructor(context: Context?,attrs: AttributeSet?) : super(context,attrs) {
            context?.let { c -> attrs?.let { a -> setAttrs(c,a) } }
        }
        constructor(width : Int, height : Int) : super(width,height)

        private fun setAttrs(context: Context, attrs: AttributeSet) {
            val TypedArray = context.obtainStyledAttributes(attrs,R.styleable.Division_Layout)

            orientation = TypedArray.getInt(R.styleable.Division_Layout_division_orientation,0)
            TypedArray.getString(R.styleable.Division_Layout_division_group)?.let { group = it }
            value = TypedArray.getFloat(R.styleable.Division_Layout_division_value,0.toFloat())

            TypedArray.recycle()
        }



    }
}