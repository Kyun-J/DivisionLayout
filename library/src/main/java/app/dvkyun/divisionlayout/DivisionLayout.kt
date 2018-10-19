package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DivisionLayout : ViewGroup {

    companion object {
        private const val TAG = "DivisionLayout"
        private const val FIELD = "DivisionLayout_create_division_"
    }

    private val divisionList: HashMap<String, D> = HashMap()
    private lateinit var divisionJson: JSONArray

    private val lateVerticalDivisionJson: ArrayList<String> = ArrayList()
    private val lateHorizontalDivisionJson: ArrayList<String> = ArrayList()
    private val recycleHashSet: HashSet<String> = HashSet()

    private var calledDivision: HashMap<String, Division> = HashMap()

    private val defaultVerticalDivision: ArrayList<Int> = ArrayList()
    private val defaultHorizontalDivision: ArrayList<Int> = ArrayList()

    private var verticalWrapMode = false
    private var horizontalWrapMode = false
    private var verticalMatchMode = false
    private var horizontalMatchMode = false

    private var isAttach = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        divisionList[""] = D()
        context?.let { c ->
            attrs?.let { a ->
                val r = R.styleable::class.java
                val ta = c.theme.obtainStyledAttributes(a, R.styleable.DivisionLayout, 0, 0)
                ta.getString(R.styleable.DivisionLayout_create_divisions)?.let {
                    try {
                        divisionJson = attrDivisionJsonArray(it)
                    } catch (e: JSONException) {
                        throw(DivisionLayoutExecption(DivisionLayoutExecption.E1, e))
                    }
                }.let {
                    if (it == null) divisionJson = JSONArray()
                }
                for (i in 1..20) {
                    ta.getString(r.getField(FIELD.plus(i)).getInt(R.styleable::class.java))?.let {
                        try {
                            divisionJson.put(attrDivisionJsonObject(it))
                        } catch (e: JSONException) {
                            throw(DivisionLayoutExecption(DivisionLayoutExecption.E1, e))
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
    * return division by name.
    * this function can return null.
    *
    * */
    fun getDivision(name: String): Division? {
        if (name == LayoutParams.DEFAULT_DIVISION) return null
        else {
            divisionList[name]?.let { g ->
                val dg = Division(name)
                dg.top = g.t
                dg.height = g.h
                dg.bottom = g.b
                dg.left = g.l
                dg.width = g.w
                dg.right = g.r
                calledDivision[name] = dg
                return dg
            }.let {
                return null
            }
        }
    }

    /*
    *
    * return all divisions as Collection<Division>
    *
    * */
    fun getAllDivision(): Collection<Division> {
        val ga = HashSet<Division>()
        for (g in divisionList) {
            if (g.key != LayoutParams.DEFAULT_DIVISION) {
                val dg = Division(g.key)
                dg.top = g.value.t
                dg.height = g.value.h
                dg.bottom = g.value.b
                dg.left = g.value.l
                dg.width = g.value.w
                dg.right = g.value.r
                ga.add(dg)
                calledDivision[g.key] = dg
            }
        }
        return ga
    }

    /*
    *
    * reset all divisions by Iterable<Division>
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun setAllDivision(list: Iterable<Division>) {
        try {
            val gl = JSONArray()
            for (a in list) {
                if (a.name != LayoutParams.DEFAULT_DIVISION) {
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
            divisionJson = gl
            calledDivision.clear()
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2, e))
        }
    }

    /*
    *
    * reset or add a single division
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun setDivision(division: Division) {
        try {
            var f = -1
            for (i in 0 until divisionJson.length()) {
                if (division.name == divisionJson.getJSONObject(i).getString("name")) {
                    f = i
                    break
                }
            }
            val go = JSONObject()
            go.put("name", division.name)
            go.put("top", division.top)
            go.put("height", division.height)
            go.put("bottom", division.bottom)
            go.put("left", division.left)
            go.put("width", division.width)
            go.put("right", division.right)
            if (f != -1) divisionJson.put(f, go)
            else divisionJson.put(go)
            calledDivision.remove(division.name)
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2, e))
        }
    }


    /*
    *
    * Automatically set divisions invoked by the get command.
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun notifyDivisionChanged() {
        try {
            val gl = JSONArray()
            for (a in calledDivision.values) {
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
            divisionJson = gl
            calledDivision.clear()
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E2, e))
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
        if(isAttach) {
            for (g in divisionList.values) g.reset()
            defaultVerticalDivision.clear()
            defaultHorizontalDivision.clear()
            verticalWrapMode = layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT
            horizontalWrapMode = layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
            verticalMatchMode = layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
            horizontalMatchMode = layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT
            var mw = if (!horizontalWrapMode) MeasureSpec.getSize(widthMeasureSpec) else 0
            var mh = if (!verticalWrapMode) MeasureSpec.getSize(heightMeasureSpec) else 0
            recycleHashSet.clear()
            for (i in 0 until divisionJson.length()) {
                try {
                    val g = divisionJson.getJSONObject(i)
                    val n = g.getString("name")
                    if (recycleHashSet.contains(n))
                        throw (DivisionLayoutExecption(DivisionLayoutExecption.E15))
                    recycleHashSet.add(n)
                    makeGroup(n)
                    val gr = divisionList[n]!!
                    gr.onJson()
                    if (!g.isNull("height")) gr.h = g.get("height")
                    if (!g.isNull("width")) gr.w = g.get("width")
                    if (!g.isNull("top")) gr.t = g.get("top")
                    if (!g.isNull("bottom")) gr.b = g.get("bottom")
                    if (!g.isNull("left")) gr.l = g.get("left")
                    if (!g.isNull("right")) gr.r = g.get("right")
                    if (g.isNull("height") && !((gr.t is String && g.isNull("bottom")) || (gr.b is String && g.isNull("top")) || (gr.t is String && gr.b is String)))
                        gr.h = 0F
                    if (g.isNull("width") && !((gr.l is String && g.isNull("right")) || (gr.r is String && g.isNull("left")) || (gr.l is String && gr.r is String)))
                        gr.w = 0F
                } catch (e: JSONException) {
                    throw DivisionLayoutExecption(DivisionLayoutExecption.E3)
                }
            }
            recycleHashSet.clear()
            if (verticalWrapMode || horizontalWrapMode) {
                for (i in 0 until childCount) {
                    val c = getChildAt(i)
                    val lp = c.layoutParams as LayoutParams
                    if (verticalWrapMode) {
                        divisionList[lp.verticalDivision]?.let {
                            if(it.h.toString() == Division.WRAP && (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT || lp.height > 0)) {
                                c.measure(0, getChildMeasureSpec(heightMeasureSpec,verticalPadding(c),lp.height))
                                mh += c.measuredHeight
                            }
                        }
                    }
                    if (horizontalWrapMode) {
                        divisionList[lp.horizontalDivision]?.let {
                            if(it.w.toString() == Division.WRAP && (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT || lp.width > 0)) {
                                c.measure(getChildMeasureSpec(widthMeasureSpec,horizontalPadding(c),lp.width), 0)
                                mw += c.measuredWidth
                            }
                        }
                    }
                }
                divisionList.forEach {
                    val g = it.value
                    if (verticalWrapMode) {
                        try {
                            mh += stringToPx(g.t.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.t is Number) g.t = 0F
                        }
                        try {
                            mh += stringToPx(g.h.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.h is Number) g.h = 0F
                        }
                        try {
                            mh += stringToPx(g.b.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.b is Number) g.b = 0F
                        }
                    }
                    if (horizontalWrapMode) {
                        try {
                            mw += stringToPx(g.l.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.l is Number) g.l = 0F
                        }
                        try {
                            mw += stringToPx(g.w.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.w is Number) g.w = 0F
                        }
                        try {
                            mw += stringToPx(g.r.toString())
                        } catch (e: DivisionLayoutExecption) {
                            if (g.r is Number) g.r = 0F
                        }
                    }
                }
            }
            mh += paddingTop + paddingBottom
            mw += paddingStart + paddingEnd
            setMeasuredDimension(mw, mh)
            measureWithDivision(widthMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (g in divisionList.values) {
            var lv = g.vf
            g.verticalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    lp.lt = g.vf + l
                    lp.lb = g.vl
                } else {
                    lp.lt = lv + f(g.vv, lp.divTop, g.va)
                    lp.lb = lp.lt + lp.mh
                    lv = lp.lb + f(g.vv, lp.divBottom, g.va)
                }
                if (lp.lc) {
                    c.layout(lp.ll, lp.lt, lp.lr, lp.lb)
                    lp.lc = false
                } else lp.lc = true
            }
            var lh = g.hf
            g.horizontalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    lp.ll = g.hf
                    lp.lr = g.hl
                } else {
                    lp.ll = lh + f(g.hv, lp.divLeft, g.ha)
                    lp.lr = lp.ll + lp.mw
                    lh = lp.lr + f(g.hv, lp.divRight, g.ha)
                }
                if (lp.lc) {
                    c.layout(lp.ll, lp.lt, lp.lr, lp.lb)
                    lp.lc = false
                } else lp.lc = true
            }
        }
        defaultVerticalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.divTop + lp.divBottom
            lp.lt = f(measuredHeight - lp.mh, lp.divTop, m)
            lp.lb = lp.lt + lp.mh
            if (lp.lc) {
                c.layout(lp.ll, lp.lt, lp.lr, lp.lb)
                lp.lc = false
            } else lp.lc = true
        }
        defaultHorizontalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val m = lp.divLeft + lp.divRight
            lp.ll = f(measuredWidth - lp.mw, lp.divLeft, m)
            lp.lr = lp.ll + lp.mw
            if (lp.lc) {
                c.layout(lp.ll, lp.lt, lp.lr, lp.lb)
                lp.lc = false
            }
        }
    }

    private fun measureWithDivision(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        divisionJsonSet(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            if (lp.verticalOrder > childCount || lp.horizontalOrder > childCount)
                throw DivisionLayoutExecption("Order can not be greater than the number of views.")
            if (lp.divWidth < 0 || lp.divHeight < 0 || lp.divTop < 0 || lp.divBottom < 0 || lp.divLeft < 0 || lp.divRight < 0)
                throw DivisionLayoutExecption("Value must be greater than -1.")
            if (lp.verticalOrder < LayoutParams.DEFAULT_ORDER || lp.horizontalOrder < LayoutParams.DEFAULT_ORDER)
                throw DivisionLayoutExecption("Order must be greater than " + LayoutParams.DEFAULT_ORDER.toString() + ".")
            makeGroup(lp.verticalDivision); makeGroup(lp.horizontalDivision)
            val vg = divisionList[lp.verticalDivision]!!
            val hg = divisionList[lp.horizontalDivision]!!
            if (lp.verticalDivision != LayoutParams.DEFAULT_DIVISION) {
                if (lp.verticalOrder == LayoutParams.DEFAULT_ORDER)
                    vg.verticalList.add(i)
                else {
                    val vol = vg.verticalOrderList
                    if (vol[lp.verticalOrder] == null) vol[lp.verticalOrder] = ArrayList()
                    vol[lp.verticalOrder]!!.add(i)
                }
                vg.va +=
                        if (lp.height == 0)
                            lp.divTop + lp.divHeight + lp.divBottom
                        else
                            lp.divTop + lp.divBottom
                val s = getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), lp.height)
                if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.mhs = getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), vg.vl - vg.vf)
                else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(0, s)
                    if (vg.vv > c.measuredHeight) {
                        vg.vv -= c.measuredHeight
                        lp.mhs = s
                    } else {
                        lp.mhs = getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), vg.vv)
                        vg.vv = 0
                    }
                } else if (lp.height > 0) {
                    if (vg.vv > lp.height) {
                        vg.vv -= lp.height
                        lp.mhs = s
                    } else {
                        lp.mhs = getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), vg.vv)
                        vg.vv = 0
                    }
                }
            } else defaultVerticalDivision.add(i)
            if (lp.horizontalDivision != LayoutParams.DEFAULT_DIVISION) {
                if (lp.horizontalOrder == LayoutParams.DEFAULT_ORDER)
                    hg.horizontalList.add(i)
                else {
                    val hol = hg.horizontalOrderList
                    if (hol[lp.horizontalOrder] == null) hol[lp.horizontalOrder] = ArrayList()
                    hol[lp.horizontalOrder]!!.add(i)
                }
                hg.ha +=
                        if (lp.width == 0)
                            lp.divLeft + lp.divWidth + lp.divRight
                        else
                            lp.divLeft + lp.divRight
                val s = getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), lp.width)
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.mws = getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), hg.hl - hg.hf)
                else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(s, 0)
                    if (hg.hv > c.measuredWidth) {
                        hg.hv -= c.measuredWidth
                        lp.mws = s
                    } else {
                        lp.mws = getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), hg.hv)
                        hg.hv = 0
                    }
                } else if (lp.width > 0) {
                    if (hg.hv > lp.width) {
                        hg.hv -= lp.width
                        lp.mws = s
                    } else {
                        lp.mws = getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), hg.hv)
                        hg.hv = 0
                    }
                }
            } else defaultHorizontalDivision.add(i)
        }

        for (g in divisionList.values) {
            for (l in g.verticalOrderList) {
                if (l.key - 1 > g.verticalList.size)
                    throw(DivisionLayoutExecption(DivisionLayoutExecption.E4))
                l.value.forEach {
                    g.verticalList.add(l.key - 1, it)
                }
            }
            for (l in g.horizontalOrderList) {
                if (l.key - 1 > g.horizontalList.size)
                    throw(DivisionLayoutExecption(DivisionLayoutExecption.E4))
                l.value.forEach {
                    g.horizontalList.add(l.key - 1, it)
                }
            }
            g.verticalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.height == 0)
                    lp.mhs = getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), f(g.vv, lp.divHeight, g.va))
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
                if (lp.width == 0)
                    lp.mws = getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), f(g.hv, lp.divWidth, g.ha))
                if (lp.mc) {
                    c.measure(lp.mws, lp.mhs)
                    lp.mw = c.measuredWidth
                    lp.mh = c.measuredHeight
                } else
                    lp.mc = true
            }
        }
        defaultVerticalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mhs = when (lp.height) {
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), measuredHeight)
                0 -> getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), f(measuredHeight, lp.divHeight, lp.divTop + lp.divHeight + lp.divBottom))
                else -> getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), lp.height)
            }
            if (lp.mc) {
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
                lp.mc = false
            } else
                lp.mc = true
        }
        defaultHorizontalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.mws = when (lp.width) {
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), measuredWidth)
                0 -> getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), f(measuredWidth, lp.divWidth, lp.divLeft + lp.divWidth + lp.divRight))
                else -> getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), lp.width)
            }
            if (lp.mc) {
                c.measure(lp.mws, lp.mhs)
                lp.mw = c.measuredWidth
                lp.mh = c.measuredHeight
                lp.mc = false
            }
        }
    }

    private fun divisionJsonSet(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lateVerticalDivisionJson.clear()
        lateHorizontalDivisionJson.clear()

        divisionList.forEach {
            val n = it.key
            val gr = it.value
            if (gr.h is String) {
                val s = gr.h.toString()
                gr.vu =
                        if (s == Division.WRAP)
                            verticalPxInDivisionMember(n, heightMeasureSpec)
                        else
                            stringToPx(s)
            } else if (gr.h !is Number)
                throw DivisionLayoutExecption(DivisionLayoutExecption.E12)
            if (gr.w is String) {
                val s = gr.w as String
                gr.hu =
                        if (s == Division.WRAP)
                            horizontalPxInDivisionMember(n, widthMeasureSpec)
                        else
                            stringToPx(gr.w as String)
            } else if (gr.w !is Number)
                throw DivisionLayoutExecption(DivisionLayoutExecption.E12)

            if (gr.t is String || gr.b is String) {
                var catch = false
                if (gr.t is String) {
                    try {
                        gr.vf = stringToPx(gr.t as String)
                        if (gr.b is Number) {
                            if (gr.vu == -1) {
                                gr.vv = f(measuredHeight - gr.vf, anyToFloat(gr.h), anyToFloat(gr.h) + anyToFloat(gr.b))
                                gr.vl = gr.vf + gr.vv
                            } else {
                                gr.vv = gr.vu
                                gr.vl = gr.vv + gr.vf
                            }
                        }
                    } catch (e: DivisionLayoutExecption) {
                        catch = true
                    }
                }
                if (gr.b is String) {
                    try {
                        val l = stringToPx(gr.b as String)
                        gr.vl = measuredHeight - l
                        if (gr.vf != -1) {
                            if (gr.vu == -1) {
                                gr.vv = gr.vl - gr.vf
                            } else {
                                gr.vv = gr.vu
                                val a = (measuredHeight - gr.vv - gr.vf - l) / 2
                                gr.vf += a
                                gr.vl -= a
                            }
                        } else if (gr.t is Number) {
                            if (gr.vu == -1) {
                                gr.vv = f(gr.vl, anyToFloat(gr.h), anyToFloat(gr.h) + anyToFloat(gr.t))
                                gr.vf = gr.vl - gr.vv
                            } else {
                                gr.vv = gr.vu
                                gr.vf = gr.vl - gr.vv
                            }
                        }
                    } catch (e: DivisionLayoutExecption) {
                        catch = true
                    }
                }
                if (catch) lateVerticalDivisionJson.add(n)
            } else if (gr.t is Number && gr.b is Number) {
                if (gr.vu == -1) {
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

            if (gr.l is String || gr.r is String) {
                var catch = false
                if (gr.l is String) {
                    try {
                        gr.hf = stringToPx(gr.l as String)
                        if (gr.r is Number) {
                            if (gr.hu == -1) {
                                gr.hv = f(measuredWidth - gr.hf, anyToFloat(gr.w), anyToFloat(gr.w) + anyToFloat(gr.r))
                                gr.hl = gr.hf + gr.hv
                            } else {
                                gr.hv = gr.hu
                                gr.hl = gr.hv + gr.hf
                            }
                        }
                    } catch (e: DivisionLayoutExecption) {
                        catch = true
                    }
                }
                if (gr.r is String) {
                    try {
                        val l = stringToPx(gr.r as String)
                        gr.hl = measuredWidth - l
                        if (gr.hf != -1) {
                            if (gr.hu == -1) {
                                gr.hv = gr.hl - gr.hf
                            } else {
                                gr.hv = gr.hu
                                val a = (measuredWidth - gr.hv - gr.hf - l) / 2
                                gr.hf += a
                                gr.hl -= a
                            }
                        } else if (gr.l is Number) {
                            if (gr.hu == -1) {
                                gr.hv = f(gr.hl, anyToFloat(gr.h), anyToFloat(gr.w) + anyToFloat(gr.l))
                                gr.hf = gr.hl - gr.hv
                            } else {
                                gr.hv = gr.vu
                                gr.hf = gr.hv - gr.hl
                            }
                        }
                    } catch (e: DivisionLayoutExecption) {
                        catch = true
                    }
                }
                if (catch) lateHorizontalDivisionJson.add(n)
            } else if (gr.l is Number && gr.r is Number) {
                if (gr.hu == -1) {
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
        }

        while (lateVerticalDivisionJson.isNotEmpty())
            setDivisionVertical(lateVerticalDivisionJson[0])
        while (lateHorizontalDivisionJson.isNotEmpty())
            setDivisionHorizontal(lateHorizontalDivisionJson[0])
    }

    private fun makeGroup(name : String) {
        if(!divisionList.containsKey(name)) divisionList[name] = D()
    }

    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v <= 0F || m <= 0F || p <= 0) 0
        else (p/m*v).toInt()
    }

    private fun verticalPadding(v : View) : Int {
        return v.paddingTop + v.paddingBottom
    }

    private fun horizontalPadding(v : View) : Int {
        return v.paddingStart + v.paddingEnd
    }

    private fun attrDivisionJsonObject(string : String) : JSONObject {
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

    private fun attrDivisionJsonArray(string : String) : JSONArray {
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

    private fun verticalPxInDivisionMember(name : String, heightMeasureSpec: Int) : Int {
        var result = 0
        for(i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as LayoutParams
            if(lp.verticalDivision == name) {
                lp.divTop = 0F; lp.divHeight = 0F; lp.divBottom = 0F
                if (lp.height > 0) result += lp.height
                else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(0, getChildMeasureSpec(heightMeasureSpec, verticalPadding(c), ViewGroup.LayoutParams.WRAP_CONTENT))
                    result += c.measuredHeight
                }
            }
        }
        return result
    }

    private fun horizontalPxInDivisionMember(name : String, widthMeasureSpec: Int) : Int {
        var result = 0
        for(i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as LayoutParams
            if(lp.horizontalDivision == name) {
                lp.divLeft = 0F; lp.divWidth = 0F; lp.divRight = 0F
                if (lp.width > 0) result += lp.width
                else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(getChildMeasureSpec(widthMeasureSpec, horizontalPadding(c), ViewGroup.LayoutParams.WRAP_CONTENT), 0)
                    result += c.measuredWidth
                }
            }
        }
        return result
    }

    private fun setDivisionVertical(name : String) : D {
        val g = divisionList[name]!!
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
        lateVerticalDivisionJson.remove(name)
        return g
    }

    private fun setDivisionHorizontal(name : String) : D {
        val g = divisionList[name]!!
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
        lateHorizontalDivisionJson.remove(name)
        return g
    }

    private fun findVerticalValue(to : String, des : String) : Int {
        val t = des.split(".")
        if(t[0] == to)
            throw ((DivisionLayoutExecption(DivisionLayoutExecption.E8)))
        if(t.size != 2)
            throw (DivisionLayoutExecption(DivisionLayoutExecption.E13))
        val gl = divisionList[t[0]]
        return if(gl == null || t[0] == LayoutParams.DEFAULT_DIVISION)
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E10))
        else if(t[1] == "t" || t[1] == "top") {
            if(gl.vf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.t is String)
                    findVerticalValue(t[0], gl.t as String)
                else
                    setDivisionVertical(t[0]).vf
            } else {
                recycleHashSet.clear()
                gl.vf
            }
        }
        else if(t[1] == "b" || t[1] == "bottom") {
            if(gl.vl == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.b is String)
                    findVerticalValue(t[0], gl.b as String)
                else
                    setDivisionVertical(t[0]).vl
            } else {
                recycleHashSet.clear()
                gl.vl
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
        val gl = divisionList[t[0]]
        return if(gl == null || t[0] == LayoutParams.DEFAULT_DIVISION)
            throw(DivisionLayoutExecption(DivisionLayoutExecption.E10))
        else if(t[1] == "l" || t[1] == "left") {
            if(gl.hf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.l is String)
                    findHorizontalValue(t[0], gl.l as String)
                else
                    setDivisionHorizontal(t[0]).hf
            } else {
                recycleHashSet.clear()
                gl.hf
            }
        }
        else if(t[1] == "r" || t[1] == "right") {
            if(gl.hl == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutExecption(DivisionLayoutExecption.E9))
                recycleHashSet.add(t[0])
                if(gl.r is String)
                    findHorizontalValue(t[0], gl.r as String)
                 else
                    setDivisionHorizontal(t[0]).hl
            } else {
                recycleHashSet.clear()
                gl.hl
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
            const val DEFAULT_DIVISION = ""
            const val DEFAULT_ORDER = 0
            const val DEFAULT_VALUE = 0F
        }

        var verticalDivision = DEFAULT_DIVISION
        var horizontalDivision = DEFAULT_DIVISION
        var verticalOrder = DEFAULT_ORDER
        var horizontalOrder = DEFAULT_ORDER
        var divWidth = DEFAULT_VALUE
        var divHeight = DEFAULT_VALUE
        var divTop = DEFAULT_VALUE
        var divBottom = DEFAULT_VALUE
        var divLeft = DEFAULT_VALUE
        var divRight = DEFAULT_VALUE

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

        constructor(width : Int, height : Int) : super(width,height)
        constructor(params: ViewGroup.LayoutParams) : super(params)
        constructor(context: Context?,attrs: AttributeSet?) : super(context,attrs) {
            context?.let { c -> attrs?.let { a -> setAttrs(c,a) } }
        }

        private fun setAttrs(context: Context, attrs: AttributeSet) {
            val ta = context.theme.obtainStyledAttributes(attrs,R.styleable.DivisionLayout_Layout,0,0)

            ta.getString(R.styleable.DivisionLayout_Layout_vertical_div)?.let { verticalDivision = it }
            ta.getString(R.styleable.DivisionLayout_Layout_horizontal_div)?.let { horizontalDivision = it }
            divWidth = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_width,DEFAULT_VALUE)
            divHeight = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_height,DEFAULT_VALUE)
            divTop = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_top,DEFAULT_VALUE)
            divBottom = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_bottom,DEFAULT_VALUE)
            divLeft = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_left,DEFAULT_VALUE)
            divRight = ta.getFloat(R.styleable.DivisionLayout_Layout_div_member_right,DEFAULT_VALUE)
            verticalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_vertical_div_order,DEFAULT_ORDER)
            horizontalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_vertical_div_order,DEFAULT_ORDER)

            ta.recycle()
        }
    }

    private inner class D {
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
            vl = vv
            hv = measuredWidth
            hl = hv
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
