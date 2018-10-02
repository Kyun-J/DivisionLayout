package app.dvkyun.divisionlayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
    }

    private var groupList : HashMap<String,SparseArray<ArrayList<Any>>> = HashMap()
    private var defaultGroup : SparseArray<ArrayList<Int>> = SparseArray()

    private var groupJson : JSONArray = JSONArray()

    private var parentWidth = 0
    private var parentHeight = 0

    private var f = false

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
                        throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.",e))
                    }
                }
                ta.recycle()
            }
        }
    }

    //return grouplist by JSONArray
    fun getGroupsByJSON() : JSONArray {
        val gl = JSONArray()
        for(g in groupList) {
            val go = JSONObject()
            val vg = g.value[0][0] as VG
            val hg = g.value[1][0] as HG
            go.put("name",g.key)
            go.put("top",vg.t)
            go.put("height",vg.h)
            go.put("bottom",vg.b)
            go.put("left",hg.l)
            go.put("width",hg.w)
            go.put("right",hg.r)
            gl.put(go)
        }
        return gl
    }

    //return grouplist by ArrayList<DivisionGroup>
    fun getGroups() : ArrayList<DivisionGroup> {
        val ga = ArrayList<DivisionGroup>()
        for(g in groupList) {
            val dg = DivisionGroup(g.key)
            val vg = g.value[0][0] as VG
            val hg = g.value[1][0] as HG
            dg.top = vg.t
            dg.height = vg.h
            dg.bottom = vg.b
            dg.left = hg.l
            dg.width = hg.w
            dg.right = hg.r
            ga.add(dg)
        }
        return ga
    }

    //set grouplist by JSONArray
    //When this function is called, it redraws the layout
    fun setGroups(jsonArray : JSONArray) {
        groupJson = jsonArray
    }

    //set grouplist by ArrayList<DivisionGroup>
    //When this function is successfully called, it redraws the layout
    fun setGroups(arrayList: ArrayList<DivisionGroup>) {
        try {
            val gl = JSONArray()
            for(a in arrayList) {
                val go = JSONObject()
                go.put("name",a.name)
                go.put("top",a.top)
                go.put("height",a.height)
                go.put("bottom",a.bottom)
                go.put("left",a.left)
                go.put("width",a.width)
                go.put("right",a.right)
                gl.put(go)
            }
            groupJson = gl
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption("JSON error. Please contact github issue",e))
        }
    }

    //reset or add a single group by JSONObject
    //When this function is successfully called, it redraws the layout
    fun setGroup(jsonObject : JSONObject) {
        try {
            var f = -1
            for(i in 0 until groupJson.length()) {
                if(jsonObject.getString("name") == groupJson.getJSONObject(i).getString("name")) {
                    f = i
                    break
                }
            }
            val go = JSONObject()
            go.put("name",jsonObject.getString("name"))
            if(!jsonObject.isNull("top")) go.put("top",jsonObject.getDouble("top"))
            if(!jsonObject.isNull("height")) go.put("height",jsonObject.getDouble("height"))
            if(!jsonObject.isNull("bottom")) go.put("bottom",jsonObject.getDouble("bottom"))
            if(!jsonObject.isNull("left")) go.put("left",jsonObject.getDouble("left"))
            if(!jsonObject.isNull("width")) go.put("width",jsonObject.getDouble("width"))
            if(!jsonObject.isNull("right")) go.put("right",jsonObject.getDouble("right"))
            if(f != -1) groupJson.put(f,go)
            else groupJson.put(go)
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption("Illegal format of group-Json, please check inserted JSONObject.",e))
        }
    }

    //reset or add a single group by JSONObject
    //When this function is successfully called, it redraws the layout
    fun setGroup(divisionGroup: DivisionGroup) {
        try {
            var f = -1
            for(i in 0 until groupJson.length()) {
                if(divisionGroup.name == groupJson.getJSONObject(i).getString("name")) {
                    f = i
                    break
                }
            }
            val go = JSONObject()
            go.put("name",divisionGroup.name)
            go.put("top",divisionGroup.top)
            go.put("height",divisionGroup.height)
            go.put("bottom",divisionGroup.bottom)
            go.put("left",divisionGroup.left)
            go.put("width",divisionGroup.width)
            go.put("right",divisionGroup.right)
            if(f != -1) groupJson.put(f,go)
            else groupJson.put(go)
            placeGroup()
            requestLayout()
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption("JSON error. Please contact github issue",e))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("mmmmmm","draw")
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        Log.i("mmmmmm","addview")
        if(f) { placeGroup() }
    }

    override fun removeView(view: View?) {
        super.removeView(view)
        if(f) placeGroup()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.i("mmmmmm","attach")
        placeGroup()
        f = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        f = false
    }

    override fun requestLayout() {
        super.requestLayout()
        Log.i("mmmmmm","req")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i("mmmmmm","measure")
        if(layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT || layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT)
            throw(DivisionLayoutExecption("Do not use wrap_contents in DivisionLayout"))
        parentWidth = measuredWidth
        parentHeight = measuredHeight
        defaultGroup[0].forEach {
            val lp = getChildAt(it).layoutParams as DivisionLayout.LayoutParams
            lp.mhs = when(lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                0 -> getChildMeasureSpec(heightMeasureSpec,0,f(parentHeight,lp.dHeight,lp.dTop+lp.dHeight+lp.dBottom))
                else -> getChildMeasureSpec(heightMeasureSpec,0,lp.height)
            }
            lp.mc = true
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mws = when(lp.width) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                0 -> getChildMeasureSpec(widthMeasureSpec,0,f(parentWidth,lp.dWidth,lp.dLeft+lp.dWidth+lp.dRight))
                else -> getChildMeasureSpec(widthMeasureSpec,0,lp.width)
            }

            if(lp.mc){
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
            } else
                lp.mc = true
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as VG
            vgs.f = f(parentHeight,vgs.t,vgs.t+vgs.h+vgs.b)
            vgs.v = f(parentHeight,vgs.h,vgs.t+vgs.h+vgs.b)
            for(i in 1 until vl.size) {
                val c = getChildAt(vl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(0,getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT))
                    vgs.v -= c.measuredHeight
                } else if(lp.height != 0)
                    vgs.v -= lp.height
            }
            val hl = g[1]
            val hgs = hl[0] as HG
            hgs.f = f(parentWidth,hgs.l,hgs.l+hgs.w+hgs.r)
            hgs.v = f(parentWidth,hgs.w,hgs.l+hgs.w+hgs.r)
            for(i in 1 until hl.size) {
                val c = getChildAt(hl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT){
                    c.measure(getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT),0)
                    hgs.v -= c.measuredWidth
                } else if(lp.width != 0)
                    hgs.v -= lp.width
            }
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as VG
            for(i in 1 until vl.size) {
                val c = getChildAt(vl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mhs = when(lp.height) {
                    ViewGroup.LayoutParams.WRAP_CONTENT ->  getChildMeasureSpec(heightMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                    0 -> getChildMeasureSpec(heightMeasureSpec,0,f(vgs.v,lp.dHeight,vgs.m))
                    else -> getChildMeasureSpec(heightMeasureSpec,0,lp.height)
                }
                if(lp.mc){
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
            val hl = g[1]
            val hgs = hl[0] as HG
            for(i in 1 until hl.size) {
                val c = getChildAt(hl[i] as Int)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mws = when(lp.width) {
                    ViewGroup.LayoutParams.WRAP_CONTENT -> getChildMeasureSpec(widthMeasureSpec,0,ViewGroup.LayoutParams.WRAP_CONTENT)
                    0 -> getChildMeasureSpec(widthMeasureSpec,0,f(hgs.v,lp.dWidth,hgs.m))
                    else -> getChildMeasureSpec(widthMeasureSpec,0,lp.width)
                }
                if(lp.mc){
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.i("mmmmmm","layout")
        defaultGroup[0].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dTop + lp.dBottom
            lp.lt = f(parentHeight-lp.mh,lp.dTop,m)
            lp.lb = lp.lt + lp.mh
            lp.lc = true
        }
        defaultGroup[1].forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.dLeft + lp.dRight
            lp.ll = f(parentWidth-lp.mw,lp.dLeft,m)
            lp.lr = lp.ll + lp.mw
            if(lp.lc) c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
            else lp.lc = true
        }
        for(g in groupList.values) {
            val vl = g[0]
            val vgs = vl[0] as VG
            val vm = vgs.m
            var lv = vgs.f
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
            val hgs = hl[0] as HG
            val hm = hgs.m
            var lh = hgs.f
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

    private fun placeGroup() {
        Log.i("mmmmmm","place")
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
                val vgs = vl[0] as VG
                vgs.m +=
                        if(lp.height == 0)
                            lp.dTop + lp.dHeight + lp.dBottom
                        else
                            lp.dTop + lp.dBottom
                if(lp.vOrder == LayoutParams.DEFAULT_ORDER) vl.add(i)
                else vol.add(DCO(i,lp.vOrder))
            } else defaultGroup[0].add(i)
            if(lp.hGroup != LayoutParams.DEFAULT_GROUP) {
                val hl = groupList[lp.hGroup]!![1]
                val hol = groupList[lp.hGroup]!![3]
                val hgs = hl[0] as HG
                hgs.m +=
                        if(lp.width == 0)
                            lp.dRight + lp.dWidth + lp.dLeft
                        else
                            lp.dRight + lp.dLeft
                if(lp.hOrder == LayoutParams.DEFAULT_ORDER) hl.add(i)
                else hol.add(DCO(i,lp.hOrder))
            } else defaultGroup[1].add(i)
        }
        for(g in groupList.values) {
            g[2].sortedWith(compareBy { (it as DCO).o }).apply {
                this.forEach {
                    it as DCO
                    if(it.o > g[0].size)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group. Also, please arrange the order starting from 1"))
                    g[0].add(it.o,it.i)
                }
            }
            g[3].sortedWith(compareBy { (it as DCO).o }).apply {
                this.forEach {
                    it as DCO
                    if(it.o > g[1].size)
                        throw(DivisionLayoutExecption("Order can not be greater than the number of views in group. Also, please arrange the order starting from 1"))
                    g[1].add(it.o,it.i)
                }
            }
            g[2].clear()
            g[3].clear()
        }
    }

    private fun attrGroupSet() {
        for(i in 0 until groupJson.length()) {
            try {
                val g = groupJson.getJSONObject(i)
                val n = g.getString("name")
                setGroup(n)
                val vs = groupList[n]!![0][0] as VG
                if(!g.isNull("top")) vs.t = g.getDouble("top").toFloat()
                if(!g.isNull("height")) vs.h = g.getDouble("height").toFloat()
                if(!g.isNull("bottom")) vs.b = g.getDouble("bottom").toFloat()
                val hs = groupList[n]!![1][0] as HG
                if(!g.isNull("left")) hs.l = g.getDouble("left").toFloat()
                if(!g.isNull("width")) hs.w = g.getDouble("width").toFloat()
                if(!g.isNull("right")) hs.r = g.getDouble("right").toFloat()
            } catch (e : JSONException) {
                Log.w(TAG,e.message)
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
            d[0].add(VG())
            d[1].add(HG())
            groupList[n] = d
        }
    }


    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v == 0F || m == 0F) 0
        else (p/m*v).toInt()
    }

    override fun generateLayoutParams(attrs: AttributeSet): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): DivisionLayout.LayoutParams {
        return DivisionLayout.LayoutParams(0, 0)
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
            const val DEFAULT_VALUE = 0F
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

    private class VG {
        internal var m = 0F
        internal var f = 0
        internal var v = 0
        internal var t = 0F
        internal var b = 0F
        internal var h = 1F
    }

    private class HG {
        internal var m = 0F
        internal var f = 0
        internal var v = 0
        internal var l = 0F
        internal var r = 0F
        internal var w = 1F
    }
}