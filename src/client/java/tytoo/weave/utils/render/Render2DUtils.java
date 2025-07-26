package tytoo.weave.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;

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

    public static void drawRect(DrawContext context, float x, float y, float width, float height, Color color) {
        drawRect(x, y, width, height, color);
    }

    public static void drawRect(float x, float y, float width, float height, Color color) {
        float x2 = x + width;
        float y2 = y + height;

        BufferBuilder buffer = setupRender(ShaderProgramKeys.POSITION_COLOR, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        // getRGB gives ARGB i think
        buffer.vertex(x, y2, 0).color(color.getRGB());
        buffer.vertex(x2, y2, 0).color(color.getRGB());
        buffer.vertex(x2, y, 0).color(color.getRGB());
        buffer.vertex(x, y, 0).color(color.getRGB());

        endRender(buffer);
    }

}
