package com.peachyseed.test;

import com.peachyseed.core.*;
import com.peachyseed.core.entity.Entity;
import com.peachyseed.core.entity.Model;
import com.peachyseed.core.entity.Texture;
import com.peachyseed.core.lightning.DirectionalLight;
import com.peachyseed.core.lightning.PointLight;
import com.peachyseed.core.lightning.SpotLight;
import com.peachyseed.core.rendering.RenderManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGame implements ILogic {

    private final static float CameraMoveSpeed = 0.01f;

    private final RenderManager renderer;
    private final ModelLoader loader;
    private final WindowManager window;

    private List<Entity> _entities;
    private Camera _camera;

    private Vector3f _cameraIncrement;

    private float _lightAngle;
    private DirectionalLight _directionalLight;
    private PointLight[] _pointLights;
    private SpotLight[] _spotLights;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.GetWindow();
        loader = new ModelLoader();
        _camera = new Camera();
        _cameraIncrement = new Vector3f(0, 0, 0);
        _lightAngle = -90;
    }

    @Override
    public void Init() throws Exception {
        renderer.Init();

        Model model = loader.LoadOBJModel("/models/cube.obj");
        model.SetTexture(new Texture(loader.LoadTexture("textures/rock.png")), 1f);

        _entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * 100 - 50;
            float y = random.nextFloat() * 100 - 50;
            float z = random.nextFloat() * -300;
            _entities.add(new Entity(model, new Vector3f(x, y, z),
                    new Vector3f(random.nextFloat() * 180, random.nextFloat() * 180, 0), 1));
        }
        _entities.add(new Entity(model, new Vector3f(0, 0, -2f), new Vector3f(0, 0, 0), 1));

        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity, 0, 0, 1);

        Vector3f coneDirection = new Vector3f(0, 0, 1);
        float cutoff = (float) Math.cos(Math.toRadians(180));
        SpotLight spotLight = new SpotLight(new PointLight(lightColor, new Vector3f(0, 0, 1f),
                lightIntensity, 0, 0, 1), coneDirection, cutoff);

        SpotLight spotLightSample = new SpotLight(pointLight, coneDirection, cutoff);

        lightPosition = new Vector3f(-1, -10, 0);
        lightColor = new Vector3f(1, 1, 1);
        _directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);

        _pointLights = new PointLight[] {pointLight};
        _spotLights = new SpotLight[] {spotLight, spotLight};
    }

    @Override
    public void Input() {
        _cameraIncrement.set(0, 0, 0);
        if (window.IsKeyPressed(GLFW.GLFW_KEY_W))
            _cameraIncrement.z = -1;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_S))
            _cameraIncrement.z = 1;

        if (window.IsKeyPressed(GLFW.GLFW_KEY_A))
            _cameraIncrement.x = -1;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_D))
            _cameraIncrement.x = 1;

        if (window.IsKeyPressed(GLFW.GLFW_KEY_E))
            _cameraIncrement.y = -1;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_Q))
            _cameraIncrement.y = 1;

        /*if (window.IsKeyPressed(GLFW.GLFW_KEY_LEFT))
            _pointLight.GetPosition().x -= 0.0000001f;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_RIGHT))
            _pointLight.GetPosition().x += 0.0000001f;*/

        float lightPosition = _spotLights[0].GetPointLight().GetPosition().z;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_UP))
            _spotLights[0].GetPointLight().GetPosition().z = lightPosition - 0.000001f;
        if (window.IsKeyPressed(GLFW.GLFW_KEY_DOWN))
            _spotLights[0].GetPointLight().GetPosition().z = lightPosition + 0.000001f;
    }

    @Override
    public void Update(float interval, MouseInput mouseInput) {
        _camera.MovePosition(
                _cameraIncrement.x * CameraMoveSpeed,
                _cameraIncrement.y * CameraMoveSpeed,
                _cameraIncrement.z * CameraMoveSpeed);

        if (mouseInput.IsRightButtonPressed()) {
            Vector2f rotationVector = mouseInput.GetDisplayVector();
            _camera.MoveRotation(rotationVector.x, rotationVector.y, 0);
        }

        //_entity.IncrementRotation(0.0f, 0.1f, 0.0f);



        _lightAngle += 0.1f;
        if (_lightAngle > 90) {
            _directionalLight.SetIntensity(0);
            if (_lightAngle >= 360)
                _lightAngle = -90;
        }
        else if (_lightAngle <= -80 || _lightAngle >= 80) {
            float factor = 1 - (Math.abs(_lightAngle) - 80) / 10.0f;
            _directionalLight.SetIntensity(factor);
            _directionalLight.GetColor().y = Math.max(factor, 0.9f);
            _directionalLight.GetColor().z = Math.max(factor, 0.5f);
        }
        else {
            _directionalLight.SetIntensity(1);
            _directionalLight.GetColor().x = 1;
            _directionalLight.GetColor().y = 1;
            _directionalLight.GetColor().z = 1;
        }

        double angRad = Math.toRadians(_lightAngle);
        _directionalLight.GetDirection().x = (float) Math.sin(angRad);
        _directionalLight.GetDirection().y = (float) Math.cos(angRad);

        for (Entity entity : _entities) {
            renderer.ProcessEntity(entity);
        }
    }

    @Override
    public void Render() {
        renderer.Render(_camera, _directionalLight, _pointLights, _spotLights);
    }

    @Override
    public void Cleanup() {
        renderer.Cleanup();
        loader.Cleanup();
    }
}
