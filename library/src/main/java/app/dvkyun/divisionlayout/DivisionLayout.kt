package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
    }

    private var groupList : HashMap<String,SparseArray<ArrayList<Any>>> = HashMap()
    private var defaultGroup : SparseArray<ArrayList<Int>> = SparseArray()

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
            if(lp.vOrder > childCount || lp.hOrder > childCount)
                throw(DivisionLayoutExecption("Order can not be greater than the number of views."))
            if(lp.dWidth < 0 || lp.dHeight < 0 || lp.dTop < 0 || lp.dBottom < 0 || lp.dLeft < 0 || lp.dRight < 0)
                throw(DivisionLayoutExecption("Value must be greater than -1."))
            if(lp.vOrder < LayoutParams.DEFAULT_ORDER || lp.hOrder < LayoutParams.DEFAULT_ORDER)
                throw(DivisionLayoutExecption("Order must be greater than "+ LayoutParams.DEFAULT_ORDER.toString()+"."))
            if(lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                lp.vGroup = LayoutParams.DEFAULT_GROUP
                lp.dTop = 0.toFloat()
                lp.dBottom = 0.toFloat()
                lp.dHeight = 1.toFloat()
            }
            if(lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                lp.hGroup = LayoutParams.DEFAULT_GROUP
                lp.dRight = 0.toFloat()
                lp.dLeft = 0.toFloat()
                lp.dWidth = 1.toFloat()
            }
            setGroup(lp.vGroup); setGroup(lp.hGroup)
            if(lp.vGroup != LayoutParams.DEFAULT_GROUP) {
                val vl = groupList[lp.vGroup]!![0]
                val vol = groupList[lp.vGroup]!![2]
                val vgs = vl[0] as DGroupSet
                vgs.m +=
                        if(lp.height == 0)
                            lp.dTop + lp.dHeight + lp.dBottom
                        else
                            lp.dTop + lp.dBottom
                vgs.n += 1
                if(lp.vOrder == LayoutParams.DEFAULT_ORDER) vl.add(i)
                else vol.add(DCO(i,lp.vOrder))
            } else defaultGroup[0].add(i)
            if(lp.hGroup != LayoutParams.DEFAULT_GROUP) {
                val hl = groupList[lp.hGroup]!![1]
                val hol = groupList[lp.hGroup]!![3]
                val hgs = hl[0] as DGroupSet
                hgs.m +=
                        if(lp.width == 0)
                            lp.dRight + lp.dWidth + lp.dLeft
                        else
                            lp.dRight + lp.dLeft
                hgs.n += 1
                if(lp.hOrder == LayoutParams.DEFAULT_ORDER) hl.add(i)
                else hol.add(DCO(i,lp.hOrder))
            } else defaultGroup[1].add(i)
        }
        for(g in groupList.values) {
            g[2].sortedWith(compareBy { (it as DCO).o }).apply {
                this.forEach {
                    it as DCO
                    if(it.o > (g[0][0] as DGroupSet).n)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group."))
                    g[0].add(it.o,it.i)
                }
            }
            g[3].sortedWith(compareBy { (it as DCO).o }).apply {
                this.forEach {
                    it as DCO
                    if(it.o > (g[1][0] as DGroupSet).n)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group."))
                    g[1].add(it.o,it.i)
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
            val lp = getChildAt(it).layoutParams as DivisionLayout.LayoutParams
            lp.mhs =
                    if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT)
                        getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                    else if (lp.height == 0)
                        getChildMeasureSpec(heightMeasureSpec,0,f(parentHeight,lp.dHeight,lp.dTop+lp.dHeight+lp.dBottom))
                    else
                        getChildMeasureSpec(heightMeasureSpec,0,lp.height)
            lp.mc = true
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mws =
                    if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT)
                        getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                    else if(lp.width == 0)
                        getChildMeasureSpec(widthMeasureSpec,0,f(parentWidth,lp.dWidth,lp.dLeft+lp.dWidth+lp.dRight))
                    else
                        getChildMeasureSpec(widthMeasureSpec,0,lp.width)
            if(lp.mc){
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
            } else
                lp.mc = true
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as DGroupSet
            vgs.f = f(parentHeight,vgs.t,vgs.t+vgs.h+vgs.b)
            vgs.v = f(parentHeight,vgs.h,vgs.t+vgs.h+vgs.b)
            for(i in 1 until vl.size) {
                val c = getChildAt(vl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT){
                    c.measure(0,getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT))
                    vgs.v -= c.measuredHeight
                } else if(lp.height != 0) {
                    vgs.v -= lp.height
                }
            }
            val hl = g[1]
            val hgs = hl[0] as DGroupSet
            hgs.f = f(parentWidth,hgs.l,hgs.l+hgs.w+hgs.r)
            hgs.v = f(parentWidth,hgs.w,hgs.l+hgs.w+hgs.r)
            for(i in 1 until hl.size) {
                val c = getChildAt(hl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT){
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT),0)
                    hgs.v -= c.measuredWidth
                } else if(lp.width != 0) {
                    hgs.v -= lp.width
                }
            }
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as DGroupSet
            for(i in 1 until vl.size) {
                val c = getChildAt(vl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mhs =
                        if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT)
                            getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                        else if(lp.height == 0)
                            getChildMeasureSpec(heightMeasureSpec,0,f(vgs.v,lp.dHeight,vgs.m))
                        else
                            getChildMeasureSpec(heightMeasureSpec,0,lp.height)
                if(lp.mc){
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
            val hl = g[1]
            val hgs = hl[0] as DGroupSet
            for(i in 1 until hl.size) {
                val c = getChildAt(hl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mws =
                        if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT)
                            getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                        else if(lp.width == 0)
                            getChildMeasureSpec(widthMeasureSpec,0,f(hgs.v,lp.dWidth,hgs.m))
                        else
                            getChildMeasureSpec(widthMeasureSpec,0,lp.width)
                if(lp.mc){
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
        }
        setMeasuredDimension(parentWidth,parentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        defaultGroup[0].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dTop + lp.dBottom
            lp.lt = t + f(parentHeight-lp.mh,lp.dTop,m)
            lp.lb = lp.lt + lp.mh
            lp.lc = true
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dLeft + lp.dRight
            lp.ll = l + f(parentWidth-lp.mw,lp.dLeft,m)
            lp.lr = lp.ll + lp.mw
            if(lp.lc) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
            else lp.lc = true
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as DGroupSet
            val vm = vgs.m
            var lv = t + vgs.f
            for(i in 1 until vl.size) {
                val c = getChildAt(vl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.lt = lv + f(vgs.v, lp.dTop, vm)
                lp.lb = lp.lt + lp.mh
                lv = lp.lb + f(vgs.v, lp.dBottom, vm)
                if(lp.lc) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                else lp.lc = true
            }
            val hl = g[1]
            val hgs = hl[0] as DGroupSet
            val hm = hgs.m
            var lh = l + hgs.f
            for( i in 1 until hl.size) {
                val c = getChildAt(hl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.ll= lh + f(hgs.v, lp.dLeft, hm)
                lp.lr = lp.ll + lp.mw
                lh = lp.lr + f(hgs.v, lp.dRight, hm)
                if(lp.lc) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                else lp.lc = true
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

        internal var mc = false
        internal var mws = 0
        internal var mhs = 0
        internal var lc = false
        internal var ll = 0
        internal var lt = 0
        internal var lr = 0
        internal var lb = 0
        internal var mw = 0
        internal var mh = 0

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

    private data class DCO(val i : Int, val o : Int)

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