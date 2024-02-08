package com.example.hellotangible;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    private Button fakeButton;
    private View[] colorFields;
    private ImageView smokeImageView;
    private boolean colorFieldsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fakeButton = findViewById(R.id.fakeButton);
        smokeImageView = findViewById(R.id.smokeImageView);

        colorFields = new View[]{
                findViewById(R.id.colorField1),
                findViewById(R.id.colorField2),
                findViewById(R.id.colorField3),
                findViewById(R.id.colorField4),
                findViewById(R.id.colorField5),
                findViewById(R.id.colorField6),
                findViewById(R.id.colorField7)
        };

        fakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle colorFields visibility
                if (colorFieldsVisible) {
                    hideColorFieldsWithDelay();
                } else {
                    showColorFieldsWithDelay();
                }
                // Update visibility state
                colorFieldsVisible = !colorFieldsVisible;
            }
        });
    }

    private void showColorFieldsWithDelay() {
        // Show colorFields one by one with a delay
        for (int i = 0; i < colorFields.length; i++) {
            final int index = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    colorFields[index].setVisibility(View.VISIBLE);
                    // If it's the last colorField, show smokeImageView
                    if (index == colorFields.length - 1) {
                        smokeImageView.setVisibility(View.VISIBLE);
                    }
                }
            }, i * 200); // Delay each colorField by 0.2 seconds
        }
    }

    private void hideColorFieldsWithDelay() {
        // Hide colorFields one by one with a delay
        for (int i = colorFields.length - 1; i >= 0; i--) {
            final int index = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    colorFields[index].setVisibility(View.GONE);
                    // If it's the first colorField, hide smokeImageView
                    if (index == 0) {
                        smokeImageView.setVisibility(View.GONE);
                    }
                }
            }, (colorFields.length - i - 1) * 200); // Delay each colorField by 0.2 seconds
        }
    }
}
