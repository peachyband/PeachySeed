#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 fragmentTextureCoordinates;
out vec3 fragmentNormal;
out vec3 fragmentPosition;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    fragmentNormal = normalize(worldPosition).xyz;
    fragmentPosition = worldPosition.xyz;
    fragmentTextureCoordinates = textureCoordinates;
}