package tytoo.weave.effects.implementations;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;
import tytoo.weave.utils.Precision;

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

        GradientBatch batch = new GradientBatch(context);

        if (inside) {
            if (sides.top()) drawHorizontalEdge(batch, x, y, w, lw, y + lw * 0.5f, dirX, dirY, minProj, range, angleHorizontal);
            if (sides.bottom()) drawHorizontalEdge(batch, x, y + h - lw, w, lw, y + h - lw * 0.5f, dirX, dirY, minProj, range, angleHorizontal);
            float topOffset = sides.top() ? lw : 0f;
            float bottomOffset = sides.bottom() ? lw : 0f;
            float vertLen = h - (topOffset + bottomOffset);
            if (vertLen > 0f) {
                if (sides.left()) drawVerticalEdge(batch, x, y + topOffset, vertLen, lw, x + lw * 0.5f, dirX, dirY, minProj, range, angleVertical);
                if (sides.right()) drawVerticalEdge(batch, x + w - lw, y + topOffset, vertLen, lw, x + w - lw * 0.5f, dirX, dirY, minProj, range, angleVertical);
            }
        } else {
            if (sides.top()) drawHorizontalEdge(batch, x - lw, y - lw, w + lw * 2, lw, y - lw * 0.5f, dirX, dirY, minProj, range, angleHorizontal);
            if (sides.bottom()) drawHorizontalEdge(batch, x - lw, y + h, w + lw * 2, lw, y + h + lw * 0.5f, dirX, dirY, minProj, range, angleHorizontal);
            if (sides.left()) drawVerticalEdge(batch, x - lw, y, h, lw, x - lw * 0.5f, dirX, dirY, minProj, range, angleVertical);
            if (sides.right()) drawVerticalEdge(batch, x + w, y, h, lw, x + w + lw * 0.5f, dirX, dirY, minProj, range, angleVertical);
        }

        batch.flush();
    }

    private void drawHorizontalEdge(GradientBatch batch, float rectX, float rectY, float rectW, float rectH, float yEdge,
                                    float dirX, float dirY, float minProj, float range, float angle) {
        if (rectW <= 0 || rectH <= 0) return;

        float pA = dirX * rectX + dirY * yEdge;
        float pB = dirX * (rectX + rectW) + dirY * yEdge;

        if (Math.abs(pB - pA) < 1e-6f) {
            float s = (pA - minProj) / range;
            if (s < 0f) s = 0f;
            if (s > 1f) s = 1f;
            Color c = sampleWaveNonCyclic(colorWave.colors(), s);
            batch.addSolidRect(rectX, rectY, rectW, rectH, c);
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

            batch.addGradientRect(segX, rectY, segW, rectH, c0, c1, angle);
        }
    }

    private void drawVerticalEdge(GradientBatch batch, float rectX, float rectY, float rectH, float rectW, float xEdge,
                                  float dirX, float dirY, float minProj, float range, float angle) {
        if (rectW <= 0 || rectH <= 0) return;

        float pA = dirX * xEdge + dirY * rectY;
        float pB = dirX * xEdge + dirY * (rectY + rectH);

        if (Math.abs(pB - pA) < 1e-6f) {
            float s = (pA - minProj) / range;
            if (s < 0f) s = 0f;
            if (s > 1f) s = 1f;
            Color c = sampleWaveNonCyclic(colorWave.colors(), s);
            batch.addSolidRect(rectX, rectY, rectW, rectH, c);
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

            batch.addGradientRect(rectX, segY, rectW, segH, c0, c1, angle);
        }
    }

    private static void appendGradientRect(BufferBuilder buffer, Matrix4f matrix, float x, float y, float width, float height, Color color1, Color color2, float angleDegrees) {
        if (width <= 0f || height <= 0f) return;
        float angleRad = (float) Math.toRadians(90 - angleDegrees);
        Vector2f dir = new Vector2f((float) Math.cos(angleRad), (float) Math.sin(angleRad));
        Vector4f[] vertices = {
                new Vector4f(x, y, 0, 1),
                new Vector4f(x + width, y, 0, 1),
                new Vector4f(x + width, y + height, 0, 1),
                new Vector4f(x, y + height, 0, 1)
        };
        float[] projections = new float[4];
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            float p = dir.x * vertices[i].x + dir.y * vertices[i].y;
            projections[i] = p;
            if (p < min) min = p;
            if (p > max) max = p;
        }
        float range = max - min;
        if (Math.abs(range) < 0.0001f) range = 1f;
        int[] quadOrder = {3, 2, 1, 0};
        for (int index : quadOrder) {
            float progress = (projections[index] - min) / range;
            Color interpolated = Interpolators.COLOR.interpolate(color1, color2, progress);
            buffer.vertex(matrix, vertices[index].x, vertices[index].y, 0).color(interpolated.getRGB());
        }
    }

    private static void appendSolidRect(BufferBuilder buffer, Matrix4f matrix, float x, float y, float width, float height, Color color) {
        float snappedX = Precision.snapCoordinate(x);
        float snappedY = Precision.snapCoordinate(y);
        float snappedWidth = Precision.snapLength(width);
        float snappedHeight = Precision.snapLength(height);
        if (snappedWidth <= 0f || snappedHeight <= 0f) return;
        float x2 = snappedX + snappedWidth;
        float y2 = snappedY + snappedHeight;
        int rgb = color.getRGB();
        buffer.vertex(matrix, snappedX, y2, 0).color(rgb);
        buffer.vertex(matrix, x2, y2, 0).color(rgb);
        buffer.vertex(matrix, x2, snappedY, 0).color(rgb);
        buffer.vertex(matrix, snappedX, snappedY, 0).color(rgb);
    }

    private static final class GradientBatch {
        private final DrawContext context;
        private BufferBuilder buffer;
        private Matrix4f matrix;
        private boolean started;

        GradientBatch(DrawContext context) {
            this.context = context;
        }

        void addSolidRect(float x, float y, float width, float height, Color color) {
            ensureStarted();
            appendSolidRect(buffer, matrix, x, y, width, height, color);
        }

        void addGradientRect(float x, float y, float width, float height, Color color1, Color color2, float angleDegrees) {
            ensureStarted();
            appendGradientRect(buffer, matrix, x, y, width, height, color1, color2, angleDegrees);
        }

        void flush() {
            if (!started) return;
            BuiltBuffer builtBuffer = buffer.end();
            if (builtBuffer != null) {
                BufferRenderer.drawWithGlobalProgram(builtBuffer);
            }
            RenderSystem.disableBlend();
            started = false;
            buffer = null;
        }

        private void ensureStarted() {
            if (started) return;
            started = true;
            RenderSystem.enableBlend();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
            matrix = context.getMatrices().peek().getPositionMatrix();
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        }
    }

    public enum Direction {
        LEFT_TO_RIGHT,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        BOTTOM_LEFT_TO_TOP_RIGHT
    }
}
