package com.peachyseed.core.rendering;

import com.peachyseed.core.Camera;
import com.peachyseed.core.entity.Model;
import com.peachyseed.core.lightning.DirectionalLight;
import com.peachyseed.core.lightning.PointLight;
import com.peachyseed.core.lightning.SpotLight;

public interface IRenderer<T> {

    public void Init() throws Exception;

    public void Renderer(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
                         DirectionalLight directionalLight);

    public void Bind(Model model);

    public void Unbind();

    public void Prepare(T t, Camera camera);

    public void Cleanup();
}
