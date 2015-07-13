attribute vec4 a_position;
attribute vec2 a_textureCord;
varying vec2 v_textureCord;
//uniform mat4 u_projectionMatrix;
void main(){
    gl_Position = a_position;//u_projectionMatrix *
    v_textureCord = a_textureCord;
}