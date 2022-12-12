package com.peachyseed.core;

public interface ILogic {
    void Init() throws Exception;

    void Input();

    void Update(float interval, MouseInput mouseInput);

    void Render();

    void Cleanup();
}
