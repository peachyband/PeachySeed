package com.peachyseed.core;

import com.peachyseed.core.utils.Consts;
import com.peachyseed.test.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000f;

    private static int Fps;
    private static float FrameTime = 1.0f / FRAMERATE;

    private boolean _isRunning;

    private WindowManager _windowManager;
    private MouseInput _mouseInput;
    private GLFWErrorCallback _errorCallback;
    private ILogic _gameLogic;

    private void Init() throws Exception {
        GLFW.glfwSetErrorCallback(_errorCallback = GLFWErrorCallback.createPrint(System.err));

        _windowManager = Launcher.GetWindow();
        _gameLogic = Launcher.GetGame();
        _mouseInput = new MouseInput();
        _windowManager.Init();
        _gameLogic.Init();
        _mouseInput.Init();
    }

    public void Start() throws Exception {
        Init();
        if (_isRunning){
            return;
        }

        Run();
    }

    public void Run() {
        _isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (_isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            Input();

            while (unprocessedTime > FrameTime) {
                render = true;
                unprocessedTime -= FrameTime;

                if (_windowManager.WindowShouldClose())
                    Stop();

                if(frameCounter >= NANOSECOND) {
                    SetFps(frames);
                    _windowManager.SetTitle(Consts.TITLE + " FPS: " + GetFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                Update(FrameTime);
                Render();
                frames++;
            }
        }
        Cleanup();
    }

    public void Stop() {
        if (!_isRunning)
            return;

        _isRunning = false;
    }

    public void Input() {
        _mouseInput.Input();
        _gameLogic.Input();
    }

    private void Render() {
        _gameLogic.Render();
        _windowManager.Update();
    }

    private void Update(float interval) {
        _gameLogic.Update(interval, _mouseInput);
    }

    private void Cleanup() {
        _windowManager.Cleanup();
        _gameLogic.Cleanup();
        _errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int GetFps() {
        return Fps;
    }

    public static void SetFps(int fps) {
        Fps = fps;
    }
}
