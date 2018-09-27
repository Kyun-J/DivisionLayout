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

    private var groupList : HashMap<String,SparseArray<ArrayList<Any>>> = HashMap()
    private var defaultGroup : SparseArray<ArrayList<DChild>> = SparseArray()

    private lateinit var groupJson : JSONArray

    private var parentWidth = 0
    private var parentHeight = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context?.let { c ->
            attrs?.let { a ->
                val ta = c.theme.obtainStyledAttributes(a,R.styleable.DivisionLayout,0,0)
                ta.getString(R.styleable.DivisionLayout_division_create_groups)?.let {
                    try {
                        groupJson = JSONArray(it)
                    } catch (e : JSONException) {
                        throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups."))
                    }
                }
                ta.recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        groupList.clear()
        defaultGroup.clear()
        defaultGroup.put(0, ArrayList())
        defaultGroup.put(1, ArrayList())

        attrGroupSet()

        for(i in 0 until childCount) {
            val lp = getChildAt(i).layoutParams as DivisionLayout.LayoutParams
            if(lp.vOrder > childCount || lp.hOrder >= childCount)
                throw(DivisionLayoutExecption("Order can not be greater than the number of views."))
            if(lp.dWidth < 0 || lp.dHeight < 0 || lp.dTop < 0 || lp.dBottom < 0 || lp.dLeft < 0 || lp.dRight < 0)
                throw(DivisionLayoutExecption("Value must be greater than -1."))
            if(lp.vOrder < LayoutParams.DEFAULT_ORDER || lp.hOrder < LayoutParams.DEFAULT_ORDER)
                throw(DivisionLayoutExecption("Order must be greater than "+ LayoutParams.DEFAULT_ORDER.toString()+"."))
            setGroup(lp.vGroup); setGroup(lp.hGroup)
            if(lp.vGroup != LayoutParams.DEFAULT_GROUP) {
                val vl = groupList[lp.vGroup]!![0]
                val vol = groupList[lp.vGroup]!![2]
                val vc = DChild(i)
                vc.o = lp.vOrder
                (vl[0] as DGroupSet).m += lp.dTop + lp.dHeight + lp.dBottom
                (vl[0] as DGroupSet).n += 1
                if(vc.o == LayoutParams.DEFAULT_ORDER) vl.add(vc)
                else vol.add(vc)
            } else defaultGroup[0].add(DChild(i))
            if(lp.hGroup != LayoutParams.DEFAULT_GROUP) {
                val hl = groupList[lp.hGroup]!![1]
                val hol = groupList[lp.hGroup]!![3]
                val hc = DChild(i)
                hc.o = lp.hOrder
                (hl[0] as DGroupSet).m += lp.dRight + lp.dWidth + lp.dLeft
                (hl[0] as DGroupSet).n += 1
                if(hc.o == LayoutParams.DEFAULT_ORDER) hl.add(hc)
                else hol.add(hc)
            } else defaultGroup[1].add(DChild(i))
        }
        for(g in groupList.values) {
            g[2].sortedWith(compareBy { (it as DChild).o }).apply {
                this.forEach {
                    it as DChild
                    if(it.o > (g[0][0] as DGroupSet).n)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group."))
                    g[0].add(it.o,it)
                }
            }
            g[3].sortedWith(compareBy { (it as DChild).o }).apply {
                this.forEach {
                    it as DChild
                    if(it.o > (g[1][0] as DGroupSet).n)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group."))
                    g[1].add(it.o,it)
                }
            }
            g[2].clear()
            g[3].clear()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
            throw(DivisionLayoutExecption("Do not use wrap_contents in DivisionLayout"))
        parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        defaultGroup[0].forEach {
            val lp = getChildAt(it.cid).layoutParams as DivisionLayout.LayoutParams
            lp.mHeight = f(parentHeight,lp.dHeight,lp.dTop+lp.dHeight+lp.dBottom)
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it.cid)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mWidth = f(parentWidth,lp.dWidth,lp.dLeft+lp.dWidth+lp.dRight)
            if(lp.mHeight != -1)
                c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as DGroupSet
            vgs.f = f(parentHeight,vgs.t,vgs.t+vgs.h+vgs.b)
            vgs.v = f(parentHeight,vgs.h,vgs.t+vgs.h+vgs.b)
            val vm = vgs.m
            for(i in 1 until vl.size) {
                val c = getChildAt((vl[i] as DChild).cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mHeight = f(vgs.v,lp.dHeight,vm)
                if(lp.mWidth != -1)
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
            }
            val hl = g[1]
            val hgs = hl[0] as DGroupSet
            hgs.f = f(parentWidth,hgs.l,hgs.l+hgs.w+hgs.r)
            hgs.v = f(parentWidth,hgs.w,hgs.l+hgs.w+hgs.r)
            val hm = hgs.m
            for(i in 1 until hl.size) {
                val c = getChildAt((hl[i] as DChild).cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mWidth = f(hgs.v,lp.dWidth,hm)
                if(lp.mHeight != -1)
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.mWidth), getChildMeasureSpec(heightMeasureSpec,0,lp.mHeight))
            }
        }
        setMeasuredDimension(parentWidth,parentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        defaultGroup[0].forEach {
            val c = getChildAt(it.cid)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dTop + lp.dHeight + lp.dBottom
            lp.lt = t + f(parentHeight,lp.dTop,m)
            lp.lb = lp.lt + f(parentHeight,lp.dHeight,m)
            lp.lCheck = true
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it.cid)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dLeft + lp.dWidth + lp.dRight
            lp.ll = l + f(parentWidth,lp.dLeft,m)
            lp.lr = lp.ll + f(parentWidth,lp.dWidth,m)
            if(lp.lCheck) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
            else lp.lCheck = true
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as DGroupSet
            val vm = vgs.m
            var lv = t + vgs.f
            for(i in 1 until vl.size) {
                val c = getChildAt((vl[i] as DChild).cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.lt = lv + f(vgs.v, lp.dTop, vm)
                lp.lb = lp.lt + f(vgs.v, lp.dHeight, vm)
                lv = lp.lb + f(vgs.v, lp.dBottom, vm)
                if(lp.lCheck) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                else lp.lCheck = true
            }
            val hl = g[1]
            val hgs = hl[0] as DGroupSet
            val hm = hgs.m
            var lh = l + hgs.f
            for( i in 1 until hl.size) {
                val c = getChildAt((hl[i] as DChild).cid)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.ll= lh + f(hgs.v, lp.dLeft, hm)
                lp.lr = lp.ll + f(hgs.v, lp.dWidth, hm)
                lh = lp.lr + f(hgs.v, lp.dRight, hm)
                if(lp.lCheck) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                else lp.lCheck = true
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
        else (p/m*v).toInt()
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
        internal var lCheck = false
        internal var ll = 0
        internal var lt = 0
        internal var lr = 0
        internal var lb = 0

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
        var n = 0

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