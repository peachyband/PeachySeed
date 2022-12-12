package com.peachyseed.test;

import com.peachyseed.core.EngineManager;
import com.peachyseed.core.WindowManager;
import com.peachyseed.core.utils.Consts;

public class Launcher
{
    private static WindowManager Window;
    private static TestGame Game;

    public static void main(String[] args)
    {
        Window = new WindowManager(Consts.TITLE, 0, 0, false);
        Game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.Start();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static WindowManager GetWindow() {
        return Window;
    }

    public static TestGame GetGame() {
        return Game;
    }
}
