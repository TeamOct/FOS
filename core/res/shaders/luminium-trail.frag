uniform vec2 u_resolution;
uniform float u_time;

void main() {
    vec2 st = gl_FragCoord.xy / u_resolution.xy;
    float aspect_ratio = u_resolution.x / u_resolution.y;
    float scale = 50.0;

    float r = 0.447;
    float g = 0.796 + abs(sin(st.x * aspect_ratio * scale)+cos(st.y * scale)+sin(u_time)) * (0.812-0.796);
    float b = 0.812 + abs(sin(st.x * aspect_ratio * scale)+cos(st.y * scale)+sin(u_time)) * (0.545-0.812);

    vec3 color = vec3(r, g, b);

    gl_FragColor = vec4(color, 1.0);
}
