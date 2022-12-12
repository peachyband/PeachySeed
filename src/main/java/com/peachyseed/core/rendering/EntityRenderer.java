package com.peachyseed.core.rendering;

import com.peachyseed.core.Camera;
import com.peachyseed.core.ShaderManager;
import com.peachyseed.core.entity.Entity;
import com.peachyseed.core.entity.Model;
import com.peachyseed.core.lightning.DirectionalLight;
import com.peachyseed.core.lightning.PointLight;
import com.peachyseed.core.lightning.SpotLight;
import com.peachyseed.core.utils.Transformation;
import com.peachyseed.core.utils.Utils;
import com.peachyseed.test.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements  IRenderer{

    private ShaderManager _shader;

    private Map<Model, List<Entity>> _entities;

    public EntityRenderer() throws Exception {
        _entities = new HashMap<>();
        _shader = new ShaderManager();
    }

    @Override
    public void Init() throws Exception {
        _shader.CreateVertexShader(Utils.LoadResource("/shaders/entity_vertex.vs"));
        _shader.CreateFragmentShader(Utils.LoadResource("/shaders/entity_fragment.fs"));
        _shader.Link();
        _shader.CreateUniform("textureSampler");
        _shader.CreateUniform("transformationMatrix");
        _shader.CreateUniform("projectionMatrix");
        _shader.CreateUniform("viewMatrix");
        _shader.CreateUniform("ambientLight");
        _shader.CreateMaterialUniform("material");
        _shader.CreateUniform("specularPower");
        _shader.CreateDirectionalLightUniform("directionalLight");
        _shader.CreatePointLightListUniform("pointLights", 5);
        _shader.CreateSpotLightListUniform("spotLights", 5);
    }

    @Override
    public void Renderer(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
                            DirectionalLight directionalLight) {
        _shader.Bind();
        _shader.SetUniform("projectionMatrix", Launcher.GetWindow()
                .UpdateProjectionMatrix());
        RenderManager.RenderLights(pointLights, spotLights, directionalLight, _shader);

        for (Model model : _entities.keySet()) {
            Bind(model);
            List<Entity> entityList = _entities.get(model);

            for (Entity entity : entityList) {
                Prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.GetModel().GetVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            Unbind();
        }

        _entities.clear();
        _shader.Unbind();
    }

    @Override
    public void Bind(Model model) {
        GL30.glBindVertexArray(model.GetId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        _shader.SetUniform("material", model.GetMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.GetTexture().GetId());
    }

    @Override
    public void Unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void Prepare(Object entity, Camera camera) {
        _shader.SetUniform("textureSampler", 0);
        _shader.SetUniform("transformationMatrix", Transformation.CreateTransformationMatrix((Entity) entity));
        _shader.SetUniform("viewMatrix", Transformation.GetViewMatrix(camera));
    }

    @Override
    public void Cleanup() {
        _shader.Cleanup();
    }

    public Map<Model, List<Entity>> GetEntities() {
        return _entities;
    }
}
