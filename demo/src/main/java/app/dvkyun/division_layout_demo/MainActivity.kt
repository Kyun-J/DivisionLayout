package app.dvkyun.division_layout_demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        example1.setOnClickListener {
            startActivity(Intent(this,Example1::class.java))
        }
        example2.setOnClickListener {
            startActivity(Intent(this,Example2::class.java))
        }
    }

}
