varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_cameraScale;

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
    vec2 coords = vec2(v_texCoords.x * u_resolution.x + u_campos.x, v_texCoords.y * u_resolution.y + u_campos.y);
    float random = rand(ceil(vec2(coords.x, coords.y) / 8. * u_cameraScale));

    vec4 c = texture2D(u_texture, v_texCoords);

    vec3 hsv = rgb2hsv(c.rgb);
    hsv.r += sin(u_time + random) * 0.16 - 0.04;
    c = vec4(hsv2rgb(hsv), c.a);

    gl_FragColor = c;
}
