#version 330

out vec4 color;

in DATA
{
	vec2 tc;
}fs_in;

uniform sampler2D tex;
uniform int top;

void main()
{
	color = texture(tex, fs_in.tc);
	if ( color.w < 1.0)
		discard;
	
}	