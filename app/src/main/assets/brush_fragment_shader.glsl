//precision mediump float;
//varying vec4 vColor;
//void main() {
//    gl_FragColor = vColor;
//}
precision mediump float;
uniform vec4 u_Color;
void main(){
    gl_FragColor = u_Color;
}