package evandemaris.appletapper;

/**
 * Created by Revan on 12/14/2015.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class AutoRepeatButton extends ImageButton {
    private long initialRepeatDelay = 500;
    private long repeatIntervalInMilliseconds = 100;

    // speedup
    private long repeatIntervalCurrent = repeatIntervalInMilliseconds;
    private long repeatIntervalStep = 2;
    private long repeatIntervalMin = 10;

    private Runnable repeatClickWhileButtonHeldRunnable = new Runnable() {
        @Override
        public void run() {
            // Perform the present repetition of the click action provided by the user
            // in setOnClickListener().
            performClick();

            // Schedule the next repetitions of the click action,
            // faster and faster until it reaches repeaterIntervalMin
            if (repeatIntervalCurrent > repeatIntervalMin)
                repeatIntervalCurrent = repeatIntervalCurrent - repeatIntervalStep;

            postDelayed(repeatClickWhileButtonHeldRunnable, repeatIntervalCurrent);
        }
    };

    private void commonConstructorCode() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    // Just to be sure that we removed all callbacks,
                    // which should have occurred in the ACTION_UP
                    removeCallbacks(repeatClickWhileButtonHeldRunnable);

                    // Perform the default click action.
                    performClick();

                    // Schedule the start of repetitions after a one half second delay.
                    repeatIntervalCurrent = repeatIntervalInMilliseconds;
                    postDelayed(repeatClickWhileButtonHeldRunnable, initialRepeatDelay);
                } else if (action == MotionEvent.ACTION_UP) {
                    // Cancel any repetition in progress.
                    removeCallbacks(repeatClickWhileButtonHeldRunnable);
                }

                // Returning true here prevents performClick() from getting called
                // in the usual manner, which would be redundant, given that we are
                // already calling it above.
                return true;
            }
        });
    }

    public AutoRepeatButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        commonConstructorCode();
    }

    public AutoRepeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructorCode();
    }

    public AutoRepeatButton(Context context) {
        super(context);
        commonConstructorCode();
    }
}