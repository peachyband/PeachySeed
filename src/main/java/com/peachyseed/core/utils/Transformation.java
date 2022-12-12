package com.peachyseed.core.utils;

import com.peachyseed.core.Camera;
import com.peachyseed.core.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Transformation {

    public static Matrix4f CreateTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(entity.GetPosition())
                .rotateX((float) Math.toRadians(entity.GetRotation().x))
                .rotateY((float) Math.toRadians(entity.GetRotation().y))
                .rotateZ((float) Math.toRadians(entity.GetRotation().z))
                .scale(entity.GetScale());

        return matrix;
    }

    public static Matrix4f GetViewMatrix(Camera camera) {
        Vector3f position = camera.GetPosition();
        Vector3f rotation = camera.GetRotation();
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        matrix.translate(-position.x, -position.y, -position.z);

        return matrix;
    }
}
