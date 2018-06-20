package com.roman.safronov.simpleshopinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edName, edQuantity;
    Button btnOk, btnCancel;
    TextView tvActFunc;
    int action = 0;
    long id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // находим View элементы

        edName = (EditText) findViewById(R.id.edName);
        edQuantity = (EditText) findViewById(R.id.edQuantity);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        tvActFunc = (TextView) findViewById(R.id.tvActFunc);

        Intent inputIntent = getIntent();
        action = inputIntent.getIntExtra("action", 0 );
        id = inputIntent.getLongExtra("id", 0);

        switch (action){
            case 1:
                tvActFunc.setText(R.string.tv_activity_add);
            break;
            case 2:
                tvActFunc.setText(R.string.tv_activity_edit);
                edName.setText(inputIntent.getStringExtra("name"));
                edQuantity.setText(inputIntent.getStringExtra("quantity"));

            break;
        }
    }

    @Override
    public void onClick (View v) {
        float quan = 0;
        switch (v.getId()){
            case  R.id.btnOk:
                Intent intent = new Intent();
                intent.putExtra("action", action);
                intent.putExtra("id", id);
                intent.putExtra("name", edName.getText().toString());
                try {quan = Float.valueOf(edQuantity.getText().toString());
                }
                catch (NumberFormatException e) {
                    Toast.makeText(this, "Wrong quantity", Toast.LENGTH_SHORT).show();
                }
                intent.putExtra("quantity", quan );
                setResult(RESULT_OK, intent);
                finish();
                break;
            case  R.id.btnCancel:
                finish();
                break;
        }



    }



}
