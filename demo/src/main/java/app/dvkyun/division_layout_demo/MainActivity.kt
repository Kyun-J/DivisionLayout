package app.dvkyun.division_layout_demo

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import app.dvkyun.divisionlayout.DivisionGroup
import app.dvkyun.divisionlayout.DivisionLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val divisionLayout = testdivision
        val test3 = TextView(this)
        test3.text = "test3"
        test3.gravity = Gravity.CENTER
        test3.setBackgroundColor(Color.parseColor("#3dd455"))
        divisionLayout.addView(test3)
        val param = test3.layoutParams as DivisionLayout.LayoutParams
        param.divWidth = 1.2F
        param.divHeight = 3F
        param.divBottom = 2F
        param.horizontalGroup = "g1"

        var click = false
        testbtn.setOnClickListener {
            if(click) {
                click = false
                param.divBottom = 2F
//                test3.layoutParams = param
                divisionLayout.setGroup(DivisionGroup("g1"))
            } else {
                click = true
                param.divBottom = 5F
//                test3.layoutParams = param

                val dg = DivisionGroup("g1")
                dg.left = 10F
                dg.width = 100F
                dg.right = 10F

//                layout.setGroup(JSONObject("{name:g1,left:10,width:100,right:10}"))
                divisionLayout.setGroup(dg)
            }
        }

    }
}
