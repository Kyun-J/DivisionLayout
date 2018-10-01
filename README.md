# division-layout

[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()

## DivisionLayout은...
서로 다른 해상도의 안드로이드 디바이스에서, 같은 비율로 화면 뷰를 구성하기 위해 만든 Layout 라이브러리입니다.
top, height, bottem으로 구성된 세로 비율과 left, width, right로 구성된 가로 비율을 계산하여 뷰를 배치합니다.
뷰들의 세로, 가로 각각 group으로 묶어 각 그룹별로 묶어서 배치할 수 있습니다.

## What is this?
Division-layout is the layout that represents a view with a constant ratio, regardless of resolution.

<img src=imgs/example1.png width="384" height="640">

This layout can be arranged by setting the proportions of left, top, right, bottom, width, and height.

```xml
<?xml version="1.0" encoding="utf-8"?>
<app.dvkyun.divisionlayout.DivisionLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="Division_Layout"
        android:background="#fffc"
        android:gravity="center"
        app:division_left="5"
        app:division_width="10"
        app:division_right="5"
        app:division_top="1"
        app:division_height="1"
        app:division_bottom="0.5"/>
</app.dvkyun.divisionlayout.DivisionLayout>
```
The above xml is calculated by applying the ratio as shown in the figure below.

<img src=imgs/example1.jpg width="384" height="640">

<hr/>

This layout allows you to set the groups in which each view is to be included, allowing you to run the layout independently.
You can also set the order of views within a group, and set the size of the group itself.

<img src=imgs/example2.png width="384" height="640">

```xml
<?xml version="1.0" encoding="utf-8"?>
<app.dvkyun.divisionlayout.DivisionLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:division_create_groups="[
    {name:group1,left:1.5,width:1,right:1},
    {name:group2,top:1,height:1,bottom:1},
    {name:group3,top:0,height:0,bottom:0,left:50,width:200,right:100}
    ]">
    <TextView
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="test1"
        android:gravity="center"
        android:background="@color/colorPrimary"
        app:division_virtical_group="group2"
	app:division_virtical_order="3"
	app:division_height="1"
        app:division_horizontal_group="group1"
        app:division_width="1"
        />
    <TextView
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="test2"
        android:gravity="center"
        android:background="@color/colorAccent"
        app:division_virtical_group="group2"
        app:division_width="1"
        app:division_height="2" />
    <TextView
    	android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="test3"
        android:gravity="center"
        android:background="@color/colorPrimaryDark"
        app:division_horizontal_group="group1"
        app:division_width="1"
        app:division_height="2"
        app:division_top="8"
        app:division_bottom="3.4"
        app:division_horizontal_order="1"/>
    <TextView
    	android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="test4"
        android:gravity="center"
        android:background="#3df344"
        app:division_virtical_group="group2"
        app:division_horizontal_group="group3"
        app:division_virtical_order="1"
        app:division_height="1"
        app:division_width="1"/>
</app.dvkyun.divisionlayout.DivisionLayout>
```

## How to use it

When you apply the following attributes in the view in division, each width and height are calculated and applied.

```xml
android:layout_width="0dp"
android:layout_height="0dp"
app:division_left=float
app:division_width=float
app:division_right=float
app:division_top=float
app:division_height=float
app:division_bottom=float
```

It can arrange the views in a row by specifying the horizontal and vertical groups respectively, and group views with the division_group attribute, place the order with the division_order attribute.

The division_width and division_height properties apply only when layout_width and layout_height are 0dp, respectively.
In the case of match_parent, all attributes such as the division_group are ignored and fill the parent layout.
If wrap_content is specified or a unit other than 0dp is specified, the size of the view is allocated as specified and the corresponding division_width or division_height attributes are ignored.

```xml
<TextView
    android:layout_width="0dp"
    android:layout_height="100dp"
    android:text="text2"
    android:gravity="center"
    android:background="#3df344"
    app:division_horizontal_group="g1"
    app:division_horizontal_order="2"
    app:division_left="0.5"
    app:division_width="1"
    app:division_right="0.5"/>
<TextView
    android:layout_width="0dp"
    android:layout_height="100dp"
    android:text="text1"
    android:gravity="center"
    android:background="#3df344"
    app:division_horizontal_group="g1"
    app:division_horizontal_order="1"
    app:division_left="1"
    app:division_width="0.5"
    app:division_right="0.8"/>
```

division_group is created automatically when you use it, but you can create it separately with the division_create_groups property of the DivisionLayout and specify the size as a percentage.
division_create_groups receives a string of type JsonArray, and each division_group is created as a JsonObject.

```xml
<app.dvkyun.divisionlayout.DivisionLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:division_create_groups="[
    {name:group1,left:1.5,width:1,right:1},
    {name:group2,top:1,height:1,bottom:1},
    {name:group3,top:0,height:0,bottom:0,left:50,width:200,right:100}
    ]">
```

## ToDo
- Apply margin and padding
- Controlling divisionlayout in Java code
- Apply with ConstraintLayout (DivisionConstrantLayout)

## Requirements
- Android SDK 17+

## Usage

Add to your root build.gradle:
```Groovy
allprojects {
	repositories {
	  maven { url "https://jitpack.io" }
	}
}
```

Add the dependency:
```Groovy
dependencies {
  implementation 'com.github.Kyun-J:division-layout:0.03-b'
}
```

## License

	The MIT License (MIT)

	Copyright © 2018 Kyun-J

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
