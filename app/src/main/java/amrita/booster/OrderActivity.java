package amrita.booster;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import java.util.Locale;
import java.util.Random;

/**
 * Created by amritachowdhury on 9/3/17.
 */

public class OrderActivity extends AppCompatActivity {
    private Double latitude;
    private Double longitude;
    private TextView address1;
    private TextView city;
    private TextView state;
    private TextView country;
    private TextView zip;
    private TextView deliverySchedule;
    private TextView deliveryScheduleMessage;
    private TextView creditCardHolderName;
    private TextView creditCardNumber;
    private CardView addressCardView;
    private CardView creditCardView;
    private String deliveryWindow;
    private CreditCard creditCard;
    private ProgressBar progressBar;
    private String deliveryTime;
    private String deliveryMessage;
    private Address address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        deliveryWindow = intent.getStringExtra("deliveryTime");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            creditCard = (CreditCard) bundle.getSerializable("creditCard");
        drawComponent();
        new ScheduleBoost().execute();

    }

    private void setCreditCardDetails() {
        creditCardHolderName.setText(creditCard.getCardHolderName());
        String creditCardDisplay = "xxxxxxxxxxxx" + creditCard.getCreditCardNumber().substring(
                creditCard.getCreditCardNumber().length() - 4);
        creditCardNumber.setText(creditCardDisplay);
        creditCardView.setVisibility(View.VISIBLE);
    }

    private void setDeliverySchedule() {
        Calendar currentCal = deliveryWindow.equals("Morning") ? getMorningDeliverySchedule()
                : getAfternoonDeliverySchedule();
        deliveryTime = String.valueOf(currentCal.getTime());
        deliveryScheduleMessage.setText(deliveryMessage);
        deliverySchedule.setText(deliveryTime);
    }

    private void setAddress() {
        city.setText(address.getLocality());
        state.setText(address.getAdminArea());
        country.setText(address.getCountryName());
        zip.setText(address.getPostalCode());
        address1.setText(address.getAddressLine(0).split(",")[0]);
        addressCardView.setVisibility(View.VISIBLE);
    }

    private Calendar getMorningDeliverySchedule() {
        Random r = new Random();
        int hourOfDay = r.nextInt(6);
        Calendar current = Calendar.getInstance();
        int currentHours = current.get( Calendar.HOUR_OF_DAY );
        Calendar scheduled = Calendar.getInstance();
        scheduled.add(Calendar.HOUR_OF_DAY, hourOfDay);
        int scheduledHours = scheduled.get( Calendar.HOUR_OF_DAY );
        if (currentHours >= 12) {
            deliveryMessage = "Oops! you requested your boost in the afternoon. It is scheduled for: ";
        } else if (scheduledHours >= 12) {
            deliveryMessage = "Sorry! no more slots available for morning. It is scheduled for: ";
        } else {
            deliveryMessage = "Congratulations! your boost is scheduled for ";
        }
        return scheduled;
    }

    private Calendar getAfternoonDeliverySchedule() {
        Random r = new Random();
        int hourOfDay = r.nextInt(6);
        Calendar scheduled = Calendar.getInstance();
        scheduled.add(Calendar.HOUR_OF_DAY, hourOfDay);
        int scheduledHours = scheduled.get( Calendar.HOUR_OF_DAY );
        if (scheduledHours < 12) {
            deliveryMessage = "Sorry! no more slots available for afternoon. It is scheduled for: ";
        } else {
            deliveryMessage = "Congratulations! your boost is scheduled for ";
        }
        return scheduled;
    }

    private void getAddressFromLatLong() {
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(this, Locale.getDefault());
            address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
        } catch (Exception Exception) {
            Log.e("bapi", Exception.getMessage());
        }
    }

    private void drawComponent() {
        address1 = (TextView) findViewById(R.id.address1);
        city = (TextView) findViewById(R.id.city);
        state = (TextView) findViewById(R.id.state);
        country = (TextView) findViewById(R.id.country);
        zip = (TextView) findViewById(R.id.zip);
        deliverySchedule = (TextView) findViewById(R.id.delivery_schedule);
        deliveryScheduleMessage = (TextView) findViewById(R.id.delivery_schedule_message);
        creditCardHolderName = (TextView) findViewById(R.id.credit_card_holder_name);
        creditCardNumber = (TextView) findViewById(R.id.credit_card_number);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        addressCardView = (CardView) findViewById(R.id.address_card_view);
        creditCardView = (CardView) findViewById(R.id.credit_card_view);
    }

    public void onClickCancelBtn(View view) {
        Toast.makeText(this, "Your boost has been cancelled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    class ScheduleBoost extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                getAddressFromLatLong();
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            setAddress();
            setCreditCardDetails();
            setDeliverySchedule();
        }
    }
}
