#version 330

layout (location=0) in vec4 position;
layout (location=1) in vec2 texCoord;

out vec2 outTexCoord;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix;

void main()
{
    gl_Position = pr_matrix * vw_matrix * position;
    outTexCoord = texCoord;
}