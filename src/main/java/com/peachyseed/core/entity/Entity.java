package com.peachyseed.core.entity;

import org.joml.Vector3f;

public class Entity {

    private Model _model;
    private Vector3f _position;
    private Vector3f _rotation;
    private float _scale;

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        _model = model;
        _position = position;
        _rotation = rotation;
        _scale = scale;
    }

    public void IncrementPosition(float x, float y, float z) {
        _position.x += x;
        _position.y += y;
        _position.z += z;
    }

    public void SetPosition(float x, float y, float z) {
        _position.x = x;
        _position.y = y;
        _position.z = z;
    }

    public void IncrementRotation(float x, float y, float z) {
        _rotation.x += x;
        _rotation.y += y;
        _rotation.z += z;
    }

    public void SetRotation(float x, float y, float z) {
        _rotation.x = x;
        _rotation.y = y;
        _rotation.z = z;
    }

    public Model GetModel() {
        return _model;
    }

    public Vector3f GetPosition() {
        return _position;
    }

    public Vector3f GetRotation() {
        return _rotation;
    }

    public float GetScale() {
        return _scale;
    }
}
