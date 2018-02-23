package com.example.arpit.toolkit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Arpit on 23-08-2017.
 */

public class Calculator extends Fragment implements View.OnClickListener {

    TextView Value;
    float var1=0.0f,var2=0.0f;
    int flag=0;
    boolean typeFlag=true;
    private final int ADD=1,SUBTRACT=2,DIVIDE=3,MULTIPLY=4,UNSET=0;

    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0,button000, buttonDec;
    Button add, subtract,divide,multiply;
    Button result, clear,sign;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.calculator_layout,container,false);

        Value=(TextView) v.findViewById(R.id.editText);
        button1=(Button)v.findViewById(R.id.button1);
        button2=(Button)v.findViewById(R.id.button2);
        button3=(Button)v.findViewById(R.id.button3);
        button4=(Button)v.findViewById(R.id.button4);
        button5=(Button)v.findViewById(R.id.button5);
        button6=(Button)v.findViewById(R.id.button6);
        button7=(Button)v.findViewById(R.id.button7);
        button8=(Button)v.findViewById(R.id.button8);
        button9=(Button)v.findViewById(R.id.button9);
        button0=(Button)v.findViewById(R.id.button0);
        button000=(Button)v.findViewById(R.id.button000);

        buttonDec=(Button)v.findViewById(R.id.buttonDec);
        multiply=(Button)v.findViewById(R.id.buttonMultiply);
        divide=(Button)v.findViewById(R.id.buttonDivide);
        add=(Button)v.findViewById(R.id.buttonAdd);
        subtract=(Button)v.findViewById(R.id.buttonSubtract);

        sign=(Button)v.findViewById(R.id.buttonSign);
        clear=(Button)v.findViewById(R.id.clear);
        result=(Button)v.findViewById(R.id.buttonResult);

        Value.setText("0.0");


        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button0.setOnClickListener(this);
        add.setOnClickListener(this);
        clear.setOnClickListener(this);
        result.setOnClickListener(this);
        subtract.setOnClickListener(this);
        multiply.setOnClickListener(this);
        divide.setOnClickListener(this);
        button000.setOnClickListener(this);
        buttonDec.setOnClickListener(this);
        sign.setOnClickListener(this);


        return v;
    }



    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.button1)
        {
            if (typeFlag)
            {
                Value.setText("1");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"1");
        }
        if (v.getId()==R.id.button2)
        {
            if (typeFlag)
            {
                Value.setText("2");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"2");
        }
        if (v.getId()==R.id.button3)
        {
            if (typeFlag)
            {
                Value.setText("3");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"3");
        }
        if (v.getId()==R.id.button4)
        {
            if (typeFlag)
            {
                Value.setText("4");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"4");
        }
        if (v.getId()==R.id.button5)
        {
            if (typeFlag)
            {
                Value.setText("5");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"5");
        }
        if (v.getId()==R.id.button6)
        {
            if (typeFlag)
            {
                Value.setText("6");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"6");
        }
        if (v.getId()==R.id.button7)
        {
            if (typeFlag)
            {
                Value.setText("7");
                typeFlag=false;
            } else
            Value.setText(Value.getText()+"7");
        }
        if (v.getId()==R.id.button8)
        {
            if (typeFlag)
            {
                Value.setText("8");
                typeFlag=false;
            } else
            Value.setText(Value.getText()+"8");
        }
        if (v.getId()==R.id.button9)
        {
            if (typeFlag)
            {
                Value.setText("9");
                typeFlag=false;
            } else
            Value.setText(Value.getText()+"9");
        }
        if (v.getId()==R.id.button0)
        {
            if (typeFlag)
            {
                Value.setText("0");
                typeFlag=false;
            }else
            Value.setText(Value.getText()+"0");
        }
        if (v.getId()==R.id.button000)
        {
            if (typeFlag)
            {
                Value.setText("0");
                typeFlag=false;
            }else
                Value.setText(Value.getText()+"000");
        }
        if (v.getId()==R.id.buttonDec)
        {
            if (typeFlag)
            {
                Value.setText("0.");
                typeFlag=false;
            }else if (!Value.getText().toString().contains("."))
                Value.setText(Value.getText()+".");
        }
        if (v.getId()==R.id.clear)
        {
            Value.setText("0.0");
            var2=var1=0.0f;
            flag=UNSET;
            typeFlag=true;
        }
        if (v.getId()==R.id.buttonResult)
        {
            if (!typeFlag) {
                    var1= Float.parseFloat(Value.getText().toString());
                typeFlag=true;
            }
            if (flag==ADD) {
                var2 = var2+var1;
            } else if(flag==SUBTRACT) {
                var2=var2-var1;
            }else if(flag==MULTIPLY) {
                var2=var1*var2;
            }else if(flag==DIVIDE) {
                var2=var2/var1;
            }
            Value.setText(var2+"");
        }
        if (v.getId()==R.id.buttonAdd)
        {
                var2= Float.parseFloat(Value.getText().toString());
                Value.setText(Value.getText());
                flag=ADD;
                typeFlag=true;

        }
        if (v.getId()==R.id.buttonSubtract)
        {
                var2= Float.parseFloat(Value.getText().toString());
                flag=SUBTRACT;
                typeFlag=true;


        }
        if (v.getId()==R.id.buttonMultiply)
        {
                var2= Float.parseFloat(Value.getText().toString());
                flag=MULTIPLY;
                typeFlag=true;


        }
        if (v.getId()==R.id.buttonDivide)
        {
                var2= Float.parseFloat(Value.getText().toString());
                flag=DIVIDE;
                typeFlag=true;


        }
        if (v.getId()==R.id.buttonSign)
        {
            if (Value.getText().toString().startsWith("-"))
            {
                Value.setText(Value.getText().toString().replace("-","+"));
            }else
                Value.setText("-"+Value.getText().toString().replace("+",""));
        }
    }
}
