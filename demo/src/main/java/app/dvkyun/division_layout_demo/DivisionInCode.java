package app.dvkyun.division_layout_demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import app.dvkyun.divisionlayout.Division;
import app.dvkyun.divisionlayout.DivisionLayout;

public class DivisionInCode extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example1);
        DivisionLayout layout = findViewById(R.id.division_layout);
        Division div = new Division("div");
        div.setLeft(1F);
        div.setTop("100dp");
        div.setRight(div.getLeftPosition());
        div.setHeight(Division.WRAP);

    }

}
