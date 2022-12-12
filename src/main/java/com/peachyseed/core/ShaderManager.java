package com.peachyseed.core;

import com.peachyseed.core.entity.Material;
import com.peachyseed.core.lightning.DirectionalLight;
import com.peachyseed.core.lightning.PointLight;
import com.peachyseed.core.lightning.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {

    private final int programId;
    private int _vertexShaderId;
    private int _fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programId = GL20.glCreateProgram();
        if (programId == 0)
            throw new Exception("Could not create shader");

        uniforms = new HashMap<>();
    }

    public void CreateUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0)
            throw new Exception("Could not find uniforms " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void CreateDirectionalLightUniform(String uniformName) throws Exception {
        CreateUniform(uniformName + ".color");
        CreateUniform(uniformName + ".direction");
        CreateUniform(uniformName + ".intensity");
    }

    public void CreatePointLightUniform(String uniformName) throws Exception {
        CreateUniform(uniformName + ".color");
        CreateUniform(uniformName + ".position");
        CreateUniform(uniformName + ".intensity");
        CreateUniform(uniformName + ".constant");
        CreateUniform(uniformName + ".linear");
        CreateUniform(uniformName + ".exponent");
    }

    public void CreateSpotLightUniform(String uniformName) throws Exception {
        CreatePointLightUniform(uniformName + ".pl");
        CreateUniform(uniformName + ".coneDirection");
        CreateUniform(uniformName + ".cutoff");
    }

    public void CreateMaterialUniform(String uniformName) throws Exception {
        CreateUniform(uniformName + ".ambient");
        CreateUniform(uniformName + ".diffuse");
        CreateUniform(uniformName + ".specular");
        CreateUniform(uniformName + ".hasTexture");
        CreateUniform(uniformName + ".reflectance");
    }

    public void CreatePointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            CreatePointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void CreateSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            CreateSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void SetUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void SetUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void SetUniform(String uniformName, boolean value) {
        GL20.glUniform1f(uniforms.get(uniformName), value ? 1 : 0);
    }

    public void SetUniform(String uniformName, SpotLight spotLight) {
        SetUniform(uniformName + ".pl", spotLight.GetPointLight());
        SetUniform(uniformName + ".coneDirection", spotLight.GetConeDirection());
        SetUniform(uniformName + ".cutoff", spotLight.GetCutoff());
    }

    public void SetUniform(String uniformName, PointLight pointLight) {
        SetUniform(uniformName + ".color", pointLight.GetColor());
        SetUniform(uniformName + ".position", pointLight.GetPosition());
        SetUniform(uniformName + ".intensity", pointLight.GetIntensity());
        SetUniform(uniformName + ".constant", pointLight.GetConstant());
        SetUniform(uniformName + ".linear", pointLight.GetLinear());
        SetUniform(uniformName + ".exponent", pointLight.GetExponent());
    }

    public void SetUniform(String uniformName, DirectionalLight directionalLight) {
        SetUniform(uniformName + ".color", directionalLight.GetColor());
        SetUniform(uniformName + ".direction", directionalLight.GetDirection());
        SetUniform(uniformName + ".intensity", directionalLight.GetIntensity());
    }

    public void SetUniform(String uniformName, PointLight[] pointLights) {
        int numLights = pointLights != null
                ? pointLights.length
                : 0;

        for(int i = 0; i < numLights; i++) {
            SetUniform(uniformName, pointLights[i], i);
        }
    }

    public void SetUniform(String uniformName, PointLight pointLight, int pos) {
        SetUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void SetUniform(String uniformName, SpotLight spotLight, int pos) {
        SetUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void SetUniform(String uniformName, SpotLight[] spotLights) {
        int numLights = spotLights != null
                ? spotLights.length
                : 0;
        for (int i = 0; i < numLights; i++){
            SetUniform(uniformName, spotLights[i], i);
        }
    }

    public void SetUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void SetUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false,
                    value.get(stack.mallocFloat(16)));
        }
    }

    public void SetUniform(String uniformName, Material material) {
        SetUniform(uniformName + ".ambient", material.GetAmbientColor());
        SetUniform(uniformName + ".diffuse", material.GetDiffuseColor());
        SetUniform(uniformName + ".specular", material.GetSpecularColor());
        SetUniform(uniformName + ".hasTexture", material.HasTexture() ? 1 : 0);
        SetUniform(uniformName + ".reflectance", material.GetReflectance());
    }

    public void SetUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void CreateVertexShader(String shaderCode) throws Exception {
        _vertexShaderId = CreateShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void CreateFragmentShader(String shaderCode) throws Exception {
        _fragmentShaderId = CreateShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int CreateShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0)
            throw new Exception("Error creating shader. Type: " + shaderType);

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader code. Type: " + shaderType
            + ". Info " + GL20.glGetShaderInfoLog(shaderId, 1024));

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void Link() throws Exception {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
            throw new Exception("Error linking shader code. Info " + GL20.glGetProgramInfoLog(programId));

        if (_vertexShaderId != 0)
            GL20.glDetachShader(programId, _vertexShaderId);

        if (_fragmentShaderId != 0)
            GL20.glDetachShader(programId, _fragmentShaderId);

        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0)
            throw new Exception("Unable to validate shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
    }

    public void Bind() {
        GL20.glUseProgram(programId);
    }

    public void Unbind() {
        GL20.glUseProgram(programId);
    }

    public void Cleanup() {
        Unbind();
        if (programId != 0)
            GL20.glDeleteProgram(programId);
    }
}
