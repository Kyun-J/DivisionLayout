package app.dvkyun.divisionlayout


class DivisionLayoutExecption : Exception {

    constructor(msg : String) : super(msg)
    constructor(msg : String, e : Throwable) : super(msg,e)

}