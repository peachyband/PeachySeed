#version 400 core

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 fragmentTextureCoordinates;
in vec3 fragmentNormal;
in vec3 fragmentPosition;

out vec4 fragmentColour;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight {
    PointLight pl;
    vec3 coneDirection;
    float cutoff;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoordinate) {
     if (material.hasTexture == 1) {
        ambientC = texture(textureSampler, fragmentTextureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
     }
     else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
     }
}

vec4 calculateLightColor(vec3 lightColor, float lightIntensity, vec3 position,
        vec3 toLightDirection, vec3 normal){
    vec4 diffuseColor = new vec4(0, 0, 0, 0);
    vec4 specularColor = new vec4(0, 0, 0, 0);

    //diffuse
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    //specular
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * lightIntensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calculateLightColor(light.color, light.intensity, position, lightDirection, normal);

    //attenuation
    float distance = length(lightDirection);
    float attenuationEnvironment = light.constant + light.linear + light.exponent * distance * distance;
    return lightColor / attenuationEnvironment;
}

vec4 calculateSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pl.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec3 fromLightDirection = -toLightDirection;
    float spotAlpha = dot(fromLightDirection, normalize(light.coneDirection));

    vec4 color = vec4(0, 0, 0, 0);

    if (spotAlpha > light.cutoff){
        color = calculatePointLight(light.pl, position, normal);
        color += (1.0 - (1.0 - spotAlpha) / (1.0 - light.cutoff));
    }

    return color;
}

vec4 calculateDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calculateLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

void main() {
    setupColors(material, fragmentTextureCoordinates);

    vec4 diffuseSpecularComp = calculateDirectionalLight(directionalLight, fragmentPosition, fragmentNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0){
            diffuseSpecularComp += calculatePointLight(pointLights[i], fragmentPosition, fragmentNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pl.intensity > 0)
            diffuseSpecularComp += calculateSpotLight(spotLights[i], fragmentPosition, fragmentNormal);
    }

    fragmentColour = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}