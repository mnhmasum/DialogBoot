# DialogBoot 
DialogBoot is an android library based on annotation processor to create an AlertDialog in your code. You don't need to write boilerplate code anymore to create AlertDialog. 

``` JAVA
@InjectDialog(
    isCancelable = true,
    getMessage = "Hi I am a Dialog Boot to create a dialog"
)
public AlertDialog dialog;
```
### To show dialog in your activity

``` JAVA
DialogBootLoader.bind(this);
dialog.show();
```

### DialogBoot Plus
Before without DialogBoot
```Java
View view = getLayoutInflater.inflate(R.layout.my_layout, null);
```
Now with DialogBoot
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
