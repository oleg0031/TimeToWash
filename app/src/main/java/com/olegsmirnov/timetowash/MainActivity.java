package com.olegsmirnov.timetowash;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerTimeAvailable;
    private Spinner spinnerTimeWork;
    private Spinner spinnerCleaningType;
    private TextView tvPrice;
    private EditText etSquare;
    private EditText etComment;
    private EditText etNumber;
    private String[] timeWorkValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeWorkValues = getResources().getStringArray(R.array.time_work);
        TextView tvClock = findViewById(R.id.tv_clock);
        displayClock(tvClock);
        tvPrice = findViewById(R.id.tv_price);
        etSquare = findViewById(R.id.et_square);
        etComment = findViewById(R.id.et_comment);
        etNumber = findViewById(R.id.et_number);
        spinnerTimeAvailable = findViewById(R.id.spin_time_available);
        spinnerTimeWork = findViewById(R.id.spin_time_work);
        spinnerCleaningType = findViewById(R.id.spin_cleaning_type);

        Button btnPrice = findViewById(R.id.btn_price);
        Button btnOrder = findViewById(R.id.btn_order);
        btnPrice.setOnClickListener(onPriceClickListener);
        btnOrder.setOnClickListener(onOrderClickListener);
    }

    private void displayClock(final TextView tvClock) {
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvClock.setText(new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);
    }

    View.OnClickListener onPriceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar snackbar = Snackbar.make(view, "Введите площадь квартиры", Snackbar.LENGTH_LONG);
            TextView snackbarTextView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackbarTextView.setTextSize(18);
            if (etSquare.getText().toString().isEmpty()) {
                snackbar.show();
                return;
            }

            String strWorkTime = spinnerTimeWork.getSelectedItem().toString();
            String strCleaningType = spinnerCleaningType.getSelectedItem().toString();
            int price = 0;
            int counter = 0;
            for (int i = timeWorkValues.length - 1; i >= 0 ; i--) {
                counter++;
                String val = timeWorkValues[i];
                if (strWorkTime.equals(val)) {
                    if (strCleaningType.equals("Легкая"))
                        price = 50 * counter;
                    else if (strCleaningType.equals("Генеральная"))
                        price = 75 * counter;
                }
            }

            int square = Integer.parseInt(etSquare.getText().toString());
            if (square <= 100)
                price += 25;
            else if (square <= 300)
                price += 50;
            else if (square <= 500)
                price += 100;
            else price += 200;
            tvPrice.setText("Стоимость уборки: " + price + " грн.");
        }
    };

    View.OnClickListener onOrderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar snackbar = Snackbar.make(view, "Введите площадь квартиры и номер телефона", Snackbar.LENGTH_LONG);
            TextView snackbarTextView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackbarTextView.setTextSize(18);
            if (etSquare.getText().toString().isEmpty() || etNumber.getText().toString().isEmpty()) {
                snackbar.show();
                return;
            }
            String timeAvailable = spinnerTimeAvailable.getSelectedItem().toString();
            String timeWork = spinnerTimeWork.getSelectedItem().toString();
            String cleaningType = spinnerCleaningType.getSelectedItem().toString();
            String square = etSquare.getText().toString();
            String comment = etComment.getText().toString();
            String phoneNumber = etNumber.getText().toString();
            StringBuilder strForm = new StringBuilder();
            strForm.append("До скольки нужно убрать: ").append(timeAvailable).append("\n").append("За сколько нужно убрать: ").append(timeWork).
                    append("\n").append("Тип уборки: ").append(cleaningType).append("\n").append("Номер телефона: ").append(phoneNumber).append("\n").append("Комментарий: ").append(comment);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"oleg0031@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Заказ на уборку");
            i.putExtra(Intent.EXTRA_TEXT, strForm.toString());
            startActivity(Intent.createChooser(i, "Оставить заказ"));
        }
    };

}
