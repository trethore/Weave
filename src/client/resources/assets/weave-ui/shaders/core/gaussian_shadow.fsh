#version 150

in vec2 fragUv;

uniform vec2 OuterSize;
uniform vec2 BaseSize;
uniform float CornerRadius;
uniform float BlurRadius;
uniform float InvTwoSigmaSq;
uniform vec4 ShadowColor;
uniform vec4 ColorModulator;

out vec4 fragColor;

float roundedRectSdf(vec2 point, vec2 halfSize, float radius) {
    float effectiveRadius = min(radius, min(halfSize.x, halfSize.y));
    vec2 adjustedHalfSize = max(halfSize - vec2(effectiveRadius), vec2(0.0));
    vec2 d = abs(point) - adjustedHalfSize;
    vec2 maxD = max(d, vec2(0.0));
    float outside = length(maxD);
    float inside = min(max(d.x, d.y), 0.0);
    return outside + inside - effectiveRadius;
}

float axisRectSdf(vec2 point, vec2 halfSize) {
    vec2 d = abs(point) - halfSize;
    vec2 maxD = max(d, vec2(0.0));
    return length(maxD) + min(max(d.x, d.y), 0.0);
}

void main() {
    vec2 outerPos = fragUv * OuterSize;
    vec2 inset = max((OuterSize - BaseSize) * 0.5, vec2(0.0));
    vec2 basePos = outerPos - inset;
    vec2 halfSize = BaseSize * 0.5;
    float radius = max(CornerRadius, 0.0);
    float sdf = radius <= 0.0
        ? axisRectSdf(basePos - halfSize, halfSize)
        : roundedRectSdf(basePos - halfSize, halfSize, radius);
    float distance = max(sdf, 0.0);
    float alphaFactor = BlurRadius > 0.0 ? exp(-distance * distance * InvTwoSigmaSq) : 1.0;
    float alpha = ShadowColor.a * alphaFactor;
    if (alpha < 0.001) {
        alpha = 0.0;
    }
    vec4 color = vec4(ShadowColor.rgb, alpha) * ColorModulator;
    fragColor = vec4(color.rgb, clamp(color.a, 0.0, 1.0));
}
