package evandemaris.appletapper;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.util.TypedValue;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.Random;


public class Fruit_Main extends AppCompatActivity implements OnClickListener {

    private static final String PREFS_FILE = "evandemaris.appletapper.preferences";
    private static final String RECALLED_GOLD_KEY = "RECALLED_GOLD_KEY";
    private static final String RECALLED_APPLES_KEY = "RECALLED_APPLES_KEY";
    private static final String RECALLED_UPGRADES_KEY = "RECALLED_UPGRADES_KEY";
    private static final String APPLE_COUNT_KEY = "APPLE_COUNT_KEY";
    private static final String GOLD_COUNT_KEY = "GOLD_COUNT_KEY";
    private static final String UPGRADES_PURCHASED_KEY = "UPGRADES_PURCHASED_KEY";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    ImageView buyLadderButton;
    ImageView buyFarmButton;
    ImageView buyFarmerButton;
    ImageView appleTapButton;
    ImageView sellApplesButton;
    ImageView buyCapacityButton;
    ImageView debug;
    Button resetButton;
    TextView appleText;
    TextView goldText;
    TextView keyCountText;
    TextView level;
    TextView applesAtCapacity;
    private int clickValue;
    private int farmerCount;
    private int farmCount;
    private int ladderCount;
    private int appleLimit;
    private int appleCount;
    private int goldCount;
    private String upgradesPurchased;
    Context context;
    private int expCounter;
    int expRewardedAt;
    Random random = new Random();
    private int keyCounter;
    private int nextExpLevel;
    private int currentLevel;
    private int decayCount = 0;
    int passiveTapUpgrades;



    private long startTime = 0;
    private String timerText = "";
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerText = String.format("%d:%02d", minutes, seconds);
            decayingFarmerCounter(farmerCount,2);
            appleText.setText(String.format(getString(R.string.apple_count),
                    Integer.toString(appleCount)));
            timerHandler.postDelayed(this, 500);
        }
    };
    private boolean timerRunning = false;


    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        timerRunning = false;
    }
    private void startTimer() {
        timerRunning = true;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }
    private void restartTimer() {
        if (timerRunning = true) {stopTimer();}
        startTimer();
    }



    void decayingFarmerCounter(int apples, int seconds) {
        if (decayCount < 0) {
            decayCount = seconds * 2;
        }
        decayCount --;
        if (decayCount == 0) {
            appleCount += apples;
            if (appleCount > appleLimit) {
                appleCount = appleLimit;
                stopTimer();
            }
            decayCount = seconds * 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_main);
        mSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        context = getApplication();

        debug = (ImageView) findViewById(R.id.debug);
        resetButton = (Button) findViewById(R.id.resetButton);
        appleTapButton = (ImageView) findViewById(R.id.mainButton);
        sellApplesButton = (ImageView) findViewById(R.id.sellApple);
        buyFarmerButton = (ImageView) findViewById(R.id.buyFarmer);
        buyFarmButton = (ImageView) findViewById(R.id.buyFarm);
        buyLadderButton = (ImageView) findViewById(R.id.buyLadder);
        buyCapacityButton = (ImageView) findViewById(R.id.buyCapacity);

        applesAtCapacity = (TextView) findViewById(R.id.sellApplesWarningText);
        keyCountText = (TextView) findViewById(R.id.keyText);
        goldText = (TextView) findViewById(R.id.goldText);
        appleText = (TextView) findViewById(R.id.myTextTitle);
        level = (TextView) findViewById(R.id.level);

        //  Set on click listeners to the buttons.
        resetButton.setOnClickListener(this);
        appleTapButton.setOnClickListener(this);
        sellApplesButton.setOnClickListener(this);
        buyFarmerButton.setOnClickListener(this);
        buyFarmButton.setOnClickListener(this);
        buyLadderButton.setOnClickListener(this);
        buyCapacityButton.setOnClickListener(this);
        debug.setOnClickListener(this);

        //  Assign the SharedPreferences object and editor, pull data from mSharedPreferences
        //  and assign that data.

        appleCount = mSharedPreferences.getInt(RECALLED_APPLES_KEY, 0);
        goldCount = mSharedPreferences.getInt(RECALLED_GOLD_KEY, 0);
        nextExpLevel = 64;
        currentLevel = 0;
        keyCounter = 0;
        expCounter = 0;
        farmerCount = 0;
        farmCount = 0;
        ladderCount = 0;
        appleLimit = 4;
        passiveTapUpgrades = farmerCount; // Add any other passive collectors here

        if (passiveTapUpgrades != 0) {
            restartTimer();
        }
        expRewardedAt = random.nextInt(512) + 1;
        clickValue = 1 + (farmerCount * 5) + (farmCount * 10) + ladderCount;
        // Save upgrades as placeholder values if none have been purchased.
        // Replace with index number if purchase has been made.
        //FIXME: This isn't a functional way to represent multiple purchases of the same upgrade.
        upgradesPurchased = mSharedPreferences.getString(RECALLED_UPGRADES_KEY, "a0");
        appleText.setText(String.format(getString(R.string.apple_count), appleCount));
        goldText.setText(String.format(getString(R.string.gold_text), goldCount));


        // Changes the font size of the text.
        appleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

    }

    // Random number generator.
    int randomWithRange(int min, int max)
    {
        int range = Math.abs(max - min) + 1;
        return (int)(Math.random() * range) + (min <= max ? min : max);
    }

    //This method is designed to override the default saveinstance state so the score can be saved.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(APPLE_COUNT_KEY, appleCount);
        savedInstanceState.putInt(GOLD_COUNT_KEY, goldCount);
        savedInstanceState.putString(UPGRADES_PURCHASED_KEY, upgradesPurchased);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        appleCount = savedInstanceState.getInt(APPLE_COUNT_KEY);
        goldCount = savedInstanceState.getInt(GOLD_COUNT_KEY);
        upgradesPurchased = savedInstanceState.getString(UPGRADES_PURCHASED_KEY);
        appleText.setText(String.format(getString(R.string.apple_count),
                Integer.toString(appleCount)));
        goldText.setText(String.format(getString(R.string.gold_text),
                Integer.toString(goldCount)));
    }

    @Override
    protected void onPause() {
        super.onPause();

        mEditor.putInt(RECALLED_APPLES_KEY, appleCount);
        mEditor.putInt(RECALLED_GOLD_KEY, goldCount);
        mEditor.putString(RECALLED_UPGRADES_KEY, upgradesPurchased);
        mEditor.apply();
        stopTimer();
    }

    private void stopTapping() {
        appleTapButton.setAlpha(0.5f);
        applesAtCapacity.setVisibility(View.VISIBLE);
    }
    private void startTapping() {
        applesAtCapacity.setVisibility(View.INVISIBLE);
        appleTapButton.setAlpha(1.0f);
    }

    @Override
    public void onClick(View v) {
        if (v == appleTapButton) {
            if ((appleCount + clickValue) > appleLimit) {
                appleCount = appleLimit;
                stopTapping();
//                Toast.makeText(context, "Out of room to store apples." +
//                        " Sell apples for gold!", Toast.LENGTH_SHORT).show();
            } else {
                appleCount += clickValue;
                appleText.setText(String.format(getString(R.string.apple_count),
                        Integer.toString(appleCount)));
                expCount();
            }
        }
        else if (v == resetButton) {
            appleCount = 0;
            goldCount = 0;
            clickValue = 1;
            ladderCount = 0;
            farmCount = 0;
            farmerCount = 0;
            appleLimit = 4;
            appleText.setText(String.format(getString(R.string.apple_count),
                    Integer.toString(appleCount)));
            goldText.setText(String.format(getString(R.string.gold_text),
                    Integer.toString(goldCount)));
            }
        else if (v == sellApplesButton) {
            startTapping();

            goldCount += appleCount;
            appleCount = 0;
            appleText.setText(String.format(getString(R.string.apple_count),
                    Integer.toString(appleCount)));
            goldText.setText(String.format(getString(R.string.gold_text),
                    Integer.toString(goldCount)));
            if (passiveTapUpgrades != 0) {
                restartTimer();
            }

        }
        else if (v == buyCapacityButton) {
            int capacityPrice = appleLimit * clickValue + 5;
            if (goldCount >= capacityPrice) {
                appleLimit = appleLimit * 2;
                goldCount -= capacityPrice;
                goldText.setText(String.format(getString(R.string.gold_text),
                        Integer.toString(goldCount)));
                startTapping();
            }
        }
        else if (v == buyFarmerButton) {
            int farmerPrice = farmerCount * clickValue * 5 + 75;
            if (goldCount >= farmerPrice) {
                farmerCount++;
                goldCount -= farmerPrice;
                goldText.setText(String.format(getString(R.string.gold_text),
                        Integer.toString(goldCount)));
                restartTimer();
            }
        }
        else if (v == buyFarmButton) {
            int farmPrice = farmCount * clickValue * farmerCount * 10 + 150;
            if (goldCount >= farmPrice) {
                farmCount++;
                goldCount -= farmPrice;
                goldText.setText(String.format(getString(R.string.gold_text),
                        Integer.toString(goldCount)));
                clickValue += 10;
            }
        }
        else if (v == buyLadderButton) {
            int ladderPrice = ladderCount * clickValue * ladderCount + 15;
            if (goldCount >= ladderPrice) {
                ladderCount++;
                goldCount -= ladderPrice;
                goldText.setText(String.format(getString(R.string.gold_text),
                        Integer.toString(goldCount)));
                clickValue ++;
                //TODO: make messages reflecting purchase of upgrades
            }
        }
        else if (v == debug) {
            appleLimit = 2000000000;
            appleCount = 999999999;
            appleText.setText(String.format(getString(R.string.apple_count),
                    Integer.toString(appleCount)));
            goldCount = 2000000000;
            goldText.setText(String.format(getString(R.string.gold_text),
                    Integer.toString(goldCount)));
        }
    }

    private void expCount() {
        expCounter++;
        if (expCounter > expRewardedAt) {
            expRewardedAt += randomWithRange(1, 512);
            int keysToAdd = randomWithRange(1,4);
            keyCounter += keysToAdd;
            keyCountText.setText(String.format(getString(R.string.key_count),
                    Integer.toString(keyCounter)));
            Toast.makeText(context, "Congratulations! You've won the lottery, and have been awarded " +
                            keysToAdd + " keys.",
                    Toast.LENGTH_SHORT).show();
        }
        if (expCounter > nextExpLevel) {
            nextExpLevel += randomWithRange(256, 1024);
            currentLevel += 1;
            level.setText("Level: " + currentLevel);
            int keysToAdd = randomWithRange(2,4);
            keyCounter += keysToAdd;
            keyCountText.setText(String.format(getString(R.string.key_count),
                    Integer.toString(keyCounter)));
            Toast.makeText(context, "Congratulations! You've reached level " +
                            currentLevel + " and earned " + keysToAdd + " keys.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}