# division-layout

[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()

## What is this?
Division-layout is the layout that represents a view with a constant ratio, regardless of resolution.

<img src=imgs/example1.png>

This layout can be arranged by setting the proportions of left, top, right, bottom, width, and height.

```xml
<?xml version="1.0" encoding="utf-8"?>
<app.dvkyun.divisionlayout.DivisionLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
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
<img src=imgs/example1.jpg>

<hr/>

This layout allows you to set the groups in which each view is to be included, allowing you to run the layout independently.
You can also set the order of views within a group, and set the size of the group itself.

<img src=imgs/example2.png>

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
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="test1"
        android:gravity="center"
        android:background="@color/colorPrimary"
        app:division_virtical_group="group2"
        app:division_horizontal_group="group1"
        app:division_width="1"
        app:division_height="1"
        app:division_virtical_order="3"/>
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="test2"
        android:gravity="center"
        android:background="@color/colorAccent"
        app:division_virtical_group="group2"
        app:division_width="1"
        app:division_height="2" />
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
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
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
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

## ToDo
- Behavior for layout_width, layout_height (currently ignoring this setting)
- Research of margin and padding application method

## Requirements
- Android SDK 15+

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
  compile 'com.github.Kyun-J:division-layout:0.02'
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
