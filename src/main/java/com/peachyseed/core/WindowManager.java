package com.peachyseed.core;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {
    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    private final Matrix4f projectionMatrix;
    public final String title;

    private int _width, _height;
    private long _window;

    private boolean _resize, _vSync;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        _width = width;
        _height = height;
        _vSync = vSync;
        projectionMatrix = new Matrix4f();
    }

    public void Init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        boolean Maximised = false;
        if (_width == 0 || _height == 0) {
            _width = 100;
            _height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            Maximised = true;
        }

        _window = GLFW.glfwCreateWindow(_width, _height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (_window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window");

        GLFW.glfwSetFramebufferSizeCallback(_window, (window, width, height) -> {
           _width = width;
           _height = height;
           SetResize(true);
        });

        GLFW.glfwSetKeyCallback(_window, (window, key, scancode, action, mods) -> {
           if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
               GLFW.glfwSetWindowShouldClose(_window, true);
        });

        if (Maximised)
           GLFW.glfwMaximizeWindow(_window);
        else {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(_window, (vidMode.width() - _width) / 2,
                    (vidMode.height() - _height) / 2);
        }

        GLFW.glfwMakeContextCurrent(_window);

        if (IsVSync())
            GLFW.glfwSwapBuffers(1);

        GLFW.glfwShowWindow(_window);

        GL.createCapabilities();

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BACK);
    }

    public void Update() {
        GLFW.glfwSwapBuffers(_window);
        GLFW.glfwPollEvents();
    }

    public void Cleanup() {
        GLFW.glfwDestroyWindow(_window);
    }

    public void SetClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }

    public boolean IsKeyPressed(int keycode) {
        return GLFW.glfwGetKey(_window, keycode) == GLFW.GLFW_PRESS;
    }

    public boolean WindowShouldClose() {
        return GLFW.glfwWindowShouldClose(_window);
    }

    public String GetTitle() {
        return title;
    }

    public void SetTitle(String title) {
        GLFW.glfwSetWindowTitle(_window, title);
    }

    public boolean IsResize() {
        return _resize;
    }

    public void SetResize(boolean resize) {
        _resize = resize;
    }

    public boolean IsVSync() {
        return _vSync;
    }

    public int GetWidth() {
        return _width;
    }

    public int GetHeight() {
        return _height;
    }

    public long GetWindowHandle() {
        return _window;
    }

    public Matrix4f GetProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f UpdateProjectionMatrix() {
        float aspectRatio = (float) _width / _height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f UpdateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / height;
        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }
}
