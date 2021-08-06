#version 330

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

out DATA
{
	vec2 tc;
	vec3 position;
}vs_out;

uniform mat4 pr_matrix;
uniform mat4 ml_matrix;
uniform mat4 vw_matrix;
void main()
{
	gl_Position = pr_matrix *vw_matrix * ml_matrix* position;
	vs_out.tc = tc;
	vs_out.position = vec3(vw_matrix * ml_matrix* position);
	
}	