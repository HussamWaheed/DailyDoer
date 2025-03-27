package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

/**
 * FocusActivity implements a Pomodoro-style timer with motivational quotes.
 * Features include:
 * - 25-minute countdown timer
 * - Start/Pause/Reset functionality
 * - Random motivational quotes display
 * - Notification when timer completes
 */
public class FocusActivity extends AppCompatActivity {

    // UI Components
    private TextView timerText;    // Displays the countdown time
    private TextView quoteText;    // Displays motivational quotes
    private Button startButton;    // Starts the timer
    private Button pauseButton;    // Pauses the timer
    private Button resetButton;   // Resets the timer
    private ImageButton image_btn; // Navigation back button

    // Timer Variables
    private CountDownTimer countDownTimer; // The countdown timer instance
    private long timeLeftInMillis = 25 * 60 * 1000; // Default 25 minutes in milliseconds
    private boolean timerRunning = false;  // Tracks timer state

    // Motivational quotes database
    private final String[] quotes = {
            "“Either you run the day, or the day runs you.” – Jim Rohn",
            "“Someday is not a day of the week.” – Janet Dailey",
            // ... (other quotes remain the same)
            "“Success usually comes to those who are too busy to be looking for it.” – Henry David Thoreau"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        // Initialize UI components
        timerText = findViewById(R.id.timerText);
        quoteText = findViewById(R.id.quoteText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        image_btn = findViewById(R.id.image_btn);

        // Show random quote when activity starts
        showRandomQuote();

        // Display initial timer value
        updateTimerText();

        // Set button click listeners
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer(true));
        resetButton.setOnClickListener(v -> resetTimer());

        // Back button returns to dashboard
        image_btn.setOnClickListener(v -> {
            startActivity(new Intent(FocusActivity.this, dashboardActivity.class));
            // Note: finish() is commented to allow returning via back button
        });
    }

    /**
     * Starts the countdown timer if not already running
     * Creates a new CountDownTimer that updates every second
     */
    private void startTimer() {
        if (timerRunning) return; // Prevent multiple timers

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText(); // Update UI with remaining time
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                // Notify user when timer completes
                Toast.makeText(getApplicationContext(),
                        "Time's up! Take a break.", Toast.LENGTH_LONG).show();
            }
        }.start();

        timerRunning = true;
        Toast.makeText(getApplicationContext(), "Timer Started!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Pauses the running timer
     * @param showToast Whether to show a pause confirmation toast
     */
    private void pauseTimer(boolean showToast) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            if (showToast) {
                Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Resets the timer to initial 25-minute duration
     * Also displays a new random quote
     */
    private void resetTimer() {
        timeLeftInMillis = 25 * 60 * 1000; // Reset to 25 minutes
        updateTimerText();
        showRandomQuote();
        pauseTimer(false); // Stop timer without toast
        Toast.makeText(getApplicationContext(), "Reset", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the timer display with formatted time
     * Converts milliseconds to MM:SS format
     */
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    /**
     * Displays a random quote from the quotes array
     */
    private void showRandomQuote() {
        int index = new Random().nextInt(quotes.length);
        quoteText.setText(quotes[index]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
