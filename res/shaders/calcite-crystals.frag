#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main() {
    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);
    vec4 color = texture2D(u_texture, c);

    float a = abs( sin( u_time / 30.0 ) ) / 5.0;

    color.g -= a;

    gl_FragColor = color;
}