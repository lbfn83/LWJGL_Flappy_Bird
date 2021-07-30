#version 330

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

out DATA
{
	vec2 tc;
}vs_out;


uniform mat4 pr_matrix;
uniform mat4 vw_matrix;

void main()
{
	gl_Position = pr_matrix *vw_matrix* position;
	vs_out.tc = tc;
}	