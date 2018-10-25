package app.dvkyun.divisionlayout

import android.content.Context
import android.util.AttributeSet
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

    private var contentsHeight = 0
    private var contentsWidth = 0

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
                        throw(DivisionLayoutException(DivisionLayoutException.E1, e))
                    }
                }.let {
                    if (it == null) divisionJson = JSONArray()
                }
                for (i in 1..20) {
                    ta.getString(r.getField(FIELD.plus(i)).getInt(R.styleable::class.java))?.let {
                        try {
                            divisionJson.put(attrDivisionJsonObject(it))
                        } catch (e: JSONException) {
                            throw(DivisionLayoutException(DivisionLayoutException.E1, e))
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
    * Return a single division by name.
    * This function can return null.
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
    * Return all divisions as Collection<Division>
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
    * Add or reset a singe division in DivisionLayout
    * To apply division, you must call notifyDivisionChanged() or setting functions
    *
    * */
    fun addDivision(division: Division) {
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
            calledDivision[division.name] = division
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Add or reset divisions in DivisionLayout
    * To apply division, you must call notifyDivisionChanged() or setting functions
    *
    * */
    fun addDivisionList(list: Iterable<Division>) {
        try {
            for (a in list) {
                if (a.name != LayoutParams.DEFAULT_DIVISION) {
                    var f = -1
                    for (i in 0 until divisionJson.length()) {
                        if (a.name == divisionJson.getJSONObject(i).getString("name")) {
                            f = i
                            break
                        }
                    }
                    val go = JSONObject()
                    go.put("name", a.name)
                    go.put("top", a.top)
                    go.put("height", a.height)
                    go.put("bottom", a.bottom)
                    go.put("left", a.left)
                    go.put("width", a.width)
                    go.put("right", a.right)
                    if(f != -1) divisionJson.put(f,go)
                    else divisionJson.put(go)
                    calledDivision[a.name] = a
                }
            }
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Reset or add a single division
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
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Reset all divisions by Iterable<Division>
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
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Remove attribute in a single division
    * If division is removed, it is automatically generated again if there is a child view that uses division,
    * so this function only removes division's properties.
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun removeDivision(name: String) {
        try {
            val result = JSONArray()
            for (i in 0 until divisionJson.length()) {
                if (name != divisionJson.getJSONObject(i).getString("name")) {
                    result.put(divisionJson.get(i))
                }
            }
            divisionJson = result
            calledDivision.remove(name)
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Remove attribute in divisions
    * If division is removed, it is automatically generated again if there is a child view that uses division,
    * so this function only removes division's properties.
    * When this function is successfully called, it redraws the layout
    *
    * */
    fun removeDivisionList(list : Iterable<Division>) {
        try {
            val result = JSONArray()
            for (i in 0 until divisionJson.length()) {
                var g = false
                list.forEach {
                    if (it.name == divisionJson.getJSONObject(i).getString("name")) {
                        g = true
                        calledDivision.remove(it.name)
                        return@forEach
                    }
                }
                if(!g) result.put(divisionJson.get(i))
            }
            divisionJson = result
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
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
            requestLayout()
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
        }
    }

    /*
    *
    * Rename Division and return the changed Division.
    * Existing Division will be deleted and the changed Division will be used instead.
    *
    * */

    fun renameDivision(division: Division, name: String) : Division {
        try {
            val result = JSONArray()
            for (i in 0 until divisionJson.length()) {
                if (division.name != divisionJson.getJSONObject(i).getString("name"))
                    result.put(divisionJson.get(i))
            }
            divisionJson = result
            calledDivision.remove(division.name)
            val d = Division(name)
            d.top = division.top
            d.bottom = division.bottom
            d.height = division.height
            d.left = division.left
            d.right = division.right
            d.width = division.width
            addDivision(d)
            return d
        } catch (e: JSONException) {
            throw(DivisionLayoutException(DivisionLayoutException.E2, e))
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
        if(isAttach) {
            for (g in divisionList.values) g.reset()
            defaultVerticalDivision.clear()
            defaultHorizontalDivision.clear()
            for(i in 0 until childCount) {
                val c = getChildAt(i)
                val lp = c.layoutParams as LayoutParams
                val t = lp.divTop.toFloatOrNull()
                val b = lp.divBottom.toFloatOrNull()
                val l = lp.divLeft.toFloatOrNull()
                val r = lp.divRight.toFloatOrNull()
                if(t == null)
                    lp.topPx = stringToPx(lp.divTop)
                else
                    lp.topRatio = t
                if(b == null)
                    lp.bottomPx = stringToPx(lp.divBottom)
                else
                    lp.bottomRatio = b
                if(l == null)
                    lp.leftPx = stringToPx(lp.divLeft)
                else
                    lp.leftRatio = l
                if(r == null)
                    lp.rightPx = stringToPx(lp.divRight)
                else
                    lp.rightRatio = r
            }
            verticalWrapMode = layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT
            horizontalWrapMode = layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
            var mw = if (!horizontalWrapMode) MeasureSpec.getSize(widthMeasureSpec) else 0
            var mh = if (!verticalWrapMode) MeasureSpec.getSize(heightMeasureSpec) else 0
            recycleHashSet.clear()
            for (i in 0 until divisionJson.length()) {
                try {
                    val g = divisionJson.getJSONObject(i)
                    if(g.isNull("name") || g.getString("name") == "")
                        throw(DivisionLayoutException(DivisionLayoutException.E14))
                    val n = g.getString("name")
                    if (recycleHashSet.contains(n))
                        throw (DivisionLayoutException(DivisionLayoutException.E15))
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
                    throw DivisionLayoutException(DivisionLayoutException.E3)
                }
            }
            recycleHashSet.clear()
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
                    throw DivisionLayoutException(DivisionLayoutException.E12)
                if (gr.w is String) {
                    val s = gr.w as String
                    gr.hu =
                            if (s == Division.WRAP)
                                horizontalPxInDivisionMember(n, widthMeasureSpec)
                            else
                                stringToPx(gr.w as String)
                } else if (gr.w !is Number)
                    throw DivisionLayoutException(DivisionLayoutException.E12)
                if(verticalWrapMode) {
                    if(gr.h is String)
                        mh += gr.vu
                    else gr.h = 0F
                    if(gr.t is String) {
                        try {
                            mh += stringToPx(gr.t as String)
                        } catch (e: DivisionLayoutException) { }
                    } else if (gr.t is Number) gr.t = 0F
                    if(gr.b is String) {
                        try {
                            mh += stringToPx(gr.b as String)
                        } catch (e: DivisionLayoutException) { }
                    } else if (gr.b is Number) gr.b = 0F
                }
                if(horizontalWrapMode) {
                    if(gr.w is String)
                        mw += gr.hu
                    else gr.h = 0F
                    if(gr.l is String) {
                        try {
                            mw += stringToPx(gr.l as String)
                        } catch (e: DivisionLayoutException) { }
                    } else if (gr.l is Number) gr.l = 0F
                    if(gr.r is String) {
                        try {
                            mw += stringToPx(gr.r as String)
                        } catch (e: DivisionLayoutException) { }
                    } else if (gr.r is Number) gr.r = 0F
                }
            }
            if(verticalWrapMode) {
                mh += paddingTop + paddingBottom
                val dh = View.getDefaultSize(suggestedMinimumHeight,heightMeasureSpec)
                if(mh > dh) mh = dh
            }
            if(horizontalWrapMode) {
                mw += paddingStart + paddingEnd
                val dw = View.getDefaultSize(suggestedMinimumWidth,widthMeasureSpec)
                if(mw > dw) mw = dw
            }
            contentsHeight = mh - paddingTop - paddingBottom
            contentsWidth = mw - paddingStart - paddingEnd
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
                    lp.layoutTop = g.vf+ lp.marginTop
                    lp.layoutBottom = lp.layoutTop + c.measuredHeight
                } else if(!lp.verticalIndividual){
                    lp.layoutTop = lv + f(g.vv, lp.topRatio, g.va) + lp.topPx + lp.marginTop
                    lp.layoutBottom = lp.layoutTop + c.measuredHeight
                    lv = lp.layoutBottom + f(g.vv, lp.bottomRatio, g.va) + lp.bottomPx + lp.marginBottom
                    if(lp.layoutTop > g.vl) lp.layoutTop = g.vl; if(lp.layoutBottom > g.vl) lp.layoutBottom = g.vl
                } else {
                    lp.layoutTop = g.vf + f(g.vl - g.vf - c.measuredHeight, lp.topRatio, lp.topRatio + lp.divHeight + lp.bottomRatio) + lp.topPx + lp.marginTop
                    lp.layoutBottom = lp.layoutTop + c.measuredHeight
                    if(lp.layoutTop > g.vl) lp.layoutTop = g.vl; if(lp.layoutBottom > g.vl) lp.layoutBottom = g.vl
                }
                if (lp.layoutCheck) {
                    c.layout(lp.layoutLeft, lp.layoutTop, lp.layoutRight, lp.layoutBottom)
                    lp.layoutCheck = false
                } else lp.layoutCheck = true
            }
            var lh = g.hf
            g.horizontalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    lp.layoutLeft = g.hf + lp.marginLeft
                    lp.layoutRight = lp.layoutLeft + c.measuredWidth
                } else if(!lp.horizontalIndividual){
                    lp.layoutLeft = lh + f(g.hv, lp.leftRatio, g.ha) + lp.leftPx + lp.marginLeft
                    lp.layoutRight = lp.layoutLeft + c.measuredWidth
                    lh = lp.layoutRight + f(g.hv, lp.rightRatio, g.ha) + lp.rightPx + lp.marginRight
                    if(lp.layoutLeft > g.hl) lp.layoutLeft = g.hl; if(lp.layoutRight > g.hl) lp.layoutRight = g.hl
                } else {
                    lp.layoutLeft = g.hf + f(g.hl - g.hf - c.measuredWidth, lp.leftRatio, lp.leftRatio + lp.divWidth + lp.rightRatio) + lp.leftPx + lp.marginLeft
                    lp.layoutRight = lp.layoutLeft + c.measuredWidth
                    if(lp.layoutLeft > g.hl) lp.layoutLeft = g.hl; if(lp.layoutRight > g.hl) lp.layoutRight = g.hl
                }
                if (lp.layoutCheck) {
                    c.layout(lp.layoutLeft, lp.layoutTop, lp.layoutRight, lp.layoutBottom)
                    lp.layoutCheck = false
                } else lp.layoutCheck = true
            }
        }
        defaultVerticalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val contentsBottom = contentsHeight + paddingTop
            lp.layoutTop = f(contentsHeight - c.measuredHeight - lp.topPx - lp.bottomPx - lp.marginBottom - lp.marginTop, lp.topRatio, lp.topRatio + lp.bottomRatio) + lp.topPx + paddingTop + lp.marginTop
            lp.layoutBottom = lp.layoutTop + c.measuredHeight
            if(lp.layoutTop > contentsBottom) lp.layoutTop = contentsBottom; if(lp.layoutBottom > contentsBottom) lp.layoutBottom = contentsBottom
            if (lp.layoutCheck) {
                c.layout(lp.layoutLeft, lp.layoutTop, lp.layoutRight, lp.layoutBottom)
                lp.layoutCheck = false
            } else lp.layoutCheck = true
        }
        defaultHorizontalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            val contentsEnd = contentsWidth + paddingStart
            lp.layoutLeft = f(contentsWidth - c.measuredWidth - lp.leftPx - lp.rightPx - lp.marginLeft - lp.marginRight, lp.leftRatio, lp.leftRatio + lp.rightRatio) + lp.leftPx + paddingStart + lp.marginLeft
            lp.layoutRight = lp.layoutLeft + c.measuredWidth
            if(lp.layoutLeft > contentsEnd) lp.layoutLeft = contentsEnd; if(lp.layoutRight > contentsEnd) lp.layoutRight = contentsEnd
            if (lp.layoutCheck) {
                c.layout(lp.layoutLeft, lp.layoutTop, lp.layoutRight, lp.layoutBottom)
                lp.layoutCheck = false
            }
        }
    }

    /*
    *
    * private methods
    *
    * */

    private fun measureWithDivision(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        divisionJsonSet()
        for (i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            if (lp.verticalOrder > childCount || lp.horizontalOrder > childCount)
                throw DivisionLayoutException("Order can not be greater than the number of views.")
            if (lp.verticalOrder < LayoutParams.DEFAULT_ORDER || lp.horizontalOrder < LayoutParams.DEFAULT_ORDER)
                throw DivisionLayoutException("Order must be greater than " + LayoutParams.DEFAULT_ORDER.toString() + ".")
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
                if(!lp.verticalIndividual && lp.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    vg.va +=
                            if (lp.height == 0)
                                lp.topRatio + lp.divHeight + lp.bottomRatio
                            else
                                lp.topRatio + lp.bottomRatio
                    vg.vv -= lp.topPx + lp.bottomPx
                }
                val s = getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
                if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.measureHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(vg.vl - vg.vf,lp))
                else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    if(lp.wrapMeasureVertical == -1) {
                        c.measure(0, s)
                        lp.wrapMeasureVertical = c.measuredHeight
                    }
                    lp.measureHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(lp.wrapMeasureVertical,lp))
                    if(!lp.verticalIndividual) vg.vv -= lp.wrapMeasureVertical
                } else if (lp.height > 0) {
                    lp.measureHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(lp.height,lp))
                    if(!lp.verticalIndividual) vg.vv -= lp.height
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
                if(!lp.horizontalIndividual && lp.width != ViewGroup.LayoutParams.MATCH_PARENT) {
                    hg.ha +=
                            if (lp.width == 0)
                                lp.leftRatio + lp.divWidth + lp.rightRatio
                            else
                                lp.leftRatio + lp.rightRatio
                    hg.hv -= lp.leftPx + lp.rightPx
                }
                val s = getChildMeasureSpec(widthMeasureSpec, 0, lp.width)
                if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.measureWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(hg.hl - hg.hf,lp))
                else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    if(lp.wrapMeasureHorizontal == - 1) {
                        c.measure(s, 0)
                        lp.wrapMeasureHorizontal = c.measuredWidth
                    }
                    if(!lp.horizontalIndividual) hg.hv -= lp.wrapMeasureHorizontal
                    lp.measureWidthSpec = getChildMeasureSpec(widthMeasureSpec,0,setHorizontalMarginFromLayoutParam(lp.wrapMeasureHorizontal,lp))
                } else if (lp.width > 0) {
                    if(!lp.horizontalIndividual) hg.hv -= lp.width
                    lp.measureWidthSpec = getChildMeasureSpec(widthMeasureSpec,0,setHorizontalMarginFromLayoutParam(lp.width,lp))
                }
            } else defaultHorizontalDivision.add(i)
        }
        divisionList.forEach {
            it.value.vf += paddingTop
            it.value.vl += paddingTop
            it.value.hf += paddingStart
            it.value.hl += paddingStart
        }

        for (g in divisionList.values) {
            for (l in g.verticalOrderList) {
                if (l.key - 1 > g.verticalList.size)
                    throw(DivisionLayoutException(DivisionLayoutException.E4))
                l.value.forEach {
                    g.verticalList.add(l.key - 1, it)
                }
            }
            for (l in g.horizontalOrderList) {
                if (l.key - 1 > g.horizontalList.size)
                    throw(DivisionLayoutException(DivisionLayoutException.E4))
                l.value.forEach {
                    g.horizontalList.add(l.key - 1, it)
                }
            }
            g.verticalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.height == 0) {
                    if(!lp.verticalIndividual){
                        lp.measureHeightSpec = if(!lp.verticalIndividual)
                            getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(f(g.vv, lp.divHeight, g.va),lp))
                        else
                            getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(f(g.vl - g.vf, lp.divHeight, lp.topRatio + lp.divHeight + lp.bottomRatio),lp))
                    }
                }
                if (lp.measureCheck) {
                    c.measure(lp.measureWidthSpec, lp.measureHeightSpec)
                    lp.measureCheck = false
                } else
                    lp.measureCheck = true
            }
            g.horizontalList.forEach {
                val c = getChildAt(it)
                val lp = c.layoutParams as DivisionLayout.LayoutParams
                if (lp.width == 0){
                    lp.measureWidthSpec = if(!lp.horizontalIndividual)
                        getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(f(g.hv, lp.divWidth, g.ha),lp))
                    else
                        getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(f(g.hl - g.hf, lp.divWidth, lp.leftRatio + lp.divWidth + lp.rightRatio),lp))
                }
                if (lp.measureCheck) {
                    c.measure(lp.measureWidthSpec, lp.measureHeightSpec)
                } else
                    lp.measureCheck = true
            }
        }
        defaultVerticalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.measureHeightSpec = when (lp.height) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> {
                    if(lp.wrapMeasureVertical == -1) {
                        c.measure(0,getChildMeasureSpec(heightMeasureSpec, 0, lp.height))
                        lp.wrapMeasureVertical = c.measuredHeight
                    }
                    getChildMeasureSpec(heightMeasureSpec,0,setVerticalMarginFromLayoutParam(lp.wrapMeasureVertical,lp))
                }
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(contentsHeight,lp))
                0 -> getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(f(contentsHeight - lp.topPx - lp.bottomPx, lp.divHeight, lp.topRatio + lp.divHeight + lp.bottomRatio),lp))
                else -> getChildMeasureSpec(heightMeasureSpec, 0, setVerticalMarginFromLayoutParam(lp.height,lp))
            }
            if (lp.measureCheck) {
                c.measure(lp.measureWidthSpec, lp.measureHeightSpec)
                lp.measureCheck = false
            } else
                lp.measureCheck = true
        }
        defaultHorizontalDivision.forEach {
            val c = getChildAt(it)
            val lp = c.layoutParams as DivisionLayout.LayoutParams
            lp.measureWidthSpec = when (lp.width) {
                ViewGroup.LayoutParams.WRAP_CONTENT -> {
                    if(lp.wrapMeasureHorizontal == -1) {
                        c.measure(getChildMeasureSpec(widthMeasureSpec,0,lp.width),0)
                        lp.wrapMeasureHorizontal = c.measuredWidth
                    }
                    getChildMeasureSpec(widthMeasureSpec,0,setHorizontalMarginFromLayoutParam(lp.wrapMeasureHorizontal,lp))
                }
                ViewGroup.LayoutParams.MATCH_PARENT -> getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(contentsWidth,lp))
                0 -> getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(f(contentsWidth - lp.leftPx - lp.rightPx, lp.divWidth, lp.leftRatio + lp.divWidth + lp.rightRatio),lp))
                else -> getChildMeasureSpec(widthMeasureSpec, 0, setHorizontalMarginFromLayoutParam(lp.width,lp))
            }
            if (lp.measureCheck) {
                c.measure(lp.measureWidthSpec, lp.measureHeightSpec)
                lp.measureCheck = false
            }
        }
    }

    private fun divisionJsonSet() {
        lateVerticalDivisionJson.clear()
        lateHorizontalDivisionJson.clear()

        divisionList.forEach {
            val n = it.key
            val gr = it.value

            if (gr.t is String || gr.b is String) {
                var catch = false
                if (gr.t is String) {
                    try {
                        gr.vf = stringToPx(gr.t as String)
                        if (gr.b is Number) {
                            if (gr.vu == -1) {
                                gr.vv = f(contentsHeight - gr.vf, anyToFloat(gr.h), anyToFloat(gr.h) + anyToFloat(gr.b))
                                gr.vl = gr.vf + gr.vv
                            } else {
                                gr.vv = gr.vu
                                gr.vl = gr.vv + gr.vf
                            }
                        }
                    } catch (e: DivisionLayoutException) {
                        catch = true
                    }
                }
                if (gr.b is String) {
                    try {
                        val l = stringToPx(gr.b as String)
                        gr.vl = contentsHeight - l
                        if (gr.vf != -1) {
                            if (gr.vu == -1) {
                                gr.vv = gr.vl - gr.vf
                            } else {
                                gr.vv = gr.vu
                                val a = (contentsHeight - gr.vv - gr.vf - l) / 2
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
                    } catch (e: DivisionLayoutException) {
                        catch = true
                    }
                }
                if (catch) lateVerticalDivisionJson.add(n)
            } else if (gr.t is Number && gr.b is Number) {
                if (gr.vu == -1) {
                    gr.vf = f(contentsHeight, anyToFloat(gr.t), anyToFloat(gr.t) + anyToFloat(gr.h) + anyToFloat(gr.b))
                    gr.vl = contentsHeight - f(contentsHeight, anyToFloat(gr.b), anyToFloat(gr.t) + anyToFloat(gr.h) + anyToFloat(gr.b))
                    gr.vv = gr.vl - gr.vf
                } else {
                    gr.vv = gr.vu
                    gr.vf = f(contentsHeight - gr.vv, anyToFloat(gr.t), anyToFloat(gr.t) + anyToFloat(gr.b))
                    gr.vl = gr.vv + gr.vf
                }
            } else
                throw (DivisionLayoutException(DivisionLayoutException.E5))

            if (gr.l is String || gr.r is String) {
                var catch = false
                if (gr.l is String) {
                    try {
                        gr.hf = stringToPx(gr.l as String)
                        if (gr.r is Number) {
                            if (gr.hu == -1) {
                                gr.hv = f(contentsWidth - gr.hf, anyToFloat(gr.w), anyToFloat(gr.w) + anyToFloat(gr.r))
                                gr.hl = gr.hf + gr.hv
                            } else {
                                gr.hv = gr.hu
                                gr.hl = gr.hv + gr.hf
                            }
                        }
                    } catch (e: DivisionLayoutException) {
                        catch = true
                    }
                }
                if (gr.r is String) {
                    try {
                        val l = stringToPx(gr.r as String)
                        gr.hl = contentsWidth - l
                        if (gr.hf != -1) {
                            if (gr.hu == -1) {
                                gr.hv = gr.hl - gr.hf
                            } else {
                                gr.hv = gr.hu
                                val a = (contentsWidth - gr.hv - gr.hf - l) / 2
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
                    } catch (e: DivisionLayoutException) {
                        catch = true
                    }
                }
                if (catch) lateHorizontalDivisionJson.add(n)
            } else if (gr.l is Number && gr.r is Number) {
                if (gr.hu == -1) {
                    gr.hf = f(contentsWidth, anyToFloat(gr.l), anyToFloat(gr.l) + anyToFloat(gr.w) + anyToFloat(gr.r))
                    gr.hl = contentsWidth - f(contentsWidth, anyToFloat(gr.r), anyToFloat(gr.l) + anyToFloat(gr.w) + anyToFloat(gr.r))
                    gr.hv = gr.hl - gr.hf
                } else {
                    gr.hv = gr.hu
                    gr.hf = f(contentsWidth - gr.hv, anyToFloat(gr.l), anyToFloat(gr.l) + anyToFloat(gr.r))
                    gr.hl = gr.hv + gr.hf
                }
            } else
                throw (DivisionLayoutException(DivisionLayoutException.E5))
        }

        while (lateVerticalDivisionJson.isNotEmpty())
            setDivisionVertical(lateVerticalDivisionJson[0])
        while (lateHorizontalDivisionJson.isNotEmpty())
            setDivisionHorizontal(lateHorizontalDivisionJson[0])
    }

    private fun makeGroup(name : String) {
        if(name == "p" || name == "parent")
            throw DivisionLayoutException(DivisionLayoutException.E16)
        if(!divisionList.containsKey(name)) divisionList[name] = D()
    }

    private fun f(p : Int, v : Float, m : Float) : Int {
        return if(v <= 0F || m <= 0F || p <= 0) 0
        else (p/m*v).toInt()
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
            if(!result.isNull("n") && result.isNull("name")) result.put("name",result.get("n"))
            if(!result.isNull("h") && result.isNull("height")) result.put("height",result.get("h"))
            if(!result.isNull("w") && result.isNull("width")) result.put("width",result.get("w"))
            if(!result.isNull("l") && result.isNull("left")) result.put("left",result.get("l"))
            if(!result.isNull("t") && result.isNull("top")) result.put("top",result.get("t"))
            if(!result.isNull("r") && result.isNull("right")) result.put("right",result.get("r"))
            if(!result.isNull("b") && result.isNull("bottom")) result.put("bottom",result.get("b"))
            return result
        } catch (e: JSONException) {
            throw DivisionLayoutException(DivisionLayoutException.E1, e)
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
            throw(DivisionLayoutException(DivisionLayoutException.E1, e))
        }
    }

    private fun stringToPx(string : String) : Int {
        var s = string
        s = s.toLowerCase()
        s = s.trim()
        if(s.length <= 2) throw (DivisionLayoutException(DivisionLayoutException.E11))
        when(s.substring(s.length - 2)) {
            "dp" -> {
                s = s.substring(0, s.length - 2)
                s.toFloatOrNull().let {
                    if (it == null)
                        throw (DivisionLayoutException(DivisionLayoutException.E11))
                    return (it * resources.displayMetrics.density).toInt()
                }
            }
            "px" -> {
                s = s.substring(0, s.length - 2)
                s.toIntOrNull().let {
                    if (it == null)
                        throw (DivisionLayoutException(DivisionLayoutException.E11))
                    return it
                }
            }
            else -> throw (DivisionLayoutException(DivisionLayoutException.E11))
        }
    }

    private fun setVerticalMarginFromLayoutParam(height : Int, lp : LayoutParams) : Int {
        val t = lp.divMarginTop.toFloatOrNull()
        val b = lp.divMarginBottom.toFloatOrNull()
        if(t == null)
            lp.marginTop = stringToPx(lp.divMarginTop)
        if(b == null)
            lp.marginBottom = stringToPx(lp.divMarginBottom)
        if(t != null && b != null) {
            lp.marginTop = f(height,t,t + b + lp.divMarginHeightRatio)
            lp.marginBottom = f(height,b,t + b + lp.divMarginHeightRatio)
        } else if(t != null)
            lp.marginTop = f(height - lp.marginBottom,t,t + lp.divMarginHeightRatio)
        else if(b != null)
            lp.marginBottom = f(height - lp.marginTop,b,b + lp.divMarginHeightRatio)
        var result = height - lp.marginTop - lp.marginBottom
        if(result < 0) {
            lp.marginTop = 0
            lp.marginBottom = 0
            result = 0
        }
        return result
    }

    private fun setHorizontalMarginFromLayoutParam(width : Int, lp : LayoutParams) : Int {
        val l = lp.divMarginLeft.toFloatOrNull()
        val r = lp.divMarginRight.toFloatOrNull()
        if(l == null)
            lp.marginLeft = stringToPx(lp.divMarginLeft)
        if(r == null)
            lp.marginRight = stringToPx(lp.divMarginRight)
        if(l != null && r != null) {
            lp.marginLeft = f(width,l,l + r + lp.divMarginWidthRatio)
            lp.marginRight = f(width,r,l + r + lp.divMarginWidthRatio)
        } else if(l != null)
            lp.marginLeft = f(width - lp.marginRight,l,l + lp.divMarginWidthRatio)
        else if(r != null)
            lp.marginRight = f(width - lp.marginLeft,r,r + lp.divMarginWidthRatio)
        var result = width - lp.marginLeft - lp.marginRight
        if(result < 0) {
            lp.marginLeft = 0
            lp.marginRight = 0
            result = 0
        }
        return result

    }

    private fun anyToFloat(a : Any) : Float {
        return (a as Number).toFloat()
    }

    private fun verticalPxInDivisionMember(name : String, heightMeasureSpec: Int) : Int {
        var result = 0
        for(i in 0 until childCount) {
            val c = getChildAt(i)
            val lp = c.layoutParams as LayoutParams
            if(lp.verticalDivision == name && !lp.verticalIndividual) {
                lp.topRatio = 0F; lp.divHeight = 0F; lp.bottomRatio = 0F
                if (lp.height > 0) result += lp.height
                else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(0, getChildMeasureSpec(heightMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT))
                    result += c.measuredHeight + lp.topPx + lp.bottomPx
                    lp.wrapMeasureVertical = c.measuredHeight
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
            if(lp.horizontalDivision == name && !lp.horizontalIndividual) {
                lp.topRatio = 0F; lp.divWidth = 0F; lp.bottomRatio = 0F
                if (lp.width > 0) result += lp.width
                else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    c.measure(getChildMeasureSpec(widthMeasureSpec, 0, ViewGroup.LayoutParams.WRAP_CONTENT), 0)
                    result += c.measuredWidth + lp.leftPx + lp.rightPx
                    lp.wrapMeasureHorizontal = c.measuredWidth
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
                g.vv = f(contentsHeight - g.vf, anyToFloat(g.h), anyToFloat(g.h) + anyToFloat(g.b))
                g.vl =  g.vv + g.vf
            } else if (g.b is String && g.t is Number) {
                g.vv = f(g.vl, anyToFloat(g.h), anyToFloat(g.h) + anyToFloat(g.t))
                g.vf = g.vl - g.vv
            }
        } else {
            g.vv = g.vu
            if (g.t is String && g.b is String) {
                val a : Int = (contentsHeight - g.vv - g.vf - (contentsHeight - g.vl))/2
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
                g.hv = f(contentsWidth - g.hf, anyToFloat(g.w), anyToFloat(g.w) + anyToFloat(g.r))
                g.hl = g.hv + g.hf
            } else if (g.r is String && g.l is Number) {
                g.hv = f(g.hl, anyToFloat(g.w), anyToFloat(g.w) + anyToFloat(g.l))
                g.hf = g.hl - g.hv
            }
        } else {
            g.hv = g.hu
            if (g.l is String && g.r is String) {
                val a : Int = (contentsWidth - g.hv - g.hf - (contentsWidth - g.hl))/2
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
            throw ((DivisionLayoutException(DivisionLayoutException.E8)))
        if(t.size != 2)
            throw (DivisionLayoutException(DivisionLayoutException.E13))
        if(t[0] == "p" || t[0] == "parent") {
            recycleHashSet.clear()
            return if(t[1] == "t" || t[1] == "top") {
                0
            } else if(t[1] == "b" || t[1] == "bottom") {
                contentsHeight
            } else
                throw (DivisionLayoutException(DivisionLayoutException.E6))
        }
        val gl = divisionList[t[0]]
        return if(gl == null || t[0] == LayoutParams.DEFAULT_DIVISION)
            throw(DivisionLayoutException(DivisionLayoutException.E10))
        else if(t[1] == "t" || t[1] == "top") {
            if(gl.vf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutException(DivisionLayoutException.E9))
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
                    throw (DivisionLayoutException(DivisionLayoutException.E9))
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
            throw (DivisionLayoutException(DivisionLayoutException.E6))
    }

    private fun findHorizontalValue(to : String, des : String) : Int {
        val t = des.split(".")
        if(t[0] == to)
            throw ((DivisionLayoutException(DivisionLayoutException.E8)))
        if(t.size != 2)
            throw (DivisionLayoutException(DivisionLayoutException.E13))
        if(t[0] == "p" || t[0] == "parent") {
            recycleHashSet.clear()
            return if(t[1] == "l" || t[1] == "left") {
                0
            } else if(t[1] == "r" || t[1] == "right") {
                contentsWidth
            } else
                throw (DivisionLayoutException(DivisionLayoutException.E6))
        }
        val gl = divisionList[t[0]]
        return if(gl == null || t[0] == LayoutParams.DEFAULT_DIVISION)
            throw(DivisionLayoutException(DivisionLayoutException.E10))
        else if(t[1] == "l" || t[1] == "left") {
            if(gl.hf == -1) {
                if (recycleHashSet.contains(t[0]))
                    throw (DivisionLayoutException(DivisionLayoutException.E9))
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
                    throw (DivisionLayoutException(DivisionLayoutException.E9))
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
            throw (DivisionLayoutException(DivisionLayoutException.E7))
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
            internal const val DEFAULT_VALUE = 0F
            internal const val DEFAULT_DP = "0dp"
        }

        var verticalDivision = DEFAULT_DIVISION
        var horizontalDivision = DEFAULT_DIVISION
        var verticalOrder = DEFAULT_ORDER
        var horizontalOrder = DEFAULT_ORDER
        var verticalIndividual = false
        var horizontalIndividual = false
        var divWidth = DEFAULT_VALUE
        var divHeight = DEFAULT_VALUE
        var divTop = DEFAULT_DP
        var divBottom = DEFAULT_DP
        var divLeft = DEFAULT_DP
        var divRight = DEFAULT_DP
        var divMarginTop = DEFAULT_DP
        var divMarginBottom = DEFAULT_DP
        var divMarginHeightRatio = DEFAULT_VALUE
        var divMarginLeft = DEFAULT_DP
        var divMarginRight = DEFAULT_DP
        var divMarginWidthRatio = DEFAULT_VALUE
        var marginTop = -1
        internal set
        var marginBottom = -1
        internal set
        var marginLeft = -1
        internal set
        var marginRight = -1
        internal set

        internal var topRatio = DEFAULT_VALUE
        internal var leftRatio = DEFAULT_VALUE
        internal var rightRatio = DEFAULT_VALUE
        internal var bottomRatio = DEFAULT_VALUE
        internal var topPx = 0
        internal var leftPx = 0
        internal var rightPx = 0
        internal var bottomPx = 0

        internal var measureCheck = false
        internal var measureWidthSpec = 0
        internal var measureHeightSpec = 0
        internal var wrapMeasureVertical = -1
        internal var wrapMeasureHorizontal = -1
        internal var layoutCheck = false
        internal var layoutLeft = 0
        internal var layoutTop = 0
        internal var layoutRight = 0
        internal var layoutBottom = 0

        constructor(width : Int, height : Int) : super(width,height)
        constructor(params: ViewGroup.LayoutParams) : super(params)
        constructor(context: Context?,attrs: AttributeSet?) : super(context,attrs) {
            context?.let { c -> attrs?.let { a -> setAttrs(c,a) } }
        }

        private fun setAttrs(context: Context, attrs: AttributeSet) {
            val ta = context.theme.obtainStyledAttributes(attrs,R.styleable.DivisionLayout_Layout,0,0)

            ta.getString(R.styleable.DivisionLayout_Layout_integrated_div)?.let {
                verticalDivision = it
                horizontalDivision = it
            }.let {
                if(it == null) {
                    ta.getString(R.styleable.DivisionLayout_Layout_vertical_div)?.let { div -> verticalDivision = div }
                    ta.getString(R.styleable.DivisionLayout_Layout_horizontal_div)?.let { div -> horizontalDivision = div }
                }
            }
            ta.getBoolean(R.styleable.DivisionLayout_Layout_integrated_individual,false).let {
                if(it) {
                    verticalIndividual = true
                    horizontalIndividual = true
                } else {
                    verticalIndividual = ta.getBoolean(R.styleable.DivisionLayout_Layout_vertical_individual,false)
                    horizontalIndividual = ta.getBoolean(R.styleable.DivisionLayout_Layout_horizontal_individual,false)
                }
            }
            divWidth = ta.getFloat(R.styleable.DivisionLayout_Layout_div_width,DEFAULT_VALUE)
            divHeight = ta.getFloat(R.styleable.DivisionLayout_Layout_div_height,DEFAULT_VALUE)
            ta.getString(R.styleable.DivisionLayout_Layout_div_top)?.let { divTop = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_bottom)?.let { divBottom = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_left)?.let { divLeft = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_right)?.let { divRight = it }
            verticalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_vertical_div_order,DEFAULT_ORDER)
            horizontalOrder = ta.getInt(R.styleable.DivisionLayout_Layout_horizontal_div_order,DEFAULT_ORDER)
            ta.getString(R.styleable.DivisionLayout_Layout_div_marginTop)?.let { divMarginTop = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_marginBottom)?.let { divMarginBottom = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_marginLeft)?.let { divMarginLeft = it }
            ta.getString(R.styleable.DivisionLayout_Layout_div_marginRight)?.let { divMarginRight = it }
            divMarginHeightRatio = ta.getFloat(R.styleable.DivisionLayout_Layout_div_marginHeightRatio,DEFAULT_VALUE)
            divMarginWidthRatio = ta.getFloat(R.styleable.DivisionLayout_Layout_div_marginWidthRatio,DEFAULT_VALUE)

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
            vv = contentsHeight
            vl = vv
            hv = contentsWidth
            hl = hv
        }

        fun reset() {
            va = 0F
            vf = 0
            vv = contentsHeight
            vu = -1
            t = 0F
            b = 0F
            h = 1F
            verticalList.clear()
            verticalOrderList.values.forEach { it.clear() }
            ha = 0F
            hf = 0
            hv = contentsWidth
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
