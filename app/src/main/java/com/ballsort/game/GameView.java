package com.ballsort.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayDeque;

/** Draws the tubes and balls and handles taps. */
public class GameView extends View {

    public interface Listener {
        void onMoveMade();
        void onSolved();
    }

    public static final int[] PALETTE = {
            0xFFE53935, // red
            0xFF1E88E5, // blue
            0xFF43A047, // green
            0xFFFDD835, // yellow
            0xFF8E24AA, // purple
            0xFFFB8C00, // orange
            0xFF00ACC1, // cyan
            0xFFEC407A, // pink
            0xFF6D4C41, // brown
            0xFF9E9E9E, // grey
            0xFF7CB342, // lime
            0xFF3949AB, // indigo
            0xFFFFFFFF, // white (spare)
    };

    private GameState state;
    private final ArrayDeque<GameState> history = new ArrayDeque<>();
    private Listener listener;
    private int selected = -1;
    private boolean solvedNotified;

    // layout
    private RectF[] tubeRects = new RectF[0];
    private RectF[] hitRects = new RectF[0];
    private float ballR;
    private float gap;

    // move animation
    private ValueAnimator animator;
    private boolean animating;
    private int animFrom, animTo, animColor, animCount;
    private float animT;

    // hint highlight
    private int hintFrom = -1, hintTo = -1;
    private ValueAnimator hintAnimator;
    private float hintPulse;

    private final Paint tubeFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tubeStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        tubeFill.setColor(0x22FFFFFF);
        tubeStroke.setStyle(Paint.Style.STROKE);
        tubeStroke.setStrokeWidth(2.5f * density);
        shinePaint.setColor(0x66FFFFFF);
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeWidth(4f * density);
        hintPaint.setColor(0xFFFFD54F);
        setClickable(true);
    }

    public void setListener(Listener l) {
        this.listener = l;
    }

    public void setState(GameState s) {
        cancelAnimations();
        this.state = s;
        history.clear();
        selected = -1;
        solvedNotified = false;
        clearHint();
        computeLayout();
        invalidate();
    }

    public GameState getState() {
        return state;
    }

    public boolean canUndo() {
        return !history.isEmpty() && !animating && !solvedNotified;
    }

    public boolean undo() {
        if (!canUndo()) return false;
        state = history.pop();
        selected = -1;
        clearHint();
        invalidate();
        return true;
    }

    /** Adds an empty tube to the current position and to every undo step. */
    public void addEmptyTube() {
        if (state == null) return;
        finishAnimation();
        state.addEmptyTube();
        for (GameState h : history) h.addEmptyTube();
        selected = -1;
        clearHint();
        computeLayout();
        invalidate();
    }

    public boolean isBusy() {
        return animating || state == null || solvedNotified;
    }

    public void showHint(int from, int to) {
        clearHint();
        selected = -1;
        hintFrom = from;
        hintTo = to;
        hintAnimator = ValueAnimator.ofFloat(0f, 1f);
        hintAnimator.setDuration(600);
        hintAnimator.setRepeatCount(5);
        hintAnimator.setRepeatMode(ValueAnimator.REVERSE);
        hintAnimator.addUpdateListener(a -> {
            hintPulse = (float) a.getAnimatedValue();
            invalidate();
        });
        hintAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hintFrom = hintTo = -1;
                invalidate();
            }
        });
        hintAnimator.start();
    }

    private void clearHint() {
        if (hintAnimator != null) {
            hintAnimator.removeAllListeners();
            hintAnimator.cancel();
            hintAnimator = null;
        }
        hintFrom = hintTo = -1;
    }

    private void cancelAnimations() {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
            animator = null;
        }
        animating = false;
    }

    private void finishAnimation() {
        if (animator != null && animating) animator.end();
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimations();
        clearHint();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        computeLayout();
    }

    private float dp(float v) {
        return v * density;
    }

    private void computeLayout() {
        if (state == null || getWidth() == 0 || getHeight() == 0) return;
        int n = state.tubeCount();
        int cap = state.capacity;
        int rows = n <= 5 ? 1 : 2;
        int perRow = (n + rows - 1) / rows;

        float padX = dp(12), padY = dp(16);
        float availW = getWidth() - padX * 2;
        float availH = getHeight() - padY * 2;
        float slotW = availW / perRow;
        float tubeW = Math.min(slotW * 0.72f, dp(62));
        float rowGap = dp(28);

        float tubeH = 0, liftSpace = 0, totalH;
        for (int pass = 0; pass < 3; pass++) {
            ballR = tubeW * 0.40f;
            gap = ballR * 0.18f;
            tubeH = cap * (2 * ballR + gap) + gap + ballR * 0.5f;
            liftSpace = 2 * ballR + dp(14);
            totalH = rows * (liftSpace + tubeH) + (rows - 1) * rowGap;
            if (totalH <= availH) break;
            tubeW *= availH / totalH * 0.98f;
        }
        totalH = rows * (liftSpace + tubeH) + (rows - 1) * rowGap;
        float top = padY + (availH - totalH) / 2f;

        tubeRects = new RectF[n];
        hitRects = new RectF[n];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            int inRow = Math.min(perRow, n - idx);
            float rowTop = top + r * (liftSpace + tubeH + rowGap);
            float rowW = inRow * slotW;
            float startX = (getWidth() - rowW) / 2f;
            for (int k = 0; k < inRow; k++, idx++) {
                float cx = startX + slotW * (k + 0.5f);
                float tTop = rowTop + liftSpace;
                tubeRects[idx] = new RectF(cx - tubeW / 2f, tTop, cx + tubeW / 2f, tTop + tubeH);
                hitRects[idx] = new RectF(cx - slotW / 2f, rowTop, cx + slotW / 2f, tTop + tubeH + dp(8));
            }
        }
    }

    private float slotY(int tube, int index) {
        RectF r = tubeRects[tube];
        return r.bottom - gap - ballR - ballR * 0.25f - index * (2 * ballR + gap);
    }

    private float liftY(int tube) {
        return tubeRects[tube].top - ballR - dp(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (state == null || tubeRects.length != state.tubeCount()) return;
        float corner = ballR * 1.05f;

        for (int t = 0; t < state.tubeCount(); t++) {
            RectF r = tubeRects[t];
            boolean complete = state.isTubeComplete(t);
            tubeStroke.setColor(complete ? 0xFF66BB6A : 0xCCFFFFFF);
            canvas.drawRoundRect(r, corner, corner, tubeFill);
            canvas.drawRoundRect(r, corner, corner, tubeStroke);

            if (t == hintFrom || t == hintTo) {
                hintPaint.setAlpha((int) (90 + 165 * hintPulse));
                float o = dp(4);
                canvas.drawRoundRect(r.left - o, r.top - o, r.right + o, r.bottom + o, corner + o, corner + o, hintPaint);
            }

            int count = state.size(t);
            int hiddenTop = (animating && t == animTo) ? animCount : 0;
            for (int j = 0; j < count - hiddenTop; j++) {
                float y = slotY(t, j);
                if (t == selected && j == count - 1) y = liftY(t);
                drawBall(canvas, r.centerX(), y, state.get(t, j));
            }
        }

        if (animating) drawMovingBalls(canvas);
    }

    private void drawMovingBalls(Canvas canvas) {
        float sx = tubeRects[animFrom].centerX();
        float sy = liftY(animFrom);
        float ex = tubeRects[animTo].centerX();
        float ey = liftY(animTo);
        int destSize = state.size(animTo);
        if (animT < 0.6f) {
            float p = animT / 0.6f;
            float x = sx + (ex - sx) * p;
            float arc = (float) Math.sin(p * Math.PI) * dp(18);
            float y = sy + (ey - sy) * p - arc;
            drawBall(canvas, x, y, animColor);
        } else {
            float p = (animT - 0.6f) / 0.4f;
            for (int k = 0; k < animCount; k++) {
                int slot = destSize - animCount + k;
                float target = slotY(animTo, slot);
                drawBall(canvas, ex, ey + (target - ey) * p, animColor);
            }
        }
    }

    private void drawBall(Canvas canvas, float cx, float cy, int colorIndex) {
        ballPaint.setColor(PALETTE[colorIndex % PALETTE.length]);
        canvas.drawCircle(cx, cy, ballR, ballPaint);
        canvas.drawCircle(cx - ballR * 0.32f, cy - ballR * 0.32f, ballR * 0.28f, shinePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            handleTap(e.getX(), e.getY());
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleTap(float x, float y) {
        if (state == null || animating || solvedNotified) return;
        int tapped = -1;
        for (int i = 0; i < hitRects.length; i++) {
            if (hitRects[i].contains(x, y)) { tapped = i; break; }
        }
        if (tapped < 0) {
            selected = -1;
            invalidate();
            return;
        }
        if (selected < 0) {
            if (!state.isEmpty(tapped) && !state.isTubeComplete(tapped)) {
                selected = tapped;
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
        } else if (tapped == selected) {
            selected = -1;
        } else if (state.canMove(selected, tapped)) {
            doMove(selected, tapped);
        } else {
            selected = (!state.isEmpty(tapped) && !state.isTubeComplete(tapped)) ? tapped : -1;
        }
        invalidate();
    }

    private void doMove(int from, int to) {
        history.push(state.copy());
        if (history.size() > 200) history.removeLast();
        int color = state.top(from);
        int moved = state.move(from, to);
        selected = -1;
        clearHint();
        if (moved == 0) return;

        animFrom = from;
        animTo = to;
        animColor = color;
        animCount = moved;
        animT = 0f;
        animating = true;
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(260);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> {
            animT = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animating = false;
                animator = null;
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                invalidate();
                if (listener != null) listener.onMoveMade();
                if (state.isSolved() && !solvedNotified) {
                    solvedNotified = true;
                    if (listener != null) listener.onSolved();
                }
            }
        });
        animator.start();
    }
}
