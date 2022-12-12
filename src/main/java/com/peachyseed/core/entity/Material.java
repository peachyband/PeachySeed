package com.peachyseed.core.entity;

import com.peachyseed.core.utils.Consts;
import org.joml.Vector4f;

public class Material {

    private Vector4f _ambientColor;
    private Vector4f _diffuseColor;
    private Vector4f _specularColor;

    private float _reflectance;
    private Texture _texture;

    public Material() {
        _ambientColor = Consts.DefaultColor;
        _diffuseColor = Consts.DefaultColor;
        _specularColor = Consts.DefaultColor;
        _texture = null;
        _reflectance = 0;
    }

    public Material(Vector4f color, float reflectance) {
        this(color, color, color, reflectance, null);
    }

    public Material(Texture texture) {
        this(Consts.DefaultColor, Consts.DefaultColor,
                Consts.DefaultColor, 0, texture);
    }

    public Material(Vector4f color, float reflectance, Texture texture) {
        this(color, color, color, reflectance, texture);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor,
                    float reflectance, Texture texture) {
        _ambientColor = ambientColor;
        _diffuseColor = diffuseColor;
        _specularColor = specularColor;
        _reflectance = reflectance;
        _texture = texture;
    }

    public Vector4f GetAmbientColor() {
        return _ambientColor;
    }

    public Vector4f GetDiffuseColor() {
        return _diffuseColor;
    }

    public Vector4f GetSpecularColor() {
        return _specularColor;
    }

    public float GetReflectance() {
        return _reflectance;
    }

    public Texture GetTexture() {
        return _texture;
    }

    public void SetTexture(Texture texture) {
        _texture = texture;
    }

    public void SetAmbientColor(Vector4f ambientColor) {
        _ambientColor = ambientColor;
    }

    public void SetDiffuseColor(Vector4f diffuseColor) {
        _diffuseColor = diffuseColor;
    }

    public void SetSpecularColor(Vector4f specularColor) {
        _specularColor = specularColor;
    }

    public void SetReflectance(float reflectance) {
        _reflectance = reflectance;
    }

    public boolean HasTexture() {
        return _texture != null;
    }
}
