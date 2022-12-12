package com.peachyseed.core.lightning;

import org.joml.Vector3f;

public class DirectionalLight {

    private Vector3f _color;
    private Vector3f _direction;

    private float _intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        _color = color;
        _direction = direction;
        _intensity = intensity;
    }

    public Vector3f GetColor() {
        return _color;
    }

    public void SetColor(Vector3f color) {
        _color = color;
    }

    public Vector3f GetDirection() {
        return _direction;
    }

    public void SetDirection(Vector3f direction) {
        _direction = direction;
    }

    public float GetIntensity() {
        return _intensity;
    }

    public void SetIntensity(float intensity) {
        _intensity = intensity;
    }
}
