package com.ballsort.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements GameView.Listener {

    private static final int COINS_PER_LEVEL = 10;
    private static final int REWARD_MULTIPLIER = 3;
    private static final int HINT_COST = 25;
    private static final int TUBE_COST = 50;
    private static final int START_COINS = 50;

    private GameView gameView;
    private TextView levelText, coinsText, winTitle, winReward;
    private View winOverlay, tubeButton;
    private AdsManager ads;
    private SharedPreferences prefs;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(null, r, "solver", 8 * 1024 * 1024);
        t.setDaemon(true);
        return t;
    });

    private int level;
    private int coins;
    private GameState initialState;
    private boolean extraTubeUsed;
    private boolean loading;
    private int loadToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge();
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("game", MODE_PRIVATE);
        level = prefs.getInt("level", 1);
        coins = prefs.getInt("coins", START_COINS);

        gameView = findViewById(R.id.gameView);
        levelText = findViewById(R.id.levelText);
        coinsText = findViewById(R.id.coinsText);
        winOverlay = findViewById(R.id.winOverlay);
        winTitle = findViewById(R.id.winTitle);
        winReward = findViewById(R.id.winReward);
        tubeButton = findViewById(R.id.btnTube);
        gameView.setListener(this);

        findViewById(R.id.btnSettings).setOnClickListener(v -> showSettings());
        findViewById(R.id.btnRestart).setOnClickListener(v -> restartLevel());
        findViewById(R.id.btnUndo).setOnClickListener(v -> {
            if (!gameView.undo()) toast(getString(R.string.nothing_to_undo));
        });
        findViewById(R.id.btnHint).setOnClickListener(v -> onHint());
        tubeButton.setOnClickListener(v -> onExtraTube());
        findViewById(R.id.btnCollectAd).setOnClickListener(v -> onCollectWithAd());
        findViewById(R.id.btnNext).setOnClickListener(v -> onNext(COINS_PER_LEVEL, true));
        winOverlay.setOnClickListener(v -> { /* block taps on the board */ });

        ads = new AdsManager(this, findViewById(R.id.adContainer));
        ads.start();

        updateCoins();
        loadLevel();
    }

    // ---------------- Window / insets ----------------

    @SuppressWarnings("deprecation")
    private void setupEdgeToEdge() {
        Window w = getWindow();
        w.setStatusBarColor(Color.TRANSPARENT);
        w.setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
        } else {
            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View root = findViewById(R.id.root);
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int l, t, r, b;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets i = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                l = i.left; t = i.top; r = i.right; b = i.bottom;
            } else {
                l = insets.getSystemWindowInsetLeft();
                t = insets.getSystemWindowInsetTop();
                r = insets.getSystemWindowInsetRight();
                b = insets.getSystemWindowInsetBottom();
            }
            v.setPadding(l, t, r, b);
            return insets;
        });
        root.requestApplyInsets();
    }

    // ---------------- Levels ----------------

    private void loadLevel() {
        loading = true;
        final int token = ++loadToken;
        final int lv = level;
        levelText.setText(getString(R.string.level_n, lv));
        winOverlay.setVisibility(View.GONE);
        worker.execute(() -> {
            GameState st = LevelGenerator.generate(lv);
            handler.post(() -> {
                if (token != loadToken || isFinishing()) return;
                initialState = st;
                extraTubeUsed = false;
                gameView.setState(st.copy());
                updateTubeButton();
                loading = false;
            });
        });
    }

    private void restartLevel() {
        if (loading || initialState == null || gameView.isBusy()) return;
        GameState st = initialState.copy();
        if (extraTubeUsed) st.addEmptyTube();
        gameView.setState(st);
    }

    @Override
    public void onMoveMade() {
        // Hook for sounds/analytics if you add them later.
    }

    @Override
    public void onSolved() {
        int finished = level;
        level++;
        prefs.edit().putInt("level", level).apply();
        handler.postDelayed(() -> showWin(finished), 450);
    }

    private void showWin(int finishedLevel) {
        if (isFinishing()) return;
        winTitle.setText(getString(R.string.level_complete, finishedLevel));
        winReward.setText(getString(R.string.coins_reward, COINS_PER_LEVEL));
        ((TextView) findViewById(R.id.btnCollectAd)).setText(
                getString(R.string.collect_with_ad, COINS_PER_LEVEL * REWARD_MULTIPLIER));
        winOverlay.setVisibility(View.VISIBLE);
    }

    private void onCollectWithAd() {
        ads.showRewarded(
                () -> onNext(COINS_PER_LEVEL * REWARD_MULTIPLIER, false),
                () -> toast(getString(R.string.ad_not_ready)));
    }

    private void onNext(int reward, boolean allowInterstitial) {
        if (winOverlay.getVisibility() != View.VISIBLE) return;
        winOverlay.setVisibility(View.GONE);
        addCoins(reward);
        int finished = level - 1;
        if (allowInterstitial) {
            ads.onLevelFinished(finished, this::loadLevel);
        } else {
            loadLevel();
        }
    }

    // ---------------- Boosters ----------------

    private void onHint() {
        if (loading || gameView.isBusy()) return;
        final GameState snapshot = gameView.getState().copy();
        final int token = loadToken;
        worker.execute(() -> {
            List<int[]> sol = Solver.solve(snapshot, 200000);
            handler.post(() -> {
                if (token != loadToken || isFinishing()) return;
                if (sol == null || sol.isEmpty()) {
                    toast(getString(R.string.no_hint));
                    return;
                }
                final int[] m = sol.get(0);
                offerBooster(getString(R.string.hint_title), getString(R.string.hint_desc), HINT_COST, () -> {
                    if (gameView.getState() != null && gameView.getState().canMove(m[0], m[1])) {
                        gameView.showHint(m[0], m[1]);
                    } else {
                        toast(getString(R.string.no_hint));
                    }
                });
            });
        });
    }

    private void onExtraTube() {
        if (loading || gameView.isBusy()) return;
        if (extraTubeUsed) {
            toast(getString(R.string.tube_used));
            return;
        }
        offerBooster(getString(R.string.tube_title), getString(R.string.tube_desc), TUBE_COST, () -> {
            if (extraTubeUsed) return;
            extraTubeUsed = true;
            gameView.addEmptyTube();
            updateTubeButton();
        });
    }

    /** Lets the player pay with coins or watch a rewarded ad. */
    private void offerBooster(String title, String desc, int cost, Runnable action) {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(title)
                .setMessage(desc + "\n\n" + getString(R.string.you_have_coins, coins))
                .setPositiveButton(R.string.watch_ad_free, (d, w) -> ads.showRewarded(action,
                        () -> toast(getString(R.string.ad_not_ready))))
                .setNegativeButton(getString(R.string.use_coins, cost), (d, w) -> {
                    if (coins >= cost) {
                        addCoins(-cost);
                        action.run();
                    } else {
                        toast(getString(R.string.not_enough_coins));
                    }
                })
                .setNeutralButton(android.R.string.cancel, null)
                .show();
    }

    private void updateTubeButton() {
        tubeButton.setAlpha(extraTubeUsed ? 0.4f : 1f);
    }

    // ---------------- Settings ----------------

    private void showSettings() {
        boolean privacy = ads.isPrivacyOptionsRequired();
        String[] items = privacy
                ? new String[]{getString(R.string.privacy_settings), getString(R.string.reset_progress)}
                : new String[]{getString(R.string.reset_progress)};
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(R.string.settings)
                .setItems(items, (d, which) -> {
                    if (privacy && which == 0) ads.showPrivacyOptions();
                    else confirmReset();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void confirmReset() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(R.string.reset_progress)
                .setMessage(R.string.reset_confirm)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    level = 1;
                    coins = START_COINS;
                    prefs.edit().putInt("level", level).putInt("coins", coins).apply();
                    updateCoins();
                    loadLevel();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // ---------------- Helpers ----------------

    private void addCoins(int delta) {
        coins = Math.max(0, coins + delta);
        prefs.edit().putInt("coins", coins).apply();
        updateCoins();
    }

    private void updateCoins() {
        coinsText.setText(getString(R.string.coins_value, coins));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        ads.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ads.resume();
    }

    @Override
    protected void onDestroy() {
        ads.destroy();
        handler.removeCallbacksAndMessages(null);
        worker.shutdownNow();
        super.onDestroy();
    }
}
