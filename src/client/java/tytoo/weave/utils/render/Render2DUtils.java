package tytoo.weave.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import tytoo.weave.animation.Interpolators;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.OutlineSides;
import tytoo.weave.utils.Precision;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("unused")
public final class Render2DUtils {

    private static final ThreadLocal<Deque<Integer>> AA_STACK = ThreadLocal.withInitial(() -> {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addLast(9);
        return d;
    });

    private Render2DUtils() {
    }

    public static void pushAntialiasSegmentsPer90(int segments) {
        if (segments < 1) segments = 1;
        AA_STACK.get().addLast(segments);
    }

    public static void popAntialiasSegmentsPer90() {
        Deque<Integer> d = AA_STACK.get();
        if (d.size() > 1) d.removeLast();
    }

    private static int currentAntialiasSegmentsPer90() {
        Integer v = AA_STACK.get().peekLast();
        return v == null ? 9 : Math.max(1, v);
    }

    private static BufferBuilder setupRender(ShaderProgramKey shaderProgramKey, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(shaderProgramKey);
        return Tessellator.getInstance().begin(drawMode, vertexFormat);
    }

    private static void endRender(BufferBuilder buffer) {
        BuiltBuffer builtBuffer = buffer.end();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }
        RenderSystem.disableBlend();
    }

    public static void enableScissor(DrawContext context, int x, int y, int width, int height) {
        context.enableScissor(x, y, x + width, y + height);
    }

    public static void disableScissor(DrawContext context) {
        context.disableScissor();
    }

    public static void drawRect(DrawContext context, float x, float y, float width, float height, Color color) {
        float snappedX = Precision.snapCoordinate(x);
        float snappedY = Precision.snapCoordinate(y);
        float snappedWidth = Precision.snapLength(width);
        float snappedHeight = Precision.snapLength(height);
        if (snappedWidth <= 0f || snappedHeight <= 0f) {
            return;
        }

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x2 = snappedX + snappedWidth;
        float y2 = snappedY + snappedHeight;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, snappedX, y2, 0).color(color.getRGB());
        buffer.vertex(matrix, x2, y2, 0).color(color.getRGB());
        buffer.vertex(matrix, x2, snappedY, 0).color(color.getRGB());
        buffer.vertex(matrix, snappedX, snappedY, 0).color(color.getRGB());

        endRender(buffer);
    }

    private static void drawHLine(DrawContext context, float x, float y, float length, float thickness, Color color) {
        drawRect(context, x, y, length, thickness, color);
    }

    private static void drawVLine(DrawContext context, float x, float y, float thickness, float length, Color color) {
        drawRect(context, x, y, thickness, length, color);
    }

    public static void drawOutline(DrawContext context, float x, float y, float width, float height, float lineWidth, Color color) {
        drawOutline(context, x, y, width, height, lineWidth, color, true);
    }

    public static void drawOutline(DrawContext context, float x, float y, float width, float height, float lineWidth, Color color, boolean inside) {
        drawOutline(context, x, y, width, height, lineWidth, color, inside, OutlineSides.all());
    }

    public static void drawOutline(DrawContext context,
                                   float x, float y, float width, float height,
                                   float lineWidth, Color color, boolean inside,
                                   OutlineSides sides) {
        if (sides == null) sides = OutlineSides.all();
        float snappedLineWidth = Precision.snapLength(lineWidth);
        if (snappedLineWidth <= 0f) {
            return;
        }
        float snappedX = Precision.snapCoordinate(x);
        float snappedY = Precision.snapCoordinate(y);
        float snappedWidth = Precision.snapLength(width);
        float snappedHeight = Precision.snapLength(height);
        if (snappedWidth <= 0f || snappedHeight <= 0f) {
            return;
        }
        if (inside) {
            if (sides.top()) drawHLine(context, snappedX, snappedY, snappedWidth, snappedLineWidth, color);
            if (sides.bottom())
                drawHLine(context, snappedX, snappedY + snappedHeight - snappedLineWidth, snappedWidth, snappedLineWidth, color);

            float topOffset = sides.top() ? snappedLineWidth : 0f;
            float bottomOffset = sides.bottom() ? snappedLineWidth : 0f;
            float vertLen = snappedHeight - (topOffset + bottomOffset);
            if (vertLen > 0f) {
                if (sides.left()) drawVLine(context, snappedX, snappedY + topOffset, snappedLineWidth, vertLen, color);
                if (sides.right())
                    drawVLine(context, snappedX + snappedWidth - snappedLineWidth, snappedY + topOffset, snappedLineWidth, vertLen, color);
            }
        } else {
            if (sides.top())
                drawHLine(context, snappedX - snappedLineWidth, snappedY - snappedLineWidth, snappedWidth + 2 * snappedLineWidth, snappedLineWidth, color);
            if (sides.bottom())
                drawHLine(context, snappedX - snappedLineWidth, snappedY + snappedHeight, snappedWidth + 2 * snappedLineWidth, snappedLineWidth, color);
            if (sides.left())
                drawVLine(context, snappedX - snappedLineWidth, snappedY, snappedLineWidth, snappedHeight, color);
            if (sides.right())
                drawVLine(context, snappedX + snappedWidth, snappedY, snappedLineWidth, snappedHeight, color);
        }
    }

    public static void drawRoundedOutline(DrawContext context, float x, float y, float width, float height, float radius, float lineWidth, Color color) {
        float snappedLineWidth = Precision.snapLength(lineWidth);
        if (snappedLineWidth <= 0f) {
            return;
        }
        float snappedRadius = Precision.snapLength(radius);
        if (snappedRadius <= 0f) {
            drawOutline(context, x, y, width, height, snappedLineWidth, color, true);
            return;
        }
        float snappedX = Precision.snapCoordinate(x);
        float snappedY = Precision.snapCoordinate(y);
        float snappedWidth = Precision.snapLength(width);
        float snappedHeight = Precision.snapLength(height);
        if (snappedWidth <= 0f || snappedHeight <= 0f) {
            return;
        }
        int thickness = Math.max(1, Math.round(snappedLineWidth));
        for (int inset = 0; inset < thickness; inset++) {
            float r = Math.max(0, snappedRadius - (float) inset);
            drawRoundedRect(context,
                    snappedX + (float) inset,
                    snappedY + (float) inset,
                    snappedWidth - (float) inset * 2,
                    snappedHeight - (float) inset * 2,
                    r,
                    color);
        }
    }

    public static void drawImage(DrawContext context, Identifier id, float x1, float y1, float x2, float y2, int rotation, boolean parity, Color color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        int[][] texCoords = {{0, 1}, {1, 1}, {1, 0}, {0, 0}};
        for (int i = 0; i < rotation % 4; i++) {
            int temp1 = texCoords[3][0], temp2 = texCoords[3][1];
            texCoords[3][0] = texCoords[2][0];
            texCoords[3][1] = texCoords[2][1];
            texCoords[2][0] = texCoords[1][0];
            texCoords[2][1] = texCoords[1][1];
            texCoords[1][0] = texCoords[0][0];
            texCoords[1][1] = texCoords[0][1];
            texCoords[0][0] = temp1;
            texCoords[0][1] = temp2;
        }
        if (parity) {
            int temp1 = texCoords[1][0];
            texCoords[1][0] = texCoords[0][0];
            texCoords[0][0] = temp1;
            temp1 = texCoords[3][0];
            texCoords[3][0] = texCoords[2][0];
            texCoords[2][0] = temp1;
        }
        RenderSystem.setShaderTexture(0, id);
        BufferBuilder bufferbuilder = setupRender(ShaderProgramKeys.POSITION_TEX_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferbuilder.vertex(matrix, x1, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[0][0], texCoords[0][1]);
        bufferbuilder.vertex(matrix, x2, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[1][0], texCoords[1][1]);
        bufferbuilder.vertex(matrix, x2, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[2][0], texCoords[2][1]);
        bufferbuilder.vertex(matrix, x1, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[3][0], texCoords[3][1]);
        endRender(bufferbuilder);
    }

    public static void drawLine(DrawContext context, Vector2f p1, Vector2f p2, float thickness, Color color) {
        Vector2f dir = new Vector2f(p2).sub(p1);
        if (dir.lengthSquared() == 0) return;
        dir.normalize();
        Vector2f perp = new Vector2f(-dir.y, dir.x).mul(thickness / 2f);

        Vector2f v1 = new Vector2f(p1).add(perp);
        Vector2f v2 = new Vector2f(p2).add(perp);
        Vector2f v3 = new Vector2f(p2).sub(perp);
        Vector2f v4 = new Vector2f(p1).sub(perp);

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int colorRgb = color.getRGB();
        buffer.vertex(matrix, v1.x, v1.y, 0).color(colorRgb);
        buffer.vertex(matrix, v2.x, v2.y, 0).color(colorRgb);
        buffer.vertex(matrix, v3.x, v3.y, 0).color(colorRgb);
        buffer.vertex(matrix, v4.x, v4.y, 0).color(colorRgb);

        endRender(buffer);
    }

    public static void drawLine(DrawContext context, Vector2f start, float angleDegrees, float length, float thickness, Color color) {
        float angleRad = (float) Math.toRadians(angleDegrees);
        float dx = (float) Math.cos(angleRad) * length;
        float dy = (float) Math.sin(angleRad) * length;
        Vector2f end = new Vector2f(start.x + dx, start.y + dy);
        drawLine(context, start, end, thickness, color);
    }

    public static void drawTriangle(DrawContext context, Vector2f p1, Vector2f p2, Vector2f p3, Color color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        int colorRgb = color.getRGB();
        buffer.vertex(matrix, p1.x, p1.y, 0).color(colorRgb);
        buffer.vertex(matrix, p3.x, p3.y, 0).color(colorRgb);
        buffer.vertex(matrix, p2.x, p2.y, 0).color(colorRgb);
        endRender(buffer);
    }

    public static void drawTriangle(DrawContext context, float x, float y, float width, float height, boolean pointingUp, Color color) {
        float right = x + width;
        float bottom = y + height;

        float margin = Math.min(width, height) * 0.25f;

        if (pointingUp) {
            Vector2f apex = new Vector2f((x + right) * 0.5f, y + margin);
            Vector2f baseLeft = new Vector2f(x + margin, bottom - margin);
            Vector2f baseRight = new Vector2f(right - margin, bottom - margin);
            drawTriangle(context, apex, baseRight, baseLeft, color);
        } else {
            Vector2f apex = new Vector2f((x + right) * 0.5f, bottom - margin);
            Vector2f baseLeft = new Vector2f(x + margin, y + margin);
            Vector2f baseRight = new Vector2f(right - margin, y + margin);
            drawTriangle(context, apex, baseLeft, baseRight, color);
        }
    }

    public static void drawCircle(DrawContext context, float centerX, float centerY, float radius, Color color) {
        drawCircle(context, centerX, centerY, radius, 0, 360, color);
    }

    public static void drawCircle(DrawContext context, float centerX, float centerY, float radius, int startAngle, int endAngle, Color color) {
        float snappedRadius = Precision.snapLength(radius);
        if (snappedRadius <= 0f) {
            return;
        }
        float snappedCenterX = Precision.snapCoordinate(centerX);
        float snappedCenterY = Precision.snapCoordinate(centerY);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        int angleRange = Math.abs(endAngle - startAngle);
        int aaPer90 = currentAntialiasSegmentsPer90();
        float perDegree = aaPer90 / 90.0f;
        int segmentsAA = Math.max(8, Math.round(angleRange * perDegree));
        int segmentsBase = Math.max(8, (int) Math.ceil(angleRange / 10.0));
        int segments = Math.max(segmentsAA, segmentsBase);

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        double startRad = Math.toRadians(startAngle);
        double endRad = Math.toRadians(endAngle);
        int argb = color.getRGB();

        for (int i = 0; i < segments; i++) {
            double angle1 = startRad + (i / (double) segments) * (endRad - startRad);
            double angle2 = startRad + ((i + 1) / (double) segments) * (endRad - startRad);

            float x1 = snappedCenterX + (float) (Math.cos(angle1) * snappedRadius);
            float y1 = snappedCenterY + (float) (Math.sin(angle1) * snappedRadius);
            float x2 = snappedCenterX + (float) (Math.cos(angle2) * snappedRadius);
            float y2 = snappedCenterY + (float) (Math.sin(angle2) * snappedRadius);

            buffer.vertex(matrix, snappedCenterX, snappedCenterY, 0).color(argb);
            buffer.vertex(matrix, x2, y2, 0).color(argb);
            buffer.vertex(matrix, x1, y1, 0).color(argb);
        }

        endRender(buffer);
    }


    public static void drawRoundedRect(DrawContext context, float x, float y, float width, float height, float radius, Color color) {
        float snappedRadius = Precision.snapLength(radius);
        if (snappedRadius <= 0f) {
            drawRect(context, x, y, width, height, color);
            return;
        }
        float snappedX = Precision.snapCoordinate(x);
        float snappedY = Precision.snapCoordinate(y);
        float snappedWidth = Precision.snapLength(width);
        float snappedHeight = Precision.snapLength(height);
        if (snappedWidth <= 0f || snappedHeight <= 0f) {
            return;
        }

        float maxRadius = Math.min(snappedWidth, snappedHeight) / 2f;
        float radiusValue = Math.min(snappedRadius, maxRadius);

        float x2 = snappedX + snappedWidth;
        float y2 = snappedY + snappedHeight;

        drawRect(context, snappedX + radiusValue, snappedY, snappedWidth - 2 * radiusValue, snappedHeight, color);

        drawRect(context, snappedX, snappedY + radiusValue, radiusValue, snappedHeight - 2 * radiusValue, color);
        drawRect(context, x2 - radiusValue, snappedY + radiusValue, radiusValue, snappedHeight - 2 * radiusValue, color);

        drawCircle(context, snappedX + radiusValue, snappedY + radiusValue, radiusValue, 180, 270, color);
        drawCircle(context, x2 - radiusValue, snappedY + radiusValue, radiusValue, 270, 360, color);
        drawCircle(context, x2 - radiusValue, y2 - radiusValue, radiusValue, 0, 90, color);
        drawCircle(context, snappedX + radiusValue, y2 - radiusValue, radiusValue, 90, 180, color);
    }

    public static void drawGradientRect(DrawContext context, float x, float y, float width, float height, Color color1, Color color2, float angleDegrees) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        float angleRad = (float) Math.toRadians(90 - angleDegrees);
        Vector2f dir = new Vector2f((float) Math.cos(angleRad), (float) Math.sin(angleRad));

        Vector4f[] vertices = {
                new Vector4f(x, y, 0, 1),
                new Vector4f(x + width, y, 0, 1),
                new Vector4f(x + width, y + height, 0, 1),
                new Vector4f(x, y + height, 0, 1)
        };

        float[] projections = new float[4];
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;

        for (int i = 0; i < 4; i++) {
            projections[i] = dir.x * vertices[i].x + dir.y * vertices[i].y;
            min = Math.min(min, projections[i]);
            max = Math.max(max, projections[i]);
        }

        float range = max - min;
        if (Math.abs(range) < 0.0001f) range = 1;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int[] quadOrder = {3, 2, 1, 0};
        for (int index : quadOrder) {
            float progress = (projections[index] - min) / range;
            Color interpolated = Interpolators.COLOR.interpolate(color1, color2, progress);
            buffer.vertex(matrix, vertices[index].x, vertices[index].y, 0).color(interpolated.getRGB());
        }
        endRender(buffer);
    }

    public static void drawGradientRect(DrawContext context, float x, float y, float width, float height, ColorWave wave, float cyclicOffset, float angleDegrees) {
        if (wave == null || wave.colors().isEmpty()) return;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        float angleRad = (float) Math.toRadians(90 - angleDegrees);
        Vector2f dir = new Vector2f((float) Math.cos(angleRad), (float) Math.sin(angleRad));

        Vector4f[] vertices = {
                new Vector4f(x, y, 0, 1),
                new Vector4f(x + width, y, 0, 1),
                new Vector4f(x + width, y + height, 0, 1),
                new Vector4f(x, y + height, 0, 1)
        };

        float[] projections = new float[4];
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;

        for (int i = 0; i < 4; i++) {
            projections[i] = dir.x * vertices[i].x + dir.y * vertices[i].y;
            min = Math.min(min, projections[i]);
            max = Math.max(max, projections[i]);
        }

        float range = max - min;
        if (Math.abs(range) < 0.0001f) range = 1;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        int[] quadOrder = {3, 2, 1, 0};
        for (int index : quadOrder) {
            float pos = (projections[index] - min) / range;
            if (pos >= 1.0f) pos = Math.nextDown(1.0f);
            if (pos <= 0.0f) pos = 0.0f;
            double total = pos + cyclicOffset;
            float t = (float) (total - Math.floor(total));

            int count = wave.colors().size();
            int idx1;
            int idx2;
            float local;
            if (count <= 1) {
                idx1 = 0;
                idx2 = 0;
                local = 0f;
            } else {
                float scaled = t * (count - 1);
                idx1 = (int) Math.floor(scaled);
                if (idx1 >= count - 1) idx1 = count - 2;
                idx2 = Math.min(idx1 + 1, count - 1);
                local = scaled - idx1;
            }
            Color c1 = wave.colors().get(idx1);
            Color c2 = wave.colors().get(idx2);
            Color interpolated = Interpolators.COLOR.interpolate(c1, c2, local);

            buffer.vertex(matrix, vertices[index].x, vertices[index].y, 0).color(interpolated.getRGB());
        }
        endRender(buffer);
    }
}
