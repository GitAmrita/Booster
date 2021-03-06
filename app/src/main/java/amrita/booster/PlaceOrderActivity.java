package amrita.booster;

import android.app.DatePickerDialog;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by amritachowdhury on 9/3/17.
 */

public class PlaceOrderActivity extends AppCompatActivity {
    private EditText cardHolderName;
    private EditText cardNumber;
    private EditText securityCode;
    private EditText cardExpiry;
    private EditText deliveryDate;
    private RadioGroup deliveryTime;
    private double latitude;
    private double longitude;
    private CreditCard creditCard;
    private String errorMessage;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        drawComponents();
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

    }

    public void placeOrder(View view) {
        if (!validateCreditCard()) {
            showErrorDialog();
        } else {
            creditCard = new CreditCard();
            creditCard.setCardHolderName(cardHolderName.getText().toString());
            creditCard.setCreditCardNumber(cardNumber.getText().toString());
            creditCard.setSecurityCode(securityCode.getText().toString());
            creditCard.setExpires(cardExpiry.getText().toString());
            RadioButton selectedButton = (RadioButton)this.findViewById(
                    deliveryTime.getCheckedRadioButtonId());
            String deliveryWindow = selectedButton.getText().toString();
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("deliveryTime", deliveryWindow);
            intent.putExtra("deliveryDate", deliveryDate.getText().toString());
            Bundle bundle = new Bundle();
            bundle.putSerializable("creditCard", creditCard);
            intent.putExtras(bundle);
            this.startActivity(intent);
        }
    }

    private void drawComponents() {
        cardHolderName = (EditText) findViewById(R.id.card_information_name_field);
        cardNumber = (EditText) findViewById(R.id.card_information_number_field);
        securityCode = (EditText) findViewById(R.id.card_information_info_cvc_field);
        cardExpiry = (EditText) findViewById(R.id.card_information_info_expiry_field);
        deliveryTime = (RadioGroup) findViewById(R.id.radio_delivery_time);
        deliveryDate = (EditText) findViewById(R.id.delivery_date);
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDeliveryRequestDate();
            }
        };

        deliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PlaceOrderActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDeliveryRequestDate() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        deliveryDate.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean validateCreditCard() {
        if (cardHolderName.getText().toString().isEmpty() ||
                cardNumber.getText().toString().isEmpty() ||
                securityCode.getText().toString().isEmpty() ||
                cardExpiry.getText().toString().isEmpty()) {
            errorMessage = "Please enter the all the credit card fields.";
            return false;
        } else if (cardNumber.getText().toString().trim().length() != 16) {
            errorMessage =" Invalid credit card number. It is the 16 digit number on your card";
            return false;
        }
        return true;
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Invalid Credit Card")
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
