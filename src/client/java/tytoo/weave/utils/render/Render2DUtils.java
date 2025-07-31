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
import tytoo.weave.style.ColorWave;

import java.awt.*;

@SuppressWarnings("unused")
public final class Render2DUtils {

    private Render2DUtils() {
    }

    private static BufferBuilder setupRender(ShaderProgramKey shaderProgramKey, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
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
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float x2 = x + width;
        float y2 = y + height;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, x, y2, 0).color(color.getRGB());
        buffer.vertex(matrix, x2, y2, 0).color(color.getRGB());
        buffer.vertex(matrix, x2, y, 0).color(color.getRGB());
        buffer.vertex(matrix, x, y, 0).color(color.getRGB());

        endRender(buffer);
    }

    public static void drawOutline(DrawContext context, float x, float y, float width, float height, float lineWidth, Color color) {
        drawOutline(context, x, y, width, height, lineWidth, color, true);
    }

    public static void drawOutline(DrawContext context, float x, float y, float width, float height, float lineWidth, Color color, boolean inside) {
        if (inside) {
            drawRect(context, x, y, width, lineWidth, color);
            drawRect(context, x, y + height - lineWidth, width, lineWidth, color);
            drawRect(context, x, y + lineWidth, lineWidth, height - (lineWidth * 2), color);
            drawRect(context, x + width - lineWidth, y + lineWidth, lineWidth, height - (lineWidth * 2), color);
        } else {
            drawRect(context, x - lineWidth, y - lineWidth, width + 2 * lineWidth, lineWidth, color);
            drawRect(context, x - lineWidth, y + height, width + 2 * lineWidth, lineWidth, color);
            drawRect(context, x - lineWidth, y, lineWidth, height, color);
            drawRect(context, x + width, y, lineWidth, height, color);
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

    public static void drawCircle(DrawContext context, float centerX, float centerY, float radius, Color color) {
        drawCircle(context, centerX, centerY, radius, 0, 360, color);
    }

    public static void drawCircle(DrawContext context, float centerX, float centerY, float radius, int startAngle, int endAngle, Color color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        int segments = Math.max(1, (int) (radius / 1.5 * Math.abs(endAngle - startAngle) / 90.0));

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, centerX, centerY, 0).color(color.getRGB());

        double startRad = Math.toRadians(startAngle);
        double endRad = Math.toRadians(endAngle);

        for (int i = 0; i <= segments; i++) {
            double angle = startRad + (i / (double) segments) * (endRad - startRad);
            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);
            buffer.vertex(matrix, x, y, 0).color(color.getRGB());
        }
        endRender(buffer);
    }

    public static void drawRoundedRect(DrawContext context, float x, float y, float width, float height, float radius, Color color) {
        if (radius <= 0) {
            drawRect(context, x, y, width, height, color);
            return;
        }
        radius = Math.min(radius, Math.min(width, height) / 2);

        float x2 = x + width;
        float y2 = y + height;

        drawRect(context, x + radius, y, width - 2 * radius, height, color);

        drawRect(context, x, y + radius, radius, height - 2 * radius, color);
        drawRect(context, x2 - radius, y + radius, radius, height - 2 * radius, color);

        drawCircle(context, x + radius, y + radius, radius, 180, 270, color);
        drawCircle(context, x2 - radius, y + radius, radius, 270, 360, color);
        drawCircle(context, x2 - radius, y2 - radius, radius, 0, 90, color);
        drawCircle(context, x + radius, y2 - radius, radius, 90, 180, color);
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
            Color interpolated = tytoo.weave.animation.Interpolators.COLOR.interpolate(color1, color2, progress);
            buffer.vertex(matrix, vertices[index].x, vertices[index].y, 0).color(interpolated.getRGB());
        }
        endRender(buffer);
    }

    public static void drawGradientRect(DrawContext context, float x, float y, float width, float height, ColorWave wave, float offset, float angleDegrees) {
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
            float progress = (projections[index] - min) / range;
            buffer.vertex(matrix, vertices[index].x, vertices[index].y, 0).color(wave.getColorAt(progress + offset).getRGB());
        }
        endRender(buffer);
    }
}