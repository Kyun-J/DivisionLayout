package app.dvkyun.divisionlayout

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

class Division_Constraint_Layout : ConstraintLayout {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}