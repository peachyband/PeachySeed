package com.peachyseed.core;

import com.peachyseed.test.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f displayVector;

    private boolean _inWindow = false;
    private boolean _leftButtonPress = false;
    private boolean _rightButtonPress = false;

    public MouseInput() {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        displayVector = new Vector2f();
    }

    public void Init() {
        GLFW.glfwSetCursorPosCallback(Launcher.GetWindow().GetWindowHandle(),
                (window, xpos, ypos) -> {
                    currentPosition.x = xpos;
                    currentPosition.y = ypos;
                });

        GLFW.glfwSetCursorEnterCallback(Launcher.GetWindow().GetWindowHandle(),
                (window, entered) -> {
                    _inWindow = entered;
                });

        GLFW.glfwSetMouseButtonCallback(Launcher.GetWindow().GetWindowHandle(),
                (window, button, action, mods) -> {
                    _leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS;
                    _rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW.GLFW_PRESS;
                });
    }

    public void Input() {
        displayVector.x = 0;
        displayVector.y = 0;
        if(previousPosition.x > 0 && previousPosition.y > 0 && _inWindow) {
            double x = currentPosition.x - previousPosition.x;
            double y = currentPosition.y - previousPosition.y;
            boolean rotateX = x != 0;
            boolean rotateY = y != 0;

            if (rotateX)
                displayVector.y = (float) x;
            if (rotateY)
                displayVector.x = (float) y;
        }
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public Vector2f GetDisplayVector() {
        return displayVector;
    }

    public boolean IsLeftButtonPressed() {
        return _leftButtonPress;
    }

    public boolean IsRightButtonPressed() { return _rightButtonPress; }
}
