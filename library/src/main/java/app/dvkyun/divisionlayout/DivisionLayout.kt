package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
        private const val FIELD = "DivisionLayout_division_create_group_"
    }

    private val groupList : HashMap<String,G> = HashMap()
    private lateinit var groupJson : JSONArray

    private val lateVerticalGroupJson : ArrayList<String> = ArrayList()
    private val lateHorizontalGroupJson : ArrayList<String> = ArrayList()
    private val recycleHashSet : HashSet<String> = HashSet()

    private var divisionGroups : HashMap<String,DivisionGroup> = HashMap()

    private val defaultVerticalGroup : ArrayList<Int> = ArrayList()
    private val defaultHorizontalGroup : ArrayList<Int> = ArrayList()

    private var verticalWrapMode = false
    private var horizontalWrapMode = false

    private var isAttach = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        groupList[""] = G()
        context?.let { c ->
            attrs?.let { a ->
                val r = R.styleable::class.java
                val ta = c.theme.obtainStyledAttributes(a,R.styleable.DivisionLayout,0,0)
                ta.getString(R.styleable.DivisionLayout_division_create_groups)?.let {
                    try {
                        groupJson = attrGroupJsonArray(it)
                    } catch (e: JSONException) {
                        throw(DivisionLayoutExecption(DivisionLayoutExecption.E1, e))
                    }
                }.let {
                    groupJson = JSONArray()
                }
                for(i in 1 .. 20) {
                    ta.getString(r.getField(FIELD.plus(i)).getInt(R.styleable::class.java))?.let {
                        try {
                            groupJson.put(attrGroupJsonObject(it))
                        } catch (e : JSONException) {
                            throw(DivisionLayoutExecption(DivisionLayoutExecption.E1,e))
                        }
                    }
                }
                ta.recycle()
            }
        }
    }

    /*
    *
    * public functions
    *
    * */

    /*
    *
    * return custom group by name.
    * this function can return null.
    *
    * */
    fun getGroup(name : String) : DivisionGroup? {
        if(name == LayoutParams.DEFAULT_GROUP) return null
        else {
            groupList[name]?.let { g->
                val dg = DivisionGroup(name)
                dg.top = g.t
                dg.height = g.h
                dg.bottom = g.b
                dg.left = g.l
                dg.width = g.w
                dg.right = g.r
                divisionGroups[name] = dg
                return dg
            }.let {
                return null
            }
        }
    }

    /*
    *
    * return custom grouplist as Collection<DivisionGroup>
    *
    * */
    fun getGroups() : Collection<DivisionGroup> {
        val ga = HashSet<DivisionGroup>()
        for(g in groupList) {
            if(g.key != LayoutParams.DEFAULT_GROUP) {
                val dg = DivisionGroup(g.key)
                dg.top = g.value.t
                dg.height = g.value.h
                dg.bottom = g.value.b
                dg.left = g.value.l
                dg.width = g.value.w
                dg.right = g.value.r
                ga.add(dg)
                divisionGroups[g.key] = dg
            }
        }
        return ga
    }

    /*
    *
    * set custom groups by ArrayList<DivisionGroup>
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun setGroups(list: Iterable<DivisionGroup>) {
        try {
            val gl = JSONArray()
            for(a in list) {
                if(a.name != LayoutParams.DEFAULT_GROUP) {
                    val go = JSONObject()
                    go.put("name", a.name)
                    go.put("top", a.top)
                    go.put("height", a.height)
                    go.put("bottom", a.bottom)
                    go.put("left", a.left)
                    go.put("width", a.width)
                    go.put("right", a.right)
                    gl.put(go)
                }
            }
            groupJson = gl
            divisionGroups.clear()
            requestLayout()
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2,e))
        }
    }

    /*
    *
    * reset or add a single group
    * When this function is successfully called, it redraws the layout
    *
    * */
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
            divisionGroups.remove(divisionGroup.name)
            requestLayout()
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2,e))
        }
    }


    /*
    *
    * Automatically sets the groups invoked by the get command.
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun notifyGroupChanged() {
        try {
            val gl = JSONArray()
            for(a in divisionGroups.values) {
                val go = JSONObject()
                go.put("name", a.name)
                go.put("top", a.top)
                go.put("height", a.height)
                go.put("bottom", a.bottom)
                go.put("left", a.left)
                go.put("width", a.width)
                go.put("right", a.right)
                gl.put(go)
            }
            groupJson = gl
            divisionGroups.clear()
            requestLayout()
        } catch (e : JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2,e))
        }
    }

    /*
    *
    * private methods
    *
    * */

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

        groupJsonSet()

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
                    throw(DivisionLayoutExecption(DivisionLayoutExecption.E4))
                l.value.forEach {
                    g.verticalList.add(l.key -1,it)
                }
            }
            for(l in g.horizontalOrderList) {
                if(l.key-1 > g.horizontalList.size)
                    throw(DivisionLayoutExecption(DivisionLayoutExecption.E4))
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

    private fun groupJsonSet() {
        lateVerticalGroupJson.clear()
        lateHorizontalGroupJson.clear()
        recycleHashSet.clear()

        for(i in 0 until groupJson.length()) {
            try {
                val g = groupJson.getJSONObject(i)
                val n = g.getString("name")
                if(recycleHashSet.contains(n))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E15))
                recycleHashSet.add(n)
                makeGroup(n)
                val gr = groupList[n]!!
                gr.onJson()
                if(!g.isNull("height")) gr.h = g.get("height")
                else if(!g.isNull("top") || !g.isNull("bottom"))
                    gr.h = 0F
                if(!g.isNull("width")) gr.w = g.get("width")
                else if(!g.isNull("left") || !g.isNull("right"))
                    gr.w = 0F
                if(!g.isNull("top")) gr.t = g.get("top")
                if(!g.isNull("bottom")) gr.b = g.get("bottom")
                if(!g.isNull("left")) gr.l = g.get("left")
                if(!g.isNull("right")) gr.r = g.get("right")

                if(gr.h is String)
                    gr.vu = stringToPx(gr.h as String)
                else if (gr.h !is Number)
                    throw DivisionLayoutExecption(DivisionLayoutExecption.E12)
                if(gr.w is String)
                    gr.hu = stringToPx(gr.w as String)
                else if (gr.w !is Number)
                    throw DivisionLayoutExecption(DivisionLayoutExecption.E12)

                if(gr.t is String || gr.b is String) {
                    var catch = false
                    if(gr.t is String) {
                        try {
                            gr.vf = stringToPx(gr.t as String)
                            if(gr.b is Number) {
                                if(gr.vu == -1) {
                                    gr.vv = f(measuredHeight - gr.vf, anyToFloat(gr.h), anyToFloat(gr.h) + anyToFloat(gr.b))
                                    gr.vl = gr.vf + gr.vv
                                } else {
                                    gr.vv = gr.vu
                                    gr.vl = gr.vv + gr.vf
                                }
                            }
                        } catch (e : DivisionLayoutExecption) {
                            catch = true
                        }
                    }
                    if(gr.b is String) {
                        try {
                            val l = stringToPx(gr.b as String)
                            gr.vl = measuredHeight - l
                            if(gr.vf != -1) {
                                if(gr.vu == -1) {
                                    gr.vv = gr.vl - gr.vf
                                } else {
                                    gr.vv = gr.vu
                                    val a = (measuredHeight - gr.vv - gr.vf - l)/2
                                    gr.vf += a
                                    gr.vl -= a
                                }
                            } else if(gr.t is Number) {
                                if(gr.vu == -1) {
                                    gr.vv = f(gr.vl, anyToFloat(gr.h), anyToFloat(gr.h) + anyToFloat(gr.t))
                                    gr.vf = gr.vl - gr.vv
                                } else {
                                    gr.vv = gr.vu
                                    gr.vf = gr.vl - gr.vv
                                }
                            }
                        } catch (e : DivisionLayoutExecption) {
                            catch = true
                        }
                    }
                    if(catch) lateVerticalGroupJson.add(n)
                }
                else if(gr.t is Number && gr.b is Number) {
                    if(gr.vu == -1) {
                        gr.vf = f(measuredHeight, anyToFloat(gr.t), anyToFloat(gr.t) + anyToFloat(gr.h) + anyToFloat(gr.b))
                        gr.vl = measuredHeight - f(measuredHeight, anyToFloat(gr.b), anyToFloat(gr.t) + anyToFloat(gr.h) + anyToFloat(gr.b))
                        gr.vv = gr.vl - gr.vf
                    } else {
                        gr.vv = gr.vu
                        gr.vf = f(measuredHeight - gr.vv, anyToFloat(gr.t), anyToFloat(gr.t) + anyToFloat(gr.b))
                        gr.vl = gr.vv + gr.vf
                    }
                } else
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E5))

                if(gr.l is String || gr.r is String) {
                    var catch = false
                    if(gr.l is String) {
                        try {
                            gr.hf = stringToPx(gr.l as String)
                            if(gr.r is Number) {
                                if(gr.hu == -1) {
                                    gr.hv = f(measuredWidth - gr.hf, anyToFloat(gr.w), anyToFloat(gr.w) + anyToFloat(gr.r))
                                    gr.hl = gr.hf + gr.hv
                                } else {
                                    gr.hv = gr.hu
                                    gr.hl = gr.hv + gr.hf
                                }
                            }
                        } catch (e : DivisionLayoutExecption) {
                            catch = true
                        }
                    }
                    if(gr.r is String) {
                        try {
                            val l = stringToPx(gr.r as String)
                            gr.hl = measuredWidth - l
                            if(gr.hf != -1) {
                                if(gr.hu == -1) {
                                    gr.hv = gr.hl - gr.hf
                                } else {
                                    gr.hv = gr.hu
                                    val a = (measuredWidth - gr.hv - gr.hf - l)/2
                                    gr.hf += a
                                    gr.hl -= a
                                }
                            } else if(gr.l is Number) {
                                if(gr.hu == -1) {
                                    gr.hv = f(gr.hl, anyToFloat(gr.h), anyToFloat(gr.w) + anyToFloat(gr.l))
                                    gr.hf = gr.hl - gr.hv
                                } else {
                                    gr.hv = gr.vu
                                    gr.hf = gr.hv - gr.hl
                                }
                            }
                        } catch (e : DivisionLayoutExecption) {
                            catch = true
                        }
                    }
                    if(catch) lateHorizontalGroupJson.add(n)
                }
                else if(gr.l is Number && gr.r is Number) {
                    if(gr.hu == -1) {
                        gr.hf = f(measuredWidth, anyToFloat(gr.l), anyToFloat(gr.l) + anyToFloat(gr.w) + anyToFloat(gr.r))
                        gr.hl = measuredWidth - f(measuredWidth, anyToFloat(gr.r), anyToFloat(gr.l) + anyToFloat(gr.w) + anyToFloat(gr.r))
                        gr.hv = gr.hl - gr.hf
                    } else {
                        gr.hv = gr.hu
                        gr.hf = f(measuredWidth - gr.hv, anyToFloat(gr.l), anyToFloat(gr.l) + anyToFloat(gr.r))
                        gr.hl = gr.hv + gr.hf
                    }
                } else
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E5))
            } catch (e : JSONException) {
                throw(DivisionLayoutExecption(DivisionLayoutExecption.E1,e))
            }
        }

        recycleHashSet.clear()

        while (lateVerticalGroupJson.isNotEmpty())
            setGroupVertical(lateVerticalGroupJson[0])
        while (lateHorizontalGroupJson.isNotEmpty())
            setGroupHorizontal(lateHorizontalGroupJson[0])
    }

    private fun makeGroup(name : String) {
        if(!groupList.containsKey(name)) groupList[name] = G()
    }

    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v == 0F || m == 0F) 0
        else (p/m*v).toInt()
    }

    private fun attrGroupJsonObject(string : String) : JSONObject {
        var st = string
        val result : JSONObject
        st = st.trim()
        st = st.toLowerCase()
        if(st.first() != '{') st = "{".plus(st)
        if(st.last() != '}') st += "}"
        try {
            result = JSONObject(st)
            if(result.isNull("name") && result.isNull("n") && result.getString("name") == "")
                throw DivisionLayoutExecption(DivisionLayoutExecption.E14)
            if(!result.isNull("n") && result.isNull("name")) result.put("name",result.get("n"))
            if(!result.isNull("h") && result.isNull("height")) result.put("height",result.get("h"))
            if(!result.isNull("w") && result.isNull("width")) result.put("width",result.get("w"))
            if(!result.isNull("l") && result.isNull("left")) result.put("left",result.get("l"))
            if(!result.isNull("t") && result.isNull("top")) result.put("top",result.get("t"))
            if(!result.isNull("r") && result.isNull("right")) result.put("right",result.get("r"))
            if(!result.isNull("b") && result.isNull("bottom")) result.put("bottom",result.get("b"))
            return result
        } catch (e: JSONException) {
            throw DivisionLayoutExecption(DivisionLayoutExecption.E1, e)
        }
    }

    private fun attrGroupJsonArray(string : String) : JSONArray {
        var st = string
        val result : JSONArray
        st = st.trim()
        st = st.toLowerCase()
        if(st.first() != '[') st = "[".plus(st)
        if(st.last() != ']') st += "]"
        try {
            result = JSONArray(st)
            for(i in 0 until result.length()) {
                val jo = result.getJSONObject(i)
                if(jo.isNull("name") && jo.isNull("n") && jo.getString("name") == "")
                    throw(DivisionLayoutExecption(DivisionLayoutExecption.E14))
                if(!jo.isNull("n") && jo.isNull("name")) jo.put("name",jo.get("n"))
                if(!jo.isNull("h") && jo.isNull("height")) jo.put("height",jo.get("h"))
                if(!jo.isNull("w") && jo.isNull("width")) jo.put("width",jo.get("w"))
                if(!jo.isNull("l") && jo.isNull("left")) jo.put("left",jo.get("l"))
                if(!jo.isNull("t") && jo.isNull("top")) jo.put("top",jo.get("t"))
                if(!jo.isNull("r") && jo.isNull("right")) jo.put("right",jo.get("r"))
                if(!jo.isNull("b") && jo.isNull("bottom")) jo.put("bottom",jo.get("b"))
            }
            return result
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E1, e))
        }
    }

    private fun stringToPx(string : String) : Int {
        var s = string
        s = s.toLowerCase()
        s = s.trim()
        when(s.substring(s.length - 2)) {
            "dp" -> {
                s = s.substring(0, s.length - 2)
                s.toIntOrNull().let {
                    if (it == null)
                        throw (DivisionLayoutExecption(DivisionLayoutExecption.E11))
                    return (it * resources.displayMetrics.density).toInt()
                }
            }
            "px" -> {
                s = s.substring(0, s.length - 2)
                s.toIntOrNull().let {
                    if (it == null)
                        throw (DivisionLayoutExecption(DivisionLayoutExecption.E11))
                    return it
                }
            }
            else -> throw (DivisionLayoutExecption(DivisionLayoutExecption.E11))
        }
    }
    
    private fun anyToFloat(a : Any) : Float {
        return (a as Number).toFloat()
    }

    private fun setGroupVertical(name : String) : G {
        val g = groupList[name]!!
        if(g.t is String && g.vf == -1)
            g.vf = findVerticalValue(name, g.t as String)
        if(g.b is String && g.vl == -1)
            g.vl = findVerticalValue(name, g.b as String)
        if(g.vu == -1) {
            if (g.t is String && g.b is String) g.vv = g.vl - g.vf
            else if (g.t is String && g.b is Number) {
                g.vv = f(measuredHeight - g.vf, anyToFloat(g.h), anyToFloat(g.h) + anyToFloat(g.b))
                g.vl =  g.vv + g.vf
            } else if (g.b is String && g.t is Number) {
                g.vv = f(g.vl, anyToFloat(g.h), anyToFloat(g.h) + anyToFloat(g.t))
                g.vf = g.vl - g.vv
            }
        } else {
            g.vv = g.vu
            if (g.t is String && g.b is String) {
                val a : Int = (measuredHeight - g.vv - g.vf - (measuredHeight - g.vl))/2
                g.vf += a
                g.vl -= a
            }
            else if (g.t is String && g.b is Number)
                g.vl = g.vv + g.vf
            else if (g.b is String && g.t is Number)
                g.vf = g.vl - g.vv
        }
        lateVerticalGroupJson.remove(name)
        return g
    }

    private fun setGroupHorizontal(name : String) : G {
        val g = groupList[name]!!
        if(g.l is String && g.hf == -1)
            g.hf = findHorizontalValue(name, g.l as String)
        if(g.r is String && g.hl == -1)
            g.hl = findHorizontalValue(name, g.r as String)
        if(g.hu == -1) {
            if (g.l is String && g.r is String) g.hv = g.hl - g.hf
            else if (g.l is String && g.r is Number) {
                g.hv = f(measuredWidth - g.hf, anyToFloat(g.w), anyToFloat(g.w) + anyToFloat(g.r))
                g.hl = g.hv + g.hf
            } else if (g.r is String && g.l is Number) {
                g.hv = f(g.hl, anyToFloat(g.w), anyToFloat(g.w) + anyToFloat(g.l))
                g.hf = g.hl - g.hv
            }
        } else {
            g.hv = g.hu
            if (g.l is String && g.r is String) {
                val a : Int = (measuredWidth - g.hv - g.hf - (measuredWidth - g.hl))/2
                g.hf += a
                g.hl -= a
            }
            else if (g.l is String && g.r is Number)
                g.hl = g.hv + g.hf
            else if (g.r is String && g.l is Number)
                g.hf = g.hl - g.hv
        }
        lateHorizontalGroupJson.remove(name)
        return g
    }

    private fun findVerticalValue(to : String, des : String) : Int {
        val t = des.split(".")
        if(t[0] == to)
            throw ((DivisionLayoutExecption(DivisionLayoutExecption.E8)))
        if(t.size != 2)
            throw (DivisionLayoutExecption(DivisionLayoutExecption.E13))
        val gl = groupList[t[0]]
        if(gl == null)
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E10))
        else if(t[1] == "t" || t[1] == "top") {
            if(gl.vf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.t is String)
                    return findVerticalValue(t[0], gl.t as String)
                 else
                    return  setGroupVertical(t[0]).vf

            } else {
                recycleHashSet.clear()
                return gl.vf
            }
        }
        else if(t[1] == "b" || t[1] == "bottom") {
            if(gl.vl == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.b is String)
                    return findVerticalValue(t[0], gl.b as String)
                else
                    return setGroupVertical(t[0]).vl
            } else {
                recycleHashSet.clear()
                return gl.vl
            }
        }
        else
            throw (DivisionLayoutExecption(DivisionLayoutExecption.E6))
    }

    private fun findHorizontalValue(to : String, des : String) : Int {
        val t = des.split(".")
        if(t[0] == to)
            throw ((DivisionLayoutExecption(DivisionLayoutExecption.E8)))
        if(t.size != 2)
            throw (DivisionLayoutExecption(DivisionLayoutExecption.E13))
        val gl = groupList[t[0]]
        if(gl == null)
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E10))
        else if(t[1] == "l" || t[1] == "left") {
            if(gl.hf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.l is String)
                    return findHorizontalValue(t[0], gl.l as String)
                else
                    return setGroupHorizontal(t[0]).hf
            } else {
                recycleHashSet.clear()
                return gl.hf
            }
        }
        else if(t[1] == "r" || t[1] == "right") {
            if(gl.hl == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.r is String)
                    return findHorizontalValue(t[0], gl.r as String)
                 else
                    return setGroupHorizontal(t[0]).hl
            } else {
                recycleHashSet.clear()
                return gl.hl
            }
        }
        else
            throw (DivisionLayoutExecption(DivisionLayoutExecption.E7))
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
            const val DEFAULT_GROUP = ""
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
        var vu = -1
        var t : Any = 0F
        var b : Any = 0F
        var h : Any = 1F
        val verticalList = ArrayList<Int>()
        val verticalOrderList = HashMap<Int,ArrayList<Int>>()
        var ha = 0F
        var hf = 0
        var hv = 0
        var hl = 0
        var hu = -1
        var l : Any = 0F
        var r : Any = 0F
        var w : Any = 1F
        val horizontalList = ArrayList<Int>()
        val horizontalOrderList = HashMap<Int,ArrayList<Int>>()

        init {
            vv = measuredHeight
            hv = measuredWidth
        }

        fun reset() {
            va = 0F
            vf = 0
            vv = measuredHeight
            vu = -1
            t = 0F
            b = 0F
            h = 1F
            verticalList.clear()
            verticalOrderList.values.forEach { it.clear() }
            ha = 0F
            hf = 0
            hv = measuredWidth
            hu = -1
            l = 0F
            r = 0F
            w = 1F
            horizontalList.clear()
            horizontalOrderList.values.forEach { it.clear() }
        }

        fun onJson() {
            vf = -1
            vv = -1
            vl = -1
            vu = -1
            hf = -1
            hv = -1
            hl = -1
            hu = -1
        }
    }

}
