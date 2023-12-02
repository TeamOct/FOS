varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float u_time;

float rand(vec2 co){
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 rgb2hsv(vec3 c){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main(){
    vec4 c = texture2D(u_texture, v_texCoords);

    vec3 hsv = rgb2hsv(c.rgb);
    hsv.r += sin(u_time + rand(vec2(gl_FragCoord.x, gl_FragCoord.y)) * 100.0) * 0.08 - 0.04;
    hsv.g += sin(u_time * 2.0 + rand(vec2(gl_FragCoord.x, gl_FragCoord.y)) * 50.0) * 0.03 - 0.05;
    hsv.b += sin(u_time * 0.5 + rand(vec2(gl_FragCoord.x, gl_FragCoord.y)) * 500.0) * 0.04 - 0.01;
    c = vec4(hsv2rgb(hsv), c.a);

    gl_FragColor = c;
}
