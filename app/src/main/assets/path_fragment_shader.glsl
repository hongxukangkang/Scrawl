precision mediump float;
varying vec2 v_textureCord;
uniform sampler2D u_sample;
void main(){
    gl_FragColor = texture2D(u_sample,v_textureCord);
}