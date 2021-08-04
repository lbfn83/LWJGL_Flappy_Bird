#version 330 core        

out vec4 color;

in DATA     
{
	vec2 tc;
}fs_in;

uniform sampler2D tex;
uniform int top;

void main()
{
	vec2 toptex = vec2(0.0,  1.0 ) - fs_in.tc;
	
	if( top == 1){
		color = texture(tex, toptex);  	
		}
	else{
		color = texture(tex, fs_in.tc );
		}
		
	if ( color.w < 1.0)
			discard;
}	