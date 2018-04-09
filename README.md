# DialogBoot 
DialogBoot is an android library based on annotation processor to create an AlertDialog in your code. You don't need to write boilerplate code anymore to create AlertDialog. 

``` JAVA
@InjectDialog(
    isCancelable = true,
    getMessage = "Hi I am Dialog Boot to create a dialog"
)
public AlertDialog dialog;
```
### To show dialog in your activity

``` JAVA
DialogBootLoader.bind(this);
dialog.show();
```

### DialogBoot Plus
Without DialogBoot
```Java
View view = getLayoutInflater.inflate(R.layout.my_layout, null);
```
With DialogBoot
```JAVA
@InjectView(layout = R.layout.layout)
View view;
```

### EXAMPLE 1

```JAVA
@InjectDialog(
    isCancelable = true,
    getMessage = "Hi I am DialogBoot to create a dialog"
)
public AlertDialog dialog;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    DialogBootLoader.bind(this);
    dialog.show();
}
```


### EXAMPLE 2 with custom layout

```JAVA
public class MainActivity implements View.OnClickListener{
   @InjectDialog(
            isCancelable = true,
            getMessage = "Hi am Dialog Boot to create a dialog"
    )
    public AlertDialog dialog;

    @InjectView(layout = R.layout.layout)
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogBootLoader.bind(this, view);

        Button button = view.findViewById(R.id.button1);
        button.setOnClickListener(this);
        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button1) {
            Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

}
```

### SETUP

## To your root build.gradle

```groovy
allprojects {
    repositories {
        ...
        mavenLocal()
        ...
    }
}
```

## To your app level build.gradle

```groovy
dependencies {
    implementation 'com.masum.dialogboot:dialogboot-library:1.3.8'
    annotationProcessor 'com.masum.dialogboot:dialogboot-compiler:1.3.8'
   
}
```
License
-----------

    MIT License

    Copyright (c) 2018 Nazmul Hasan Masum

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
