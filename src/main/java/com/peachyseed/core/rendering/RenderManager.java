package com.peachyseed.core.rendering;

import com.peachyseed.core.Camera;
import com.peachyseed.core.ShaderManager;
import com.peachyseed.core.WindowManager;
import com.peachyseed.core.entity.Entity;
import com.peachyseed.core.entity.Model;
import com.peachyseed.core.lightning.DirectionalLight;
import com.peachyseed.core.lightning.PointLight;
import com.peachyseed.core.lightning.SpotLight;
import com.peachyseed.core.utils.Consts;
import com.peachyseed.core.utils.Transformation;
import com.peachyseed.core.utils.Utils;
import com.peachyseed.test.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {
    private final WindowManager _window;
    private EntityRenderer _entityRenderer;

    private Map<Model, List<Entity>> _entities = new HashMap<>();

    public RenderManager() {
        _window = Launcher.GetWindow();
    }

    public void Init() throws Exception {
        _entityRenderer = new EntityRenderer();

        _entityRenderer.Init();
    }

    public static void RenderLights(PointLight[] pointLights, SpotLight[] spotLights,
                                    DirectionalLight directionalLight, ShaderManager shader) {
        shader.SetUniform("ambientLight", Consts.AmbientLight);
        shader.SetUniform("specularPower", Consts.SpecularPower);

        int numLights = spotLights != null
                ? spotLights.length
                : 0;

        for (int i = 0; i < numLights; i++) {
            shader.SetUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null
                ? pointLights.length
                : 0;

        for (int i = 0; i < numLights; i++) {
            shader.SetUniform("pointLights", pointLights[i], i);
        }

        shader.SetUniform("directionalLight", directionalLight);
    }

    public void Render(Camera camera, DirectionalLight directionalLight,
                       PointLight[] pointLights, SpotLight[] spotLights) {
        Clear();

        if (_window.IsResize()) {
            GL30.glViewport(0, 0, _window.GetWidth(), _window.GetHeight());
            _window.SetResize(false);
        }

        _entityRenderer.Renderer(camera, pointLights, spotLights, directionalLight);
    }

    public void ProcessEntity(Entity entity) {
        List<Entity> entityList = _entityRenderer.GetEntities().get(entity.GetModel());
        if (entityList != null)
            entityList.add(entity);
        else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            _entityRenderer.GetEntities().put(entity.GetModel(), newEntityList);
        }
    }

    public void Clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void Cleanup() {_entityRenderer.Cleanup();}
}
