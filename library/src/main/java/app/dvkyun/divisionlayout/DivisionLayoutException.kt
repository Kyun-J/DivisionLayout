package app.dvkyun.divisionlayout


class DivisionLayoutException : Exception {

    constructor(msg : String) : super(msg)
    constructor(msg : String, e : Throwable) : super(msg,e)

    companion object {
        const val E1 = "Illegal format of Json, please check create_divisions."
        const val E2 = "JSON error. Please contact github issue"
        const val E3 = "Illegal format of Json, please check inserted JSONObject."
        const val E4 = "Order can not be greater than the number of views in division. Also, please arrange the order starting from 1"
        const val E5 = "The top, bottom, left, and right side of the division should only apply to the positions of other groups or float values"
        const val E6 = "Incorrect input value. Only top or bottom values are allowed."
        const val E7 = "Incorrect input value. Only left or right values are allowed."
        const val E8 = "Division can not specify itself."
        const val E9 = "The division should be assigned a fixed horizontal or vertical value. Do not fall into the loop."
        const val E10 = "Must specify an existing division. Also, the division that was called in parent can not associate with the division that was first called in the child view."
        const val E11 = "Invalid input value. Dp or px type of int is required."
        const val E12 = "Invalid input value. The exact value(dp or px) or ratio(float) is required."
        const val E13 = "Please enter dots(.) Correctly."
        const val E14 = "When create division manually, must need the name. and can not use spaces(\"\")."
        const val E15 = "Division names can not be duplicated."
        const val E16 = "Division names can not use parent, or p."
    }

}