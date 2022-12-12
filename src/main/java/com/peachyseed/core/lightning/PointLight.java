package com.peachyseed.core.lightning;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f _color;
    private Vector3f _position;

    private float _intensity;
    private float _constant;
    private float _linear;
    private float _exponent;

    public PointLight(Vector3f color, Vector3f position, float intensity,
                      float constant, float linear, float exponent) {
        _color = color;
        _position = position;
        _intensity = intensity;
        _constant = constant;
        _linear = linear;
        _exponent = exponent;
    }

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this(color, position, intensity, 0, 0, 0);
    }

    public Vector3f GetColor() {
        return _color;
    }

    public void SetColor(Vector3f color) {
        _color = color;
    }

    public Vector3f GetPosition() {
        return _position;
    }

    public void SetPosition(Vector3f position) {
        _position = position;
    }

    public float GetIntensity() {
        return _intensity;
    }

    public void SetIntensity(float intensity) {
        _intensity = intensity;
    }

    public float GetConstant() {
        return _constant;
    }

    public void SetConstant(float constant) {
        _constant = constant;
    }

    public float GetLinear() {
        return _linear;
    }

    public void SetLinear(float linear) {
        _linear = linear;
    }

    public float GetExponent() {
        return _exponent;
    }

    public void SetExponent(float exponent) {
        _exponent = exponent;
    }
}
