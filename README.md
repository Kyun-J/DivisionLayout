# DivisionLayout

[![](https://jitpack.io/v/Kyun-J/DivisionLayout.svg)](https://jitpack.io/#Kyun-J/DivisionLayout)
[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat)]()


## DivisionLayout은...
서로 다른 해상도의 안드로이드 디바이스에서, 같은 비율로 View를 배치할 수 있는 Layout 라이브러리입니다.  
top, height, bottom으로 구성된 세로 비율과 left, width, right로 구성된 가로 비율을 계산하여 View를 배치합니다.  
Division을 사용하면 여러 자식 뷰들을 가로, 세로로 정렬하여 배치할 수 있습니다.
## 특징
1. ConstraintLayout의 percent나, LinearLayout의 weight 처럼 상대적인 비율로 뷰를 배치합니다.
2. Division이라는 개념을 사용하여 자식 View들을 묶을 수 있습니다.  
Division은 ViewGroup과 달리 ChildView로써 Parent에 종속되지 않고, 로직상으로만 구현이 되어 있습니다.

자세한 사용 방법은 [**Wiki**](https://github.com/Kyun-J/division-layout/wiki)를 참고해 주세요.

## What is this?
Divisionlayout is the Layout library that allows to place views at the same rate in Android devices of different resolutions.  
Position the view by calculating the vertical ratio of top, height, and bottom and the horizontal ratio of left, width, and right.  
With Division, you can placement child views without a ViewGroup(Layout).

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
        app:div_left="5"
        app:div_top="1"
        app:div_right="5"
        app:div_bottom="0.5"
        app:div_width="10"
        app:div_height="1"/>
</app.dvkyun.divisionlayout.DivisionLayout>
```
The above xml is calculated by applying the ratio as shown in the figure below.

<img src=imgs/example1.jpg width="384" height="640">

Please refer to the [**Wiki**](https://github.com/Kyun-J/division-layout/wiki) for details.

## ToDoNext
- Make guildlines in android studio preview
- Added various functions in default_Division

## Requirements
- Android SDK 17+

## Usage

Add to your root build.gradle:
```gradle
allprojects {
	repositories {
	  maven { url "https://jitpack.io" }
	}
}
```

Add the dependency:
```gradle
dependencies {
  implementation 'com.github.Kyun-J:division-layout:0.05'
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
