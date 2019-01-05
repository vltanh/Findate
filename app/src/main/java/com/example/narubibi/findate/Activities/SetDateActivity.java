package com.example.narubibi.findate.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.narubibi.findate.R;

import java.util.Calendar;

public class SetDateActivity extends AppCompatActivity {
    private Toolbar setDateToolbar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog dialog;

    TextView editTextDate;
    EditText editTextPlace, editTextMessage;
    Button btnOK, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);

        setDateToolbar = findViewById(R.id.set_date_toolbar);
        setSupportActionBar(setDateToolbar);
        getSupportActionBar().setTitle("Set Date");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextDate = findViewById(R.id.tv_set_date_show);
        editTextPlace = findViewById(R.id.ed_set_place);
        editTextMessage = findViewById(R.id.ed_set_message);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                if (dialog == null) {
                    dialog = new DatePickerDialog(
                            SetDateActivity.this,
                            AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                            dateSetListener,
                            year, month, day);
                }

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                editTextDate.setText(date);
            }
        };

        btnOK = findViewById(R.id.btn_invite_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("date", editTextDate.getText().toString());
                returnIntent.putExtra("place", editTextPlace.getText().toString());
                returnIntent.putExtra("message", editTextMessage.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        btnCancel = findViewById(R.id.btn_invite_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
