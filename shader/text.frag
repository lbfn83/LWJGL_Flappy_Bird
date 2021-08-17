#version 330

in vec2 outTexCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D tex;
uniform vec4 color;

void main()
{

	//vec4 sampled = texture(tex, outTexCoord);
	//fragColor = color * sampled;
    fragColor = color * texture(tex, outTexCoord);
    //fragColor = color;	
    // each RGB color code is scaled from 255 to 1 
    // When color is black dicard them
    if ( fragColor.r + fragColor.g + fragColor.b == 0.0)
		discard;
	//	When the fragment color is not corresponding to the color intended, make it to the intended
	if ( fragColor.r + fragColor.g + fragColor.b < color.r + color.g + color.b)
		fragColor = color ;
		
}