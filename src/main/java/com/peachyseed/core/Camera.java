package com.peachyseed.core;

import org.joml.Vector3f;

public class Camera {

    private Vector3f _position;
    private Vector3f _rotation;

    public Camera() {
        _position = new Vector3f(0, 0, 0);
        _rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        _position = position;
        _rotation = rotation;
    }

    public void MovePosition (float x, float y, float z) {
        if(z != 0) {
            _position.x += (float) Math.sin(Math.toRadians (_rotation.y)) * -1.0f * z;
            _position.z += (float) Math.cos(Math.toRadians (_rotation.y)) * z;
        }
        if(x != 0) {
            _position.x += (float) Math.sin(Math.toRadians (_rotation.y - 90)) * -1.0f * x;
            _position.z += (float) Math.cos(Math.toRadians (_rotation.y - 90)) * x;
        }
        _position.y += y;
    }

    public void SetPosition(float x, float y, float z) {
        _position.x = x;
        _position.y = y;
        _position.z = z;
    }

    public void SetRotation(float x, float y, float z) {
        _rotation.x = x;
        _rotation.y = y;
        _rotation.z = z;
    }

    public void MoveRotation(float x, float y, float z) {
        _rotation.x += x;
        _rotation.y += y;
        _rotation.z += z;
    }

    public Vector3f GetPosition() {
        return _position;
    }

    public Vector3f GetRotation() {
        return _rotation;
    }
}
