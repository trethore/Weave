package tytoo.weave.effects.implementations;

import net.minecraft.client.gui.DrawContext;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GradientOutlineEffect implements Effect {
    private float width;
    private boolean inside;
    private float angleDegrees;
    private ColorWave colorWave;
    private OutlineSides sides = OutlineSides.all();

    public GradientOutlineEffect(List<Color> colors, float width) {
        this(colors, width, true, Direction.LEFT_TO_RIGHT);
    }

    public GradientOutlineEffect(List<Color> colors, float width, boolean inside, Direction direction) {
        this(new ColorWave(colors, 0f), width, inside, direction);
    }

    public GradientOutlineEffect(ColorWave wave, float width, boolean inside, Direction direction) {
        this.colorWave = wave;
        this.width = width;
        this.inside = inside;
        this.angleDegrees = switch (direction) {
            case LEFT_TO_RIGHT -> 90f;
            case TOP_LEFT_TO_BOTTOM_RIGHT -> 45f;
            case BOTTOM_LEFT_TO_TOP_RIGHT -> 135f;
        };
    }

    public GradientOutlineEffect(ColorWave wave, float width, boolean inside, Direction direction, OutlineSides sides) {
        this(wave, width, inside, direction);
        this.sides = sides == null ? OutlineSides.all() : sides;
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        return Math.min(v, 1f);
    }

    private static Color sampleWaveNonCyclic(List<Color> colors, float t) {
        if (colors == null || colors.isEmpty()) return Color.WHITE;
        if (colors.size() == 1) return colors.getFirst();
        if (t <= 0f) return colors.getFirst();
        if (t >= 1f) return colors.getLast();
        float scaled = t * (colors.size() - 1);
        int i = (int) Math.floor(scaled);
        if (i >= colors.size() - 1) return colors.getLast();
        float f = scaled - i;
        Color c0 = colors.get(i);
        Color c1 = colors.get(i + 1);
        return Interpolators.COLOR.interpolate(c0, c1, f);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public ColorWave getColorWave() {
        return colorWave;
    }

    public void setColorWave(ColorWave colorWave) {
        this.colorWave = colorWave;
    }

    public float getAngleDegrees() {
        return angleDegrees;
    }

    public void setAngleDegrees(float angleDegrees) {
        this.angleDegrees = angleDegrees;
    }

    public OutlineSides getSides() {
        return sides;
    }

    public void setSides(OutlineSides sides) {
        this.sides = sides == null ? OutlineSides.all() : sides;
    }

    @Override
    public void afterDraw(DrawContext context, Component<?> component) {
        if (colorWave == null || colorWave.colors().isEmpty()) return;

        float x = component.getLeft();
        float y = component.getTop();
        float w = component.getWidth();
        float h = component.getHeight();
        float lw = Math.max(1f, this.width);
        float angleHorizontal = this.angleDegrees;
        float angleVertical = this.angleDegrees - 90f;


        float angleRad = (float) Math.toRadians(90 - this.angleDegrees);
        float dirX = (float) Math.cos(angleRad);
        float dirY = (float) Math.sin(angleRad);

        float minProj = Float.MAX_VALUE;
        float maxProj = -Float.MAX_VALUE;
        float[][] corners = new float[][]{
                {x, y},
                {x + w, y},
                {x + w, y + h},
                {x, y + h}
        };
        for (float[] c : corners) {
            float p = dirX * c[0] + dirY * c[1];
            if (p < minProj) minProj = p;
            if (p > maxProj) maxProj = p;
        }
        float range = maxProj - minProj;
        if (Math.abs(range) < 1e-6f) range = 1f;

        if (inside) {
            if (sides.top()) drawHorizontalEdge(context, x, y, w, lw, y + lw * 0.5f, dirX, dirY, minProj, range, 90f);
            if (sides.bottom())
                drawHorizontalEdge(context, x, y + h - lw, w, lw, y + h - lw * 0.5f, dirX, dirY, minProj, range, 90f);

            float topOffset = sides.top() ? lw : 0f;
            float bottomOffset = sides.bottom() ? lw : 0f;
            float vertLen = h - (topOffset + bottomOffset);
            if (vertLen > 0f) {
                if (sides.left())
                    drawVerticalEdge(context, x, y + topOffset, vertLen, lw, x + lw * 0.5f, dirX, dirY, minProj, range, 0f);
                if (sides.right())
                    drawVerticalEdge(context, x + w - lw, y + topOffset, vertLen, lw, x + w - lw * 0.5f, dirX, dirY, minProj, range, 0f);
            }
        } else {
            if (sides.top())
                drawHorizontalEdge(context, x - lw, y - lw, w + lw * 2, lw, y - lw * 0.5f, dirX, dirY, minProj, range, 90f);
            if (sides.bottom())
                drawHorizontalEdge(context, x - lw, y + h, w + lw * 2, lw, y + h + lw * 0.5f, dirX, dirY, minProj, range, 90f);
            if (sides.left())
                drawVerticalEdge(context, x - lw, y, h, lw, x - lw * 0.5f, dirX, dirY, minProj, range, 0f);
            if (sides.right())
                drawVerticalEdge(context, x + w, y, h, lw, x + w + lw * 0.5f, dirX, dirY, minProj, range, 0f);
        }
    }

    private void drawHorizontalEdge(DrawContext context, float rectX, float rectY, float rectW, float rectH, float yEdge,
                                    float dirX, float dirY, float minProj, float range, float angle) {
        if (rectW <= 0 || rectH <= 0) return;

        float pA = dirX * rectX + dirY * yEdge;
        float pB = dirX * (rectX + rectW) + dirY * yEdge;

        if (Math.abs(pB - pA) < 1e-6f) {
            float s = (pA - minProj) / range;
            if (s < 0f) s = 0f;
            if (s > 1f) s = 1f;
            Color c = sampleWaveNonCyclic(colorWave.colors(), s);
            Render2DUtils.drawRect(context, rectX, rectY, rectW, rectH, c);
            return;
        }

        float sStart = (pA - minProj) / range;
        float sEnd = (pB - minProj) / range;
        float sMin = Math.min(sStart, sEnd);
        float sMax = Math.max(sStart, sEnd);

        int n = Math.max(2, colorWave.colors().size());
        float[] stops = new float[n];
        for (int i = 0; i < n; i++) stops[i] = i / (float) (n - 1);

        ArrayList<Float> sBoundaries = new ArrayList<>();
        sBoundaries.add(sStart);
        sBoundaries.add(sEnd);
        for (int i = 1; i < n - 1; i++) {
            float v = stops[i];
            if (v >= sMin && v <= sMax) sBoundaries.add(v);
        }
        sBoundaries.sort(Float::compare);

        for (int i = 0; i < sBoundaries.size() - 1; i++) {
            float s0 = sBoundaries.get(i);
            float s1 = sBoundaries.get(i + 1);
            if (Math.abs(s1 - s0) < 1e-6f) continue;

            float p0 = minProj + s0 * range;
            float p1 = minProj + s1 * range;
            float t0 = (p0 - pA) / (pB - pA);
            float t1 = (p1 - pA) / (pB - pA);
            if (t0 > t1) {
                float tmp = t0;
                t0 = t1;
                t1 = tmp;
                float ts = s0;
                s0 = s1;
                s1 = ts;
            }
            if (t1 <= 0f || t0 >= 1f) continue;
            if (t0 < 0f) t0 = 0f;
            if (t1 > 1f) t1 = 1f;

            float segX = rectX + rectW * t0;
            float segW = rectW * (t1 - t0);
            if (segW <= 0f) continue;

            Color c0 = sampleWaveNonCyclic(colorWave.colors(), clamp01(s0));
            Color c1 = sampleWaveNonCyclic(colorWave.colors(), clamp01(s1));

            Render2DUtils.drawGradientRect(context, segX, rectY, segW, rectH, c0, c1, angle);
        }
    }

    private void drawVerticalEdge(DrawContext context, float rectX, float rectY, float rectH, float rectW, float xEdge,
                                  float dirX, float dirY, float minProj, float range, float angle) {
        if (rectW <= 0 || rectH <= 0) return;

        float pA = dirX * xEdge + dirY * rectY;
        float pB = dirX * xEdge + dirY * (rectY + rectH);

        if (Math.abs(pB - pA) < 1e-6f) {
            float s = (pA - minProj) / range;
            if (s < 0f) s = 0f;
            if (s > 1f) s = 1f;
            Color c = sampleWaveNonCyclic(colorWave.colors(), s);
            Render2DUtils.drawRect(context, rectX, rectY, rectW, rectH, c);
            return;
        }

        float sStart = (pA - minProj) / range;
        float sEnd = (pB - minProj) / range;
        float sMin = Math.min(sStart, sEnd);
        float sMax = Math.max(sStart, sEnd);

        int n = Math.max(2, colorWave.colors().size());
        float[] stops = new float[n];
        for (int i = 0; i < n; i++) stops[i] = i / (float) (n - 1);

        ArrayList<Float> sBoundaries = new ArrayList<>();
        sBoundaries.add(sStart);
        sBoundaries.add(sEnd);
        for (int i = 1; i < n - 1; i++) {
            float v = stops[i];
            if (v >= sMin && v <= sMax) sBoundaries.add(v);
        }
        sBoundaries.sort(Float::compare);

        for (int i = 0; i < sBoundaries.size() - 1; i++) {
            float s0 = sBoundaries.get(i);
            float s1 = sBoundaries.get(i + 1);
            if (Math.abs(s1 - s0) < 1e-6f) continue;

            float p0 = minProj + s0 * range;
            float p1 = minProj + s1 * range;
            float t0 = (p0 - pA) / (pB - pA);
            float t1 = (p1 - pA) / (pB - pA);
            if (t0 > t1) {
                float tmp = t0;
                t0 = t1;
                t1 = tmp;
                float ts = s0;
                s0 = s1;
                s1 = ts;
            }
            if (t1 <= 0f || t0 >= 1f) continue;
            if (t0 < 0f) t0 = 0f;
            if (t1 > 1f) t1 = 1f;

            float segY = rectY + rectH * t0;
            float segH = rectH * (t1 - t0);
            if (segH <= 0f) continue;

            Color c0 = sampleWaveNonCyclic(colorWave.colors(), clamp01(s0));
            Color c1 = sampleWaveNonCyclic(colorWave.colors(), clamp01(s1));

            Render2DUtils.drawGradientRect(context, rectX, segY, rectW, segH, c0, c1, angle);
        }
    }

    public enum Direction {
        LEFT_TO_RIGHT,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        BOTTOM_LEFT_TO_TOP_RIGHT
    }
}
