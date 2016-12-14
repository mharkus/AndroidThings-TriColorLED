package com.marctan.helloandroidthings;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final long INTERVAL_BETWEEN_BLINKS_MS = 50;

    private Gpio[] leds = new Gpio[3];
    private Handler mHandler = new Handler();
    private int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIO: " + service.getGpioList());

        try {
            for(int i=0; i < 3; i++){
                final Gpio led = service.openGpio("IO1" + (i + 1));
                led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                leds[i] = led;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mHandler.post(mBlinkRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mBlinkRunnable);


        if (leds != null) {
            try {
                for (int i = 0; i < leds.length; i++) {
                    leds[i].close();
                }

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }


    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
            if (leds == null || leds.length == 0) {
                return;
            }

            try {
                // Step 3. Toggle the LED state
                leds[currentIndex].setValue(!leds[currentIndex].getValue());
                currentIndex++;
                if(currentIndex >= 3){
                    currentIndex = 0;
                }

                // Step 4. Schedule another event after delay.
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
}
