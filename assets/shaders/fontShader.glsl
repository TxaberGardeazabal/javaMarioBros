#type vertex
#version 330 core
layout (location=0) in vec2 aPos;
layout (location=1) in vec3 aColor;
layout (location=2) in vec2 aTexCoords;

out vec3 fColor;
out vec2 fTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

void main() 
{
	fColor = aColor;
	fTexCoords = aTexCoords;
	gl_Position = vec4(aPos, 1, 1);
	//gl_Position = uProjection * uView * vec4(aPos, -5, 1);
}

#type fragment
#version 330 core

in vec3 fColor;
in vec2 fTexCoords;

uniform sampler2D uFontTexture;

out vec4 color;

void main() 
{
	//float c = texture(uFontTexture, fTexCoords).r;
	//color = vec4(fColor,1);
	vec4 c = texture(uFontTexture, fTexCoords);
	color = vec4(c.r,c.g,c.b,1) * vec4(fColor,1);
}