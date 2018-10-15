package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
    }

    private val groupList : HashMap<String,G> = HashMap()
    private lateinit var groupJson : JSONArray

    private val defaultVerticalGroup : ArrayList<Int> = ArrayList()
    private val defaultHorizontalGroup : ArrayList<Int> = ArrayList()

    private var verticalWrapMode = false
    private var horizontalWrapMode = false

    private var isAttach = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context?.let { c ->
            attrs?.let { a ->
                val st = R.styleable::class.java.getField("DivisionLayout").get(R.styleable::class.java) as IntArray
                val ta = c.theme.obtainStyledAttributes(a,R.styleable.DivisionLayout,0,0)
                ta.getString(R.styleable.DivisionLayout_division_create_groups).let {
                    when(it) {
                        null -> {
                            groupJson = JSONArray()
                            groupJson
                        }
                        else ->
                            try {
                                groupJson = attrGroupJsonArray(it)
                            } catch (e: JSONException) {
                                throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.", e))
                            }
                    }
                }
                st.forEach { id ->
                    if(id != R.styleable.DivisionLayout_division_create_groups) {
                        ta.getString(id)?.let {
                            try {
                                groupJson.put(attrGroupJsonObject(it))
                            } catch (e : JSONException) {
                                throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.",e))
                            }
                        }
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
            go.put("name",g.key)
            go.put("top",g.value.t)
            go.put("height",g.value.h)
            go.put("bottom",g.value.b)
            go.put("left",g.value.l)
            go.put("width",g.value.w)
            go.put("right",g.value.r)
            gl.put(go)
        }
        return gl
    }

    //return grouplist by ArrayList<DivisionGroup>
    fun getGroups() : ArrayList<DivisionGroup> {
        val ga = ArrayList<DivisionGroup>()
        for(g in groupList) {
            val dg = DivisionGroup(g.key)
            dg.top is String
            dg.top = g.value.t
            dg.height = g.value.h
            dg.bottom = g.value.b
            dg.left = g.value.l
            dg.width = g.value.w
            dg.right = g.value.r
            ga.add(dg)
        }
        return ga
    }

    //set grouplist by JSONArray
    //When this function is called, it redraws the layout
    fun setGroups(jsonArray : JSONArray) {
        groupJson = jsonArray
        requestLayout()
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
            requestLayout()
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
            if(!jsonObject.isNull("top")) go.put("top",jsonObject.get("top"))
            if(!jsonObject.isNull("height")) go.put("height",jsonObject.get("height"))
            if(!jsonObject.isNull("bottom")) go.put("bottom",jsonObject.get("bottom"))
            if(!jsonObject.isNull("left")) go.put("left",jsonObject.get("left"))
            if(!jsonObject.isNull("width")) go.put("width",jsonObject.get("width"))
            if(!jsonObject.isNull("right")) go.put("right",jsonObject.get("right"))
            if(f != -1) groupJson.put(f,go)
            else groupJson.put(go)
            requestLayout()
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
            requestLayout()
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption("JSON error. Please contact github issue",e))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttach = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttach = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        verticalWrapMode = layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT
        horizontalWrapMode = layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
        var mw = if(!horizontalWrapMode) MeasureSpec.getSize(widthMeasureSpec) else 0
        var mh = if(!verticalWrapMode) MeasureSpec.getSize(heightMeasureSpec) else 0
        if(verticalWrapMode || horizontalWrapMode) {
            for (i in 0 until childCount) {
                val c = getChildAt(i)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (verticalWrapMode) {
                    lp.divTop = 0F; lp.divHeight = 0F; lp.divBottom = 0F
                    if (lp.height > 0) mh += lp.height
                    else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        c.measure(0, getChildMeasureSpec(heightMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT))
                        mh += c.measuredHeight
                    }
                }
                if (horizontalWrapMode) {
                    lp.divLeft = 0F; lp.divWidth = 0F; lp.divRight = 0F
                    if (lp.width > 0) mw += lp.width
                    else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        c.measure(getChildMeasureSpec(widthMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT), 0)
                        mw += c.measuredWidth
                    }
                }
            }
        }
        setMeasuredDimension(mw,mh)
        if(isAttach) measureWithGroup(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for(g in groupList.values) {
            var lv = g.vf
            g.verticalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.lt = lv + f(g.vv, lp.divTop, g.va)
                lp.lb = lp.lt + lp.mh
                lv = lp.lb + f(g.vv, lp.divBottom, g.va)
                if(lp.lc) {
                    c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                    lp.lc = false
                }
                else lp.lc = true
            }
            var lh = g.hf
            g.horizontalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.ll = lh + f(g.hv, lp.divLeft, g.ha)
                lp.lr = lp.ll + lp.mw
                lh = lp.lr + f(g.hv, lp.divRight, g.ha)
                if(lp.lc) {
                    c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                    lp.lc = false
                }
                else lp.lc = true
            }
        }
        defaultVerticalGroup.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.divTop + lp.divBottom
            lp.lt = f(measuredHeight-lp.mh,lp.divTop,m)
            lp.lb = lp.lt + lp.mh
            if(lp.lc) {
                c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                lp.lc = false
            }
            else lp.lc = true
        }
        defaultHorizontalGroup.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.divLeft + lp.divRight
            lp.ll = f(measuredWidth-lp.mw,lp.divLeft,m)
            lp.lr = lp.ll + lp.mw
            if(lp.lc) {
                c.layout(lp.ll,lp.lt,lp.lr,lp.lb)
                lp.lc = false
            }
        }
    }

    private fun measureWithGroup(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for(g in groupList.values) g.reset()
        defaultVerticalGroup.clear()
        defaultHorizontalGroup.clear()

        GroupJsonSet()

        for(i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            if(lp.verticalOrder > childCount || lp.horizontalOrder > childCount)
                throw(DivisionLayoutExecption("Order can not be greater than the number of views."))
            if(lp.divWidth < 0 || lp.divHeight < 0 || lp.divTop < 0 || lp.divBottom < 0 || lp.divLeft < 0 || lp.divRight < 0)
                throw(DivisionLayoutExecption("Value must be greater than -1."))
            if(lp.verticalOrder < LayoutParams.DEFAULT_ORDER || lp.horizontalOrder < LayoutParams.DEFAULT_ORDER)
                throw(DivisionLayoutExecption("Order must be greater than "+ LayoutParams.DEFAULT_ORDER.toString()+"."))
            if(lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                lp.verticalGroup = LayoutParams.DEFAULT_GROUP
                if(!verticalWrapMode)
                    lp.divTop = 0F; lp.divBottom = 0F
            }
            if(lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                lp.horizontalGroup = LayoutParams.DEFAULT_GROUP
                if(!horizontalWrapMode)
                    lp.divRight = 0F; lp.divLeft = 0F
            }
            makeGroup(lp.verticalGroup); makeGroup(lp.horizontalGroup)
            val vg = groupList[lp.verticalGroup]!!
            val hg = groupList[lp.horizontalGroup]!!
            if(lp.verticalGroup != LayoutParams.DEFAULT_GROUP) {
                if(lp.verticalOrder == LayoutParams.DEFAULT_ORDER)
                    vg.verticalList.add(i)
                else {
                    val vol = vg.verticalOrderList
                    if(vol[lp.verticalOrder] == null) vol[lp.verticalOrder] = ArrayList()
                    vol[lp.verticalOrder]!!.add(i)
                }
                if(!verticalWrapMode) {
                    vg.va +=
                            if (lp.height == 0)
                                lp.divTop + lp.divHeight + lp.divBottom
                            else
                                lp.divTop + lp.divBottom
                    if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        c.measure(0, getChildMeasureSpec(heightMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT))
                        vg.vv -= c.measuredHeight
                    } else if (lp.height != 0)
                        vg.vv -= lp.height
                }
            } else defaultVerticalGroup.add(i)
            if(lp.horizontalGroup != LayoutParams.DEFAULT_GROUP) {
                if(lp.horizontalOrder == LayoutParams.DEFAULT_ORDER)
                    hg.horizontalList.add(i)
                else {
                    val hol = hg.horizontalOrderList
                    if(hol[lp.horizontalOrder] == null) hol[lp.horizontalOrder] = ArrayList()
                    hol[lp.horizontalOrder]!!.add(i)
                }
                if(!horizontalWrapMode) {
                    hg.ha +=
                            if (lp.width == 0)
                                lp.divLeft + lp.divWidth + lp.divRight
                            else
                                lp.divLeft + lp.divRight
                    if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        c.measure(getChildMeasureSpec(widthMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT), 0)
                        hg.hv -= c.measuredWidth
                    } else if (lp.width != 0)
                        hg.hv -= lp.width
                }
            } else defaultHorizontalGroup.add(i)
        }

        for (g in groupList.values) {
            for(l in g.verticalOrderList) {
                if(l.key-1 > g.verticalList.size)
                    throw(DivisionLayoutExecption("Order can not be greater than the number of views in group. Also, please arrange the order starting from 1"))
                l.value.forEach {
                    g.verticalList.add(l.key -1,it)
                }
            }
            for(l in g.horizontalOrderList) {
                if(l.key-1 > g.horizontalList.size)
                    throw(DivisionLayoutExecption("Order can not be greater than the number of views in group. Also, please arrange the order starting from 1"))
                l.value.forEach {
                    g.horizontalList.add(l.key-1,it)
                }
            }
            g.verticalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mhs = when (lp.height) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(heightMeasureSpec,0,measuredHeight)
                    0 -> getChildMeasureSpec(heightMeasureSpec, 0, f(g.vv, lp.divHeight, g.va))
                    else -> getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
                }
                if (lp.mc) {
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                    lp.mc = false
                } else
                    lp.mc = true
            }
            g.horizontalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                lp.mws = when (lp.width) {
                    ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(widthMeasureSpec,0,measuredWidth)
                    0 -> getChildMeasureSpec(widthMeasureSpec, 0, f(g.hv, lp.divWidth, g.ha))
                    else -> getChildMeasureSpec(widthMeasureSpec, 0, lp.width)
                }
                if (lp.mc) {
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
        }
        defaultVerticalGroup.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mhs = when (lp.height) {
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(heightMeasureSpec,0,measuredHeight)
                0 -> getChildMeasureSpec(heightMeasureSpec, 0, f(measuredHeight, lp.divHeight, lp.divTop + lp.divHeight + lp.divBottom))
                else -> getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
            }
            if (lp.mc) {
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
                lp.mc = false
            } else
                lp.mc = true
        }
        defaultHorizontalGroup.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mws = when (lp.width) {
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(widthMeasureSpec,0,measuredWidth)
                0 -> getChildMeasureSpec(widthMeasureSpec, 0, f(measuredWidth, lp.divWidth, lp.divLeft + lp.divWidth + lp.divRight))
                else -> getChildMeasureSpec(widthMeasureSpec, 0, lp.width)
            }
            if (lp.mc) {
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
                lp.mc = false
            }
        }
    }

    private fun GroupJsonSet() {
        val lateVerticalList = ArrayList<JSONObject>()
        val lateHorizontalList = ArrayList<JSONObject>()
        for(i in 0 until groupJson.length()) {
            try {
                val g = groupJson.getJSONObject(i)
                val n = g.getString("name")
                makeGroup(n)
                val gr = groupList[n]!!
                if(!g.isNull("height")) gr.h = g.getDouble("height").toFloat()
                else gr.h = 0F
                if(!g.isNull("width")) gr.w = g.getDouble("width").toFloat()
                else gr.w = 0F
                try {
                    if(!g.isNull("top")) gr.t = g.getDouble("top").toFloat()
                    if(!g.isNull("bottom")) gr.b = g.getDouble("bottom").toFloat()
                    gr.vf = f(measuredHeight, gr.t, gr.t + gr.h + gr.b)
                    gr.vl = f(measuredHeight, gr.b, gr.t + gr.h + gr.b)
                    gr.vv = gr.vf - gr.vl
                } catch (e : JSONException) {
                    Log.d(TAG,e.message)
                    lateVerticalList.add(g)
                }
                try {
                    if(!g.isNull("left")) gr.l = g.getDouble("left").toFloat()
                    if(!g.isNull("right")) gr.r = g.getDouble("right").toFloat()
                    gr.hf = f(measuredWidth, gr.l, gr.l + gr.w + gr.r)
                    gr.hl = f(measuredWidth, gr.r, gr.l + gr.w + gr.r)
                    gr.hv = gr.hf - gr.hl
                } catch (e : JSONException) {
                    Log.d(TAG,e.message)
                    lateHorizontalList.add(g)
                }
            } catch (e : JSONException) {
                throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.",e))
            }
        }
        lateVerticalList.forEach { lv ->
            val g = groupList[lv.getString("name")]!!
            groupList.forEach { gl ->
                if(lv.getString("top").split(".")[0] == gl.key) {
                    g.vf = gl.value.vf
                }
                if(lv.getString("bottom").split(".")[0] == gl.key) {
                    g.vl = gl.value.vl
                }
            }
            if(g.vf > 0 && g.vl > 0) g.vv = g.vf - g.vl
            else if(g.vf > 0) {
                g.vv = f(measuredHeight - g.vf, g.h, g.h + g.b)
                gr.b = g.getDouble("bottom").toFloat()
            } else if(g.vl > 0) g.vv = f(measuredHeight-g.vl,g.h,g.t + g.h)
        }
    }

    private fun makeGroup(name : String) { if(!groupList.containsKey(name)) groupList[name] = G() }


    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v == 0F || m == 0F) 0
        else (p/m*v).toInt()
    }


    private fun attrGroupJsonObject(string : String) : JSONObject {
        var st = string
        val result : JSONObject
        st = st.trim()
        if(st.first() != '{') st = "{".plus(st)
        if(st.last() != '}') st += "}"
        try {
            result = JSONObject(st)
            if(!result.isNull("l") && result.isNull("left")) result.put("left",result.get("l"))
            if(!result.isNull("t") && result.isNull("top")) result.put("top",result.get("t"))
            if(!result.isNull("r") && result.isNull("right")) result.put("rignt",result.get("r"))
            if(!result.isNull("b") && result.isNull("bottom")) result.put("bottom",result.get("b"))
            return result
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.", e))
        }
    }

    private fun attrGroupJsonArray(string : String) : JSONArray {
        var st = string
        val result : JSONArray
        st = st.trim()
        if(st.first() != '[') st = "[".plus(st)
        if(st.last() != ']') st += "]"
        try {
            result = JSONArray(st)
            for(i in 0 until result.length()) {
                val jo = result.getJSONObject(i)
                if(!jo.isNull("l") && jo.isNull("left")) jo.put("left",jo.get("l"))
                if(!jo.isNull("t") && jo.isNull("top")) jo.put("top",jo.get("t"))
                if(!jo.isNull("r") && jo.isNull("right")) jo.put("rignt",jo.get("r"))
                if(!jo.isNull("b") && jo.isNull("bottom")) jo.put("bottom",jo.get("b"))
            }
            return result
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption("Illegal format of group-Json, please check division_create_groups.", e))
        }
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

        var verticalGroup = DEFAULT_GROUP
        var horizontalGroup = DEFAULT_GROUP
        var divWidth = DEFAULT_VALUE
        var divHeight = DEFAULT_VALUE
        var divTop = DEFAULT_VALUE
        var divBottom = DEFAULT_VALUE
        var divLeft = DEFAULT_VALUE
        var divRight = DEFAULT_VALUE
        var verticalOrder = DEFAULT_ORDER
        var horizontalOrder = DEFAULT_ORDER

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

            ta.getString(R.styleable.DivisionLayout_Layout_division_vertical_group)?.let { verticalGroup = it }
            ta.getString(R.styleable.DivisionLayout_Layout_division_horizontal_group)?.let { horizontalGroup = it }
            divWidth = ta.getFloat(R.styleable.DivisionLayout_Layout_division_width,DEFAULT_VALUE)
            divHeight = ta.getFloat(R.styleable.DivisionLayout_Layout_division_height,DEFAULT_VALUE)
            divTop = ta.getFloat(R.styleable.DivisionLayout_Layout_division_top,DEFAULT_VALUE)
            divBottom = ta.getFloat(R.styleable.DivisionLayout_Layout_division_bottom,DEFAULT_VALUE)
            divLeft = ta.getFloat(R.styleable.DivisionLayout_Layout_division_left,DEFAULT_VALUE)
            divRight = ta.getFloat(R.styleable.DivisionLayout_Layout_division_right,DEFAULT_VALUE)
            verticalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_division_vertical_order,DEFAULT_ORDER)
            horizontalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_division_horizontal_order,DEFAULT_ORDER)

            ta.recycle()
        }
    }

    private inner class G {
        var va = 0F
        var vf = 0
        var vv = 0
        var vl = 0
        var t : Any = 0F
        var b : Any = 0F
        var h : Any = 1F
        val verticalList = ArrayList<Int>()
        val verticalOrderList = HashMap<Int,ArrayList<Int>>()
        var ha = 0F
        var hf = 0
        var hv = 0
        var hl = 0
        var l : Any = 0F
        var r : Any = 0F
        var w : Any = 1F
        val horizontalList = ArrayList<Int>()
        val horizontalOrderList = HashMap<Int,ArrayList<Int>>()

        init {
            vf = f(measuredHeight, t as Float, t as Float + h as Float + b as Float)
            vl = f(measuredHeight, b as Float, t as Float + h as Float + b as Float)
            vv = vf - vl
            hf = f(measuredWidth, l as Float, l as Float + w as Float + r as Float)
            hl = f(measuredWidth, r as Float, l as Float + w as Float + r as Float)
            hv = hf - hl
        }

        fun reset() {
            va = 0F
            vf = 0
            vv = 0
            t = 0F
            b = 0F
            h = 1F
            verticalList.clear()
            verticalOrderList.values.forEach { it.clear() }
            ha = 0F
            hf = 0
            hv = 0
            l = 0F
            r = 0F
            w = 1F
            horizontalList.clear()
            horizontalOrderList.values.forEach { it.clear() }
        }
    }

}
