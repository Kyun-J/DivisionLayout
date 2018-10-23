package app.dvkyun.division_layout_demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import app.dvkyun.divisionlayout.Division;
import app.dvkyun.divisionlayout.DivisionLayout;

public class DivisionInCode extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example1);
        DivisionLayout divisionLayout = findViewById(R.id.division_layout);
        divisionLayout.getDivision("div2");
        Division div = new Division("div");
        divisionLayout.addDivision(div);
        ArrayList divisions = new ArrayList<Division>();
        divisions.add(div);
        divisionLayout.setAllDivision(divisions);
        div.setLeft(1F);
        div.setTop("100dp");
        div.setRight(div.getLeftPosition());
        div.setHeight(Division.WRAP);

    }

}
