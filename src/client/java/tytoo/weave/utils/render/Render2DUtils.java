package tytoo.weave.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

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
        drawRect(x, y, width, height, color);
    }

    public static void drawOutline(DrawContext context, float x, float y, float width, float height, float lineWidth, Color color) {
        drawRect(x, y, width, lineWidth, color);
        drawRect(x, y + height - lineWidth, width, lineWidth, color);
        drawRect(x, y + lineWidth, lineWidth, height - (lineWidth * 2), color);
        drawRect(x + width - lineWidth, y + lineWidth, lineWidth, height - (lineWidth * 2), color);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        float x2 = x + width;
        float y2 = y + height;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(x, y2, 0).color(color.getRGB());
        buffer.vertex(x2, y2, 0).color(color.getRGB());
        buffer.vertex(x2, y, 0).color(color.getRGB());
        buffer.vertex(x, y, 0).color(color.getRGB());

        endRender(buffer);
    }

    public static void drawImage(Identifier id, float x1, float y1, float x2, float y2, int rotation, boolean parity, Color color) {
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
        bufferbuilder.vertex(x1, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[0][0], texCoords[0][1]);
        bufferbuilder.vertex(x2, y2, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[1][0], texCoords[1][1]);
        bufferbuilder.vertex(x2, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[2][0], texCoords[2][1]);
        bufferbuilder.vertex(x1, y1, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).texture(texCoords[3][0], texCoords[3][1]);
        endRender(bufferbuilder);
    }

}