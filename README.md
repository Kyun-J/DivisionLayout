# division-layout

[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()

## DivisionLayout은...
서로 다른 해상도의 안드로이드 디바이스에서, 같은 비율로 화면 뷰를 구성하기 위해 만든 Layout 라이브러리입니다.<br/>
top, height, bottom으로 구성된 세로 비율과 left, width, right로 구성된 가로 비율을 계산하여 뷰를 배치합니다.<br/>
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

This layout allows you to set the groups in which each view is to be included, allowing you to run the layout independently.<br/>
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
        app:division_vertical_group="group2"
	app:division_vertical_order="3"
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
        app:division_vertical_group="group2"
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
        app:division_vertical_group="group2"
        app:division_horizontal_group="group3"
        app:division_vertical_order="1"
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

It can arrange the views in a row by specifying the horizontal and vertical groups respectively, and group views with the division_group attribute, place the order with the division_order attribute.<br/><br/>
The division_width and division_height properties apply only when layout_width and layout_height are 0dp, respectively.<br/>
If wrap_content is specified or a unit other than 0dp is specified, the size of the view is allocated as specified and the corresponding division_width or division_height attributes are ignored.<br/>
In the case of match_parent, all attributes such as the division_group are ignored and fill the parent layout.

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

division_group is created automatically when you use it, but you can create it separately with the division_create_groups property of the DivisionLayout and specify the size as a percentage.<br/>
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

## 사용 방법

아래와 같이 속성을 지정하면, 각 속성을 <strong>비율로</strong> 계산하여 적용됩니다.
예를들어, division_top="1",division_width="0.5"이면 화면 내에서 뷰의 크기가 1, 뷰의 윗 빈공간을 2의 비율로 계산하여 표시합니다.

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

수평 및 수직 그룹을 각각 지정하고 뷰를 division_group 속성으로 그룹화하여 뷰를 정렬 할 수 있으며 division_order 속성을 사용하여 뷰의 순서를 지정합니다.<br/><br/>
또한 division_width과 division_height 속성은 각각 layout_width, layout_height가 0dp일때만 적용됩니다<br/>
wrap_content이거나 0dp가 아닐경우, division_width혹은 division_height은 layout_width 및 layout_height에 명시된 크기로 지정됩니다.<br/>
match_parent일 경우는, 다른 레이아웃들과 마찬가지로 부모 뷰에 가득 차게 되며, 해당 부분의 division 속성은 모두 무시됩니다.

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

division_group 속성은 뷰 속성에 적용시 자동 생성되나, DivisionLayout의 division_create_groups 속성으로 따로 생성할 수 있으며 그룹의 크기 범위를 비율로써 지정할 수 있습니다.<br/>
division_create_groups 은 JsonArray형식의 String을 받으며, 각 그룹은 아래와 같이 JsonObject로 지정합니다. 그룹 내부의 속성은 이름(name) 과 상하좌우 및 그 크기로 되어있습니다. 

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
