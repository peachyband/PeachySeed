package com.peachyseed.core.lightning;

import org.joml.Vector3f;

public class SpotLight {

    private PointLight _pointLight;

    private Vector3f _coneDirection;

    private float _cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoff) {
        _pointLight = pointLight;
        _coneDirection = coneDirection;
        _cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight) {
        _pointLight = spotLight.GetPointLight();
        _coneDirection = spotLight.GetConeDirection();
        _cutoff = spotLight.GetCutoff();
    }

    public PointLight GetPointLight() {
        return _pointLight;
    }

    public void SetPointLight(PointLight pointLight) {

        _pointLight = pointLight;
    }

    public Vector3f GetConeDirection() {
        return _coneDirection;
    }

    public void SetConeDirection(Vector3f coneDirection) {

        _coneDirection = coneDirection;
    }

    public float GetCutoff() {
        return _cutoff;
    }

    public void SetCutoff(float cutoff) {

        _cutoff = cutoff;
    }
}
