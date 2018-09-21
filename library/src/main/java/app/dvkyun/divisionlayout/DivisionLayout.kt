package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.RemoteViews


@RemoteViews.RemoteView
class DivisionLayout : ViewGroup {

    private lateinit var groupList : HashMap<String,SparseArray<ArrayList<Any>>>

    private var parentWidth = 0
    private var parentHeight = 0

    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        groupList = HashMap()

        for(i in 0 until childCount) {
            val lp = getChildAt(i).layoutParams as DivisionLayout.LayoutParams
            if(lp.vOrder > childCount || lp.hOrder >= childCount)
                throw(DivisionLayoutExecption("order can not be greater than the number of views."))
            if(lp.dWidth < 0 || lp.dHeight < 0 || lp.dTop < 0 || lp.dBottom < 0 || lp.dLeft < 0 || lp.dRight < 0)
                throw(DivisionLayoutExecption("value must be greater than -1."))
            if(lp.vOrder < LayoutParams.DEFAULT_ORDER || lp.hOrder < LayoutParams.DEFAULT_ORDER)
                throw(DivisionLayoutExecption("order must be greater than "+ LayoutParams.DEFAULT_ORDER.toString()+"."))
            if(!groupList.containsKey(lp.group)) {
                val d = SparseArray<ArrayList<Any>>()
                d.put(0,ArrayList())
                d.put(1,ArrayList())
                d.put(2,ArrayList())
                d.put(3,ArrayList())
                d[0].add(0.toFloat())
                d[1].add(0.toFloat())
                groupList[lp.group] = d
            }
            val vl = groupList[lp.group]!![0]
            val hl = groupList[lp.group]!![1]
            val vol = groupList[lp.group]!![2]
            val hol = groupList[lp.group]!![3]
            val vc = mChild(i)
            val hc = mChild(i)
            vc.dh = lp.dHeight
            vc.dt = lp.dTop
            vc.db = lp.dBottom
            vc.vo = lp.vOrder
            hc.dw = lp.dWidth
            hc.dl = lp.dLeft
            hc.dr = lp.dRight
            hc.ho = lp.hOrder
            vl[0] = lp.dTop + lp.dHeight + lp.dBottom + vl[0] as Float
            hl[0] = lp.dRight + lp.dWidth + lp.dLeft + hl[0] as Float
            if(vc.vo == LayoutParams.DEFAULT_ORDER) vl.add(vc)
            else vol.add(vc)
            if(hc.ho == LayoutParams.DEFAULT_ORDER) hl.add(hc)
            else hol.add(hc)

        }
        groupList.forEach { _ , m ->
            m[2].sortedWith(compareBy { (it as mChild).vo }).apply {
                this.forEach { m[0].add((it as mChild).vo,it) }
            }
            m[3].sortedWith(compareBy { (it as mChild).ho }).apply {
                this.forEach { m[1].add((it as mChild).ho,it) }
            }
            m[2].clear()
            m[3].clear()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT || heightMeasureSpec == ViewGroup.LayoutParams.WRAP_CONTENT)
            throw(DivisionLayoutExecption("Do Not use wrap_contents in DivisionLayout"))
        parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        groupList.forEach { _, m ->
            val vl = m.get(LayoutParams.VIRTICAL)
            val mv = vl[0] as Float
            var lv = t
            for(i in 1 until vl.size) {
                val v = vl[i] as mChild
                val c = getChildAt(v.cid)
                c.top = lv + f(false, v.dt, mv)
                c.bottom = c.top + f(false, v.dh, mv)
                lv = c.bottom + f(false, v.db, mv)
            }
            val hl = m.get(LayoutParams.HORIZONTAL)
            val mh = hl[0] as Float
            var lh = l
            for( i in 1 until hl.size) {
                val v = hl[i] as mChild
                val c = getChildAt(v.cid)
                c.left = lh + f(true, v.dl, mh)
                c.right = c.left + f(true, v.dw, mh)
                lh = c.right + f(true, v.dr, mh)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return DivisionLayout.LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is DivisionLayout.LayoutParams
    }



    private fun f(o : Boolean, v : Float, m : Float) : Int {
        if(v == 0.toFloat() || m == 0.toFloat())
            return 0
        if(o)
            return (parentWidth/(m*(1/v))).toInt()
        else
            return (parentHeight/(m*(1/v))).toInt()
    }

    class LayoutParams : ViewGroup.LayoutParams {

        companion object {
            val DEFAULT_GROUP = "-1"
            val DEFAULT_ORDER = 0
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
            val TypedArray = context.theme.obtainStyledAttributes(attrs,R.styleable.DivisionLayout_Layout,0,0)

            TypedArray.getString(R.styleable.DivisionLayout_Layout_division_group)?.let { group = it }
            dWidth = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_width, DEFAULT_VALUE)
            dHeight = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_height,DEFAULT_VALUE)
            dTop = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_top,DEFAULT_VALUE)
            dBottom = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_bottom,DEFAULT_VALUE)
            dLeft = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_left,DEFAULT_VALUE)
            dRight = TypedArray.getFloat(R.styleable.DivisionLayout_Layout_division_right,DEFAULT_VALUE)
            vOrder = TypedArray.getInt(R.styleable.DivisionLayout_Layout_division_virtical_order, DEFAULT_ORDER)
            hOrder = TypedArray.getInt(R.styleable.DivisionLayout_Layout_division_horizontal_order, DEFAULT_ORDER)

            TypedArray.recycle()
        }
    }

    private data class mChild(val cid : Int) {
        var dw = LayoutParams.DEFAULT_VALUE
        var dh = LayoutParams.DEFAULT_VALUE
        var dt = LayoutParams.DEFAULT_VALUE
        var db = LayoutParams.DEFAULT_VALUE
        var dl = LayoutParams.DEFAULT_VALUE
        var dr = LayoutParams.DEFAULT_VALUE
        var vo = LayoutParams.DEFAULT_ORDER
        var ho = LayoutParams.DEFAULT_ORDER
    }
}
