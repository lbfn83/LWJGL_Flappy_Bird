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
    if ( fragColor.r + fragColor.g + fragColor.b == 0.0)
		discard;
}