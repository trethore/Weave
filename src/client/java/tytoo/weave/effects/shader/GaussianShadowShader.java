package tytoo.weave.effects.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import tytoo.weave.WeaveCore;

import java.io.IOException;

public final class GaussianShadowShader {
    public static final ShaderProgramKey PROGRAM_KEY = new ShaderProgramKey(
            Identifier.of(WeaveCore.ID, "core/gaussian_shadow"),
            VertexFormats.POSITION_TEXTURE_COLOR,
            Defines.EMPTY
    );

    private static boolean preloadAttempted;

    private GaussianShadowShader() {
    }

    public static void preload(MinecraftClient client) {
        if (preloadAttempted) {
            return;
        }
        preloadAttempted = true;
        try {
            client.getShaderLoader().preload(client.getResourceManager(), PROGRAM_KEY);
        } catch (IOException | ShaderLoader.LoadException exception) {
            WeaveCore.LOGGER.error("Failed to preload gaussian shadow shader", exception);
        }
    }

    public static ShaderProgram bind() {
        return RenderSystem.setShader(PROGRAM_KEY);
    }

    public static boolean uploadUniforms(ShaderProgram shader,
                                         float outerWidth,
                                         float outerHeight,
                                         float baseWidth,
                                         float baseHeight,
                                         float cornerRadius,
                                         float blurRadius,
                                         float invTwoSigmaSq,
                                         float red,
                                         float green,
                                         float blue,
                                         float alpha) {
        GlUniform outerSize = shader.getUniform("OuterSize");
        GlUniform baseSize = shader.getUniform("BaseSize");
        GlUniform corner = shader.getUniform("CornerRadius");
        GlUniform blur = shader.getUniform("BlurRadius");
        GlUniform sigma = shader.getUniform("InvTwoSigmaSq");
        GlUniform color = shader.getUniform("ShadowColor");
        if (outerSize == null || baseSize == null || corner == null || blur == null || sigma == null || color == null) {
            return false;
        }
        outerSize.set(outerWidth, outerHeight);
        outerSize.upload();
        baseSize.set(baseWidth, baseHeight);
        baseSize.upload();
        corner.set(cornerRadius);
        corner.upload();
        blur.set(blurRadius);
        blur.upload();
        sigma.set(invTwoSigmaSq);
        sigma.upload();
        color.set(red, green, blue, alpha);
        color.upload();
        return true;
    }
}
