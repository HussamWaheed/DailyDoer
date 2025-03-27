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

public class FocusActivity extends AppCompatActivity {

    private TextView timerText, quoteText;
    private Button startButton, pauseButton, resetButton;
    private ImageButton image_btn;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 25 * 60 * 1000; // 25 minutes in milliseconds
    private boolean timerRunning = false;

    //array of motivational quotes
    private final String[] quotes = {
            "“Either you run the day, or the day runs you.” – Jim Rohn",
            "“Someday is not a day of the week.” – Janet Dailey",
            "“Make each day your masterpiece.” – John Wooden",
            "“Your time is limited, don’t waste it living someone else’s life.” – Steve Jobs",
            "“The best way to get started is to quit talking and begin doing.” – Walt Disney",
            "“Do one thing every day that scares you.” – Eleanor Roosevelt",
            "“Believe you can and you're halfway there.” – Theodore Roosevelt",
            "“Everything you’ve ever wanted is on the other side of fear.” – George Addair",
            "“Success is the sum of small efforts, repeated day-in and day-out.” – Robert Collier",
            "“Don’t watch the clock; do what it does. Keep going.” – Sam Levenson",
            "“The secret of getting ahead is getting started.” – Mark Twain",
            "“Quality means doing it right when no one is looking.” – Henry Ford",
            "“If you spend too much time thinking about a thing, you’ll never get it done.” – Bruce Lee",
            "“Start where you are. Use what you have. Do what you can.” – Arthur Ashe",
            "“Go as far as you can see; when you get there, you’ll be able to see further.” – Thomas Carlyle",
            "“Don’t let yesterday take up too much of today.” – Will Rogers",
            "“I would rather die of passion than of boredom.” – Vincent van Gogh",
            "“Great things are not done by impulse, but by a series of small things brought together.” – Vincent van Gogh",
            "“Well done is better than well said.” – Benjamin Franklin",
            "“Success usually comes to those who are too busy to be looking for it.” – Henry David Thoreau"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        //bind views
        timerText = findViewById(R.id.timerText);
        quoteText = findViewById(R.id.quoteText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        image_btn = findViewById(R.id.image_btn);
        Intent intent = getIntent();

        //set a random quote at launch
        showRandomQuote();

        //display the initial time
        updateTimerText();

        //button click listeners
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer(true));
        resetButton.setOnClickListener(v -> resetTimer());

        //back button functionality
        image_btn.setOnClickListener(v -> {
            startActivity(new Intent(FocusActivity.this, dashboardActivity.class));
            //finish();
        });
    }

    //start the countdown timer
    private void startTimer() {
        if (timerRunning) return;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            public void onFinish() {
                timerRunning = false;
                Toast.makeText(getApplicationContext(), "Time's up! Take a break.", Toast.LENGTH_LONG).show();
            }
        }.start();

        timerRunning = true;
        Toast.makeText(getApplicationContext(), "Timer Started!", Toast.LENGTH_SHORT).show();
    }

    //pause the timer
    private void pauseTimer(boolean showToast) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            if (showToast) {
                Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //reset the timer and show a new quote
    private void resetTimer() {
        timeLeftInMillis = 25 * 60 * 1000;
        updateTimerText();
        showRandomQuote();
        pauseTimer(false);
        Toast.makeText(getApplicationContext(), "Reset", Toast.LENGTH_SHORT).show();
    }

    //format and display the time
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    //pick a random quote from the list
    private void showRandomQuote() {
        int index = new Random().nextInt(quotes.length);
        quoteText.setText(quotes[index]);
    }
}
