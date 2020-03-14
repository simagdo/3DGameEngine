#version 150

in vec2 pass_textureCoords;
in vec2 pass_blurTextureCoords;

out vec4 out_colour;

uniform sampler2D guiTexture;
uniform sampler2D blurTexture;
uniform vec3 overrideColour;
uniform float alpha;

uniform float useOverrideColour;
uniform float useBlur;
uniform float useTexture;

uniform float uiWidth;
uniform float uiHeight;
uniform float radius;

const float cornerSmoothFactor = 0.55;

float square(float val) {
    return val * val;
}

float distanceSquared(vec2 p1, vec2 p2) {
    vec2 vector = p2 - p1;
    return dot(vector, vector);
}

float calcRoundedCorners() {
    if (radius <= 0.0) {
        return 1.0;
    }
    vec2 pixelPos = pass_textureCoords * vec2(uiWidth, uiHeight);
    vec2 minCorner = vec2(radius, radius);
    vec2 maxCorner = vec2(uiWidth - radius, uiHeight - radius);

    vec2 cornerPoint = clamp(pixelPos, minCorner, maxCorner);
    float lowerBound = square(radius - cornerSmoothFactor);
    float upperBound = square(radius + cornerSmoothFactor);
    return smoothstep(upperBound, lowerBound, distanceSquared(pixelPos, cornerPoint));
}

void main(void) {

    out_colour = vec4(1.0);

    if (useTexture > 0.5) {
        out_colour = texture(guiTexture, pass_textureCoords);
    }

    out_colour.rgb = mix(out_colour.rgb, overrideColour * out_colour.rgb, useOverrideColour);

    if (useBlur > 0.5) {
        vec4 blurColour = texture(blurTexture, pass_blurTextureCoords);
        out_colour.rgb = mix(blurColour.rgb, out_colour.rgb, alpha);
    } else {
        out_colour.a *= alpha;
    }
    out_colour.a *= calcRoundedCorners();

}
