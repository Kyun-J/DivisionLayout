package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
    }

    private lateinit var groupList : HashMap<String,SparseArray<ArrayList<Any>>>
    private lateinit var defaultView : SparseArray<ArrayList<DChild>>

    private var parentWidth = 0
    private var parentHeight = 0

    private lateinit var groupJson : JSONArray

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context?.let {c ->
            attrs?.let { a->
                val ta = c.theme.obtainStyledAttributes(a,R.styleable.DivisionLayout,0,0)
                ta.getString(R.styleable.DivisionLayout_division_create_groups)?.let {
                    try {
                        groupJson = JSONArray(it)
                    } catch (e : JSONException) {
                        Log.w(TAG,e.message)
                    }
                }
                ta.recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        groupList = HashMap()
        defaultView = SparseArray()
        defaultView.put(0, ArrayList())
        defaultView.put(1, ArrayList())

        attrGroupSet()

        for(i in 0 until childCount) {
            val lp = getChildAt(i).layoutParams as DivisionLayout.LayoutParams
            if(lp.vOrder > childCount || lp.hOrder >= childCount)
                throw(DivisionLayoutExecption("order can not be greater than the number of views."))
            if(lp.dWidth < 0 || lp.dHeight < 0 || lp.dTop < 0 || lp.dBottom < 0 || lp.dLeft < 0 || lp.dRight < 0)
                throw(DivisionLayoutExecption("value must be greater than -1."))
            if(lp.vOrder < LayoutParams.DEFAULT_ORDER || lp.hOrder < LayoutParams.DEFAULT_ORDER)
                throw(DivisionLayoutExecption("order must be greater than "+ LayoutParams.DEFAULT_ORDER.toString()+"."))
            setGroup(lp.vGroup); setGroup(lp.hGroup)
            if(lp.vGroup != LayoutParams.DEFAULT_GROUP) {
                val vl = groupList[lp.vGroup]!![0]
                val vol = groupList[lp.vGroup]!![2]
                val vc = DChild(i)
                vc.o = lp.vOrder
                (vl[0] as DGroupSet).m += lp.dTop + lp.dHeight + lp.dBottom
                if(vc.o == LayoutParams.DEFAULT_ORDER) vl.add(vc)
                else vol.add(vc)
            } else defaultView[0].add(DChild(i))
            if(lp.hGroup != LayoutParams.DEFAULT_GROUP) {
                val hl = groupList[lp.hGroup]!![1]
                val hol = groupList[lp.hGroup]!![3]
                val hc = DChild(i)
                hc.o = lp.hOrder
                (hl[0] as DGroupSet).m += lp.dRight + lp.dWidth + lp.dLeft
                if(hc.o == LayoutParams.DEFAULT_ORDER) hl.add(hc)
                else hol.add(hc)
            } else defaultView[1].add(DChild(i))
        }
        groupList.forEach { _ , m ->
            m[2].sortedWith(compareBy { (it as DChild).o }).apply {
                this.forEach { m[0].add((it as DChild).o,it) }
            }
            m[3].sortedWith(compareBy { (it as DChild).o }).apply {
                this.forEach { m[1].add((it as DChild).o,it) }
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
        defaultView[0].forEach {
            val lp = getChildAt(it.cid).layoutParams as DivisionLayout.LayoutParams
            lp.mHeight = f(parentHeight,lp.dHeight,lp.dTop+lp.dHeight+lp.dBottom)
        }
        defaultView[1].forEach {
            val c = getChildAt(it.cid)
            val lp = getChildAt(it.cid).layoutParams as DivisionLayout.LayoutParams
            lp.mWidth = f(parentWidth,lp.dWidth,lp.dLeft+lp.dWidth+lp.dRight)
            if(lp.mWidth != -1)
                c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
        }
        groupList.forEach { _ , m ->
            val vl = m[0]
            val vgs = vl[0] as DGroupSet
            vgs.f = f(parentHeight,vgs.t,vgs.t+vgs.h+vgs.b)
            vgs.v = f(parentHeight,vgs.h,vgs.t+vgs.h+vgs.b)
            val vm = vgs.m
            for(i in 1 until vl.size) {
                val v = vl[i] as DChild
                val c = getChildAt(v.cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mHeight = f(vgs.v,lp.dHeight,vm)
                if(lp.mWidth != -1)
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
            }
            val hl = m[1]
            val hgs = hl[0] as DGroupSet
            hgs.f = f(parentWidth,hgs.l,hgs.l+hgs.w+hgs.r)
            hgs.v = f(parentWidth,hgs.w,hgs.l+hgs.w+hgs.r)
            val hm = hgs.m
            for(i in 1 until hl.size) {
                val v = hl[i] as DChild
                val c = getChildAt(v.cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mWidth = f(hgs.v,lp.dWidth,hm)
                if(lp.mHeight != -1)
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        defaultView[0].forEach {
            val c = getChildAt(it.cid)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dTop + lp.dHeight + lp.dBottom
            c.top = t + f(parentHeight,lp.dTop,m)
            c.bottom = c.top + f(parentHeight,lp.dHeight,m)
        }
        defaultView[1].forEach {
            val c = getChildAt(it.cid)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dLeft + lp.dWidth + lp.dRight
            c.left = l + f(parentWidth,lp.dLeft,m)
            c.right = c.left + f(parentWidth,lp.dWidth,m)
        }
        groupList.forEach { _ , m ->
            val vl = m[0]
            val vgs = vl[0] as DGroupSet
            val vm = vgs.m
            var lv = t + vgs.f
            for(i in 1 until vl.size) {
                val v = vl[i] as DChild
                val c = getChildAt(v.cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                c.top = lv + f(vgs.v, lp.dTop, vm)
                c.bottom = c.top + f(vgs.v, lp.dHeight, vm)
                lv = c.bottom + f(vgs.v, lp.dBottom, vm)
            }
            val hl = m[1]
            val hgs = hl[0] as DGroupSet
            val hm = hgs.m
            var lh = l + hgs.f
            for( i in 1 until hl.size) {
                val v = hl[i] as DChild
                val c = getChildAt(v.cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                c.left = lh + f(hgs.v, lp.dLeft, hm)
                c.right = c.left + f(hgs.v, lp.dWidth, hm)
                lh = c.right + f(hgs.v, lp.dRight, hm)
            }
        }
    }

    private fun attrGroupSet() {
        if(::groupJson.isInitialized) {
            for(i in 0 until groupJson.length()) {
                try {
                    val g = groupJson.getJSONObject(i)
                    val n = g.getString("name")
                    setGroup(n)
                    val vs = groupList[n]!![0][0] as DGroupSet
                    if(!g.isNull("top")) vs.t = g.getDouble("top").toFloat()
                    if(!g.isNull("height")) vs.h = g.getDouble("height").toFloat()
                    if(!g.isNull("bottom")) vs.b = g.getDouble("bottom").toFloat()
                    val hs = groupList[n]!![1][0] as DGroupSet
                    if(!g.isNull("left")) hs.l = g.getDouble("left").toFloat()
                    if(!g.isNull("width")) hs.w = g.getDouble("width").toFloat()
                    if(!g.isNull("right")) hs.r = g.getDouble("right").toFloat()
                } catch (e : JSONException) {
                    Log.w(TAG,e.message)
                }
            }
        }
    }

    private fun setGroup(n : String) {
        if(!groupList.containsKey(n)) {
            val d = SparseArray<ArrayList<Any>>().apply {
                put(0,ArrayList())
                put(1,ArrayList())
                put(2,ArrayList())
                put(3,ArrayList())
            }
            d[0].add(DGroupSet())
            d[1].add(DGroupSet())
            groupList[n] = d
        }
    }

    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v == 0.toFloat() || m == 0.toFloat()) 0
        else (p/(m*(1/v))).toInt()
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

    class LayoutParams : ViewGroup.LayoutParams {

        companion object {
            const val DEFAULT_GROUP = "-1"
            const val DEFAULT_ORDER = 0
            const val DEFAULT_VALUE = 0.toFloat()
        }

        var vGroup = DEFAULT_GROUP
        var hGroup = DEFAULT_GROUP
        var dWidth = DEFAULT_VALUE
        var dHeight = DEFAULT_VALUE
        var dTop = DEFAULT_VALUE
        var dBottom = DEFAULT_VALUE
        var dLeft = DEFAULT_VALUE
        var dRight = DEFAULT_VALUE
        var vOrder = DEFAULT_ORDER
        var hOrder = DEFAULT_ORDER

        internal var mWidth = -1
        internal var mHeight = -1

        constructor(context: Context?,attrs: AttributeSet?) : super(context,attrs) {
            context?.let { c -> attrs?.let { a -> setAttrs(c,a) } }
        }
        constructor(width : Int, height : Int) : super(width,height)
        constructor(params: ViewGroup.LayoutParams) : super(params)

        private fun setAttrs(context: Context, attrs: AttributeSet) {
            val ta = context.theme.obtainStyledAttributes(attrs,R.styleable.DivisionLayout_Layout,0,0)

            ta.getString(R.styleable.DivisionLayout_Layout_division_virtical_group)?.let { vGroup = it }
            ta.getString(R.styleable.DivisionLayout_Layout_division_horizontal_group)?.let { hGroup = it }
            dWidth = ta.getFloat(R.styleable.DivisionLayout_Layout_division_width,DEFAULT_VALUE)
            dHeight = ta.getFloat(R.styleable.DivisionLayout_Layout_division_height,DEFAULT_VALUE)
            dTop = ta.getFloat(R.styleable.DivisionLayout_Layout_division_top,DEFAULT_VALUE)
            dBottom = ta.getFloat(R.styleable.DivisionLayout_Layout_division_bottom,DEFAULT_VALUE)
            dLeft = ta.getFloat(R.styleable.DivisionLayout_Layout_division_left,DEFAULT_VALUE)
            dRight = ta.getFloat(R.styleable.DivisionLayout_Layout_division_right,DEFAULT_VALUE)
            vOrder = ta.getInt(R.styleable.DivisionLayout_Layout_division_virtical_order,DEFAULT_ORDER)
            hOrder = ta.getInt(R.styleable.DivisionLayout_Layout_division_horizontal_order,DEFAULT_ORDER)

            ta.recycle()
        }

    }

    private data class DChild(val cid : Int) {
        var o = LayoutParams.DEFAULT_ORDER
    }

    private class DGroupSet {
        var m = 0.toFloat()
        var f = 0
        var v = 0

        var l = 0.toFloat()
        var t = 0.toFloat()
        var r = 0.toFloat()
        var b = 0.toFloat()
        var h = 1.toFloat()
        var w = 1.toFloat()
    }
}
