package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.RemoteViews


@RemoteViews.RemoteView
class DivisionLayout : ViewGroup {

    private val groupList : HashMap<String,SparseArray<ArrayList<Any>>> = HashMap()

    private var parentWidth = 0
    private var parentHeight = 0
    private val displayMetrics by lazy { context.resources.displayMetrics }

    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        for(i in 0 until childCount) {
            val lp = getChildAt(i).layoutParams as DivisionLayout.LayoutParams
            if(!groupList.containsKey(lp.group)) {
                val d = SparseArray<ArrayList<Any>>()
                d.setValueAt(LayoutParams.VIRTICAL,ArrayList())
                d.setValueAt(LayoutParams.HORIZONTAL, ArrayList())
                d[LayoutParams.VIRTICAL].add(LayoutParams.DEFAULT_VALUE)
                d[LayoutParams.HORIZONTAL].add(LayoutParams.DEFAULT_VALUE)
                groupList[LayoutParams.DEFAULT_GROUP] = d
            }
            val vl = groupList[lp.group]!![LayoutParams.VIRTICAL]
            val hl = groupList[lp.group]!![LayoutParams.HORIZONTAL]
            val vc = mChild(i)
            val hc = mChild(i)
            vc.dh = lp.dHeight
            vc.dt = lp.dTop
            vc.db = lp.dBottom
            hc.dw = lp.dWidth
            hc.dl = lp.dLeft
            hc.dr = lp.dRight
            vl.add(lp.vOrder,vc)
            hl.add(lp.hOrder,hc)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT || heightMeasureSpec == ViewGroup.LayoutParams.WRAP_CONTENT)
            throw(DivisionLayoutExecption("Do Not use wrap_contents on DivisionLayout"))
        parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        groupList.forEach { k, m ->
            val mv = 0.toFloat()
            val lv = 0.toFloat()
            m.get(LayoutParams.VIRTICAL).forEach {

            }
            val mh = 0.toFloat()
            val lh = 0.toFloat()
            m.get(LayoutParams.HORIZONTAL).forEach {

            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return true // p is LayoutParams
    }

    private fun f(o : Boolean, m : Float, v : Float) : Int {
        if(o)
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (parentWidth/(m)*(1/v)),displayMetrics).toInt()
        else
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (parentHeight/(m)*(1/v)),displayMetrics).toInt()
    }

    class LayoutParams : ViewGroup.LayoutParams {

        companion object {
            val DEFAULT_GROUP = "-1"
            val DEFAULT_ORDER = 99
            val DEFAULT_VALUE = 0.toFloat()
            val VIRTICAL = 0
            val HORIZONTAL = 1
        }

        var group = DEFAULT_GROUP
        var dWidth = DEFAULT_VALUE
        var dHeight = DEFAULT_VALUE
        var dTop = DEFAULT_VALUE
        var dBottom = DEFAULT_VALUE
        var dLeft = DEFAULT_VALUE
        var dRight = DEFAULT_VALUE
        var vOrder = DEFAULT_ORDER
        var hOrder = DEFAULT_ORDER

        constructor(context: Context?,attrs: AttributeSet?) : super(context,attrs) {
            context?.let { c -> attrs?.let { a -> setAttrs(c,a) } }
        }
        constructor(width : Int, height : Int) : super(width,height)
        constructor(params: ViewGroup.LayoutParams) : super(params)

        private fun setAttrs(context: Context, attrs: AttributeSet) {
            val TypedArray = context.theme.obtainStyledAttributes(attrs,R.styleable.DivisionLayoutLP,0,0)

            TypedArray.getString(R.styleable.DivisionLayoutLP_division_group)?.let { group = it }
            dWidth = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_width, DEFAULT_VALUE)
            dHeight = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_height,DEFAULT_VALUE)
            dTop = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_top,DEFAULT_VALUE)
            dBottom = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_bottom,DEFAULT_VALUE)
            dLeft = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_left,DEFAULT_VALUE)
            dRight = TypedArray.getFloat(R.styleable.DivisionLayoutLP_division_right,DEFAULT_VALUE)
            vOrder = TypedArray.getInt(R.styleable.DivisionLayoutLP_division_virtical_order,DEFAULT_ORDER)
            hOrder = TypedArray.getInt(R.styleable.DivisionLayoutLP_division_horizontal_order, DEFAULT_ORDER)
            if(vOrder > 99 || vOrder < 1 || hOrder > 99 || hOrder < 1)
                throw(DivisionLayoutExecption("order must be greater than 0 and less than 100."))
            if(dWidth < 0 || dHeight < 0 || dTop < 0 || dBottom < 0 || dLeft < 0 || dRight < 0)
                throw(DivisionLayoutExecption("order must be greater than 0 and less than 100."))

            TypedArray.recycle()
        }
    }

    private inner class mChild(val cid : Int) {
        var dw = LayoutParams.DEFAULT_VALUE
        var dh = LayoutParams.DEFAULT_VALUE
        var dt = LayoutParams.DEFAULT_VALUE
        var db = LayoutParams.DEFAULT_VALUE
        var dl = LayoutParams.DEFAULT_VALUE
        var dr = LayoutParams.DEFAULT_VALUE
    }
}