package app.dvkyun.divisionlayout


class DivisionLayoutExecption : Exception {

    constructor(msg : String) : super(msg)
    constructor(msg : String, e : Throwable) : super(msg,e)

    companion object {
        const val E1 = "Illegal format of group-Json, please check division_create_groups."
        const val E2 = "JSON error. Please contact github issue"
        const val E3 = "Illegal format of group-Json, please check inserted JSONObject."
        const val E4 = "Order can not be greater than the number of views in group. Also, please arrange the order starting from 1"
        const val E5 = "The top, bottom, left, and right side of the group should only apply to the positions of other groups or float values"
        const val E6 = "Incorrect input value. Only top or bottom values are allowed."
        const val E7 = "Incorrect input value. Only left or right values are allowed."
        const val E8 = "Group can not specify itself."
        const val E9 = "The group should be assigned a fixed horizontal or vertical value. Do not fall into the loop."
        const val E10 = "Must specify an existing group."
        const val E11 = "Invalid input value. Dp or px type of int is required."
        const val E12 = "Invalid input value. The exact value(dp or px) or ratio(float) is required."
        const val E13 = "Please enter a bullet (.) Correctly."
        const val E14 = "When create a group in xml, must need the name."
        const val E15 = "Group names can not be duplicated."
    }

}