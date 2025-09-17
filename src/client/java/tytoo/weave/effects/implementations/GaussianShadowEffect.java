package tytoo.weave.effects.implementations;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import tytoo.weave.component.Component;
import tytoo.weave.effects.Effect;
import tytoo.weave.effects.shader.GaussianShadowShader;

import java.awt.*;

public record GaussianShadowEffect(Color color,
                                   float offsetX,
                                   float offsetY,
                                   float blurRadius,
                                   float spread,
                                   float cornerRadius) implements Effect {

    public GaussianShadowEffect {
        blurRadius = Math.max(0f, blurRadius);
        spread = Math.max(0f, spread);
    }

    @Override
    public void beforeDraw(DrawContext context, Component<?> component) {
        float componentWidth = component.getWidth();
        float componentHeight = component.getHeight();
        if (componentWidth <= 0f || componentHeight <= 0f) {
            return;
        }
        float alpha = color.getAlpha() / 255f;
        if (alpha <= 0f) {
            return;
        }
        float baseWidth = componentWidth + spread * 2f;
        float baseHeight = componentHeight + spread * 2f;
        if (baseWidth <= 0f || baseHeight <= 0f) {
            return;
        }
        float blurExtent = blurRadius * 1.5f;
        float outerPadding = spread + blurExtent;
        float outerWidth = componentWidth + outerPadding * 2f;
        float outerHeight = componentHeight + outerPadding * 2f;
        if (outerWidth <= 0f || outerHeight <= 0f) {
            return;
        }
        float x0 = component.getLeft() + offsetX - outerPadding;
        float y0 = component.getTop() + offsetY - outerPadding;
        float corner = cornerRadius + spread;
        float maxCorner = Math.min(baseWidth, baseHeight) * 0.5f;
        if (corner > maxCorner) {
            corner = maxCorner;
        }
        if (corner < 0f) {
            corner = 0f;
        }
        float sigma = blurRadius > 0f ? Math.max(0.0001f, blurRadius / 3f) : 0f;
        float invTwoSigmaSq = sigma > 0f ? 1f / (2f * sigma * sigma) : 0f;
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        ShaderProgram shader = GaussianShadowShader.bind();
        if (shader == null) {
            RenderSystem.disableBlend();
            return;
        }
        shader.bind();
        boolean uploaded = GaussianShadowShader.uploadUniforms(
                shader,
                outerWidth,
                outerHeight,
                baseWidth,
                baseHeight,
                corner,
                blurRadius,
                invTwoSigmaSq,
                red,
                green,
                blue,
                alpha
        );
        if (!uploaded) {
            RenderSystem.disableBlend();
            return;
        }
        float x1 = x0 + outerWidth;
        float y1 = y0 + outerHeight;
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, x0, y1, 0f).color(255, 255, 255, 255).texture(0f, 1f);
        buffer.vertex(matrix, x1, y1, 0f).color(255, 255, 255, 255).texture(1f, 1f);
        buffer.vertex(matrix, x1, y0, 0f).color(255, 255, 255, 255).texture(1f, 0f);
        buffer.vertex(matrix, x0, y0, 0f).color(255, 255, 255, 255).texture(0f, 0f);
        BuiltBuffer builtBuffer = buffer.end();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }
        RenderSystem.disableBlend();
    }
}
