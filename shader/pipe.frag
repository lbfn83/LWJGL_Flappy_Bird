#version 330 core        

out vec4 color;

in DATA     
{
	vec2 tc;
	vec3 position;
}fs_in;

uniform vec2 bird;
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
	// opague level is not met just discard the texture data.. 
	 		discard;
	color *= 2.0 / (length(bird - fs_in.position.xy)*0.3 + 0.5) ;
	//color.w = 1.0;
}	