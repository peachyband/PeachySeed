package com.peachyseed.core.entity;

public class Model {

    private int Id;
    private int VertexCount;
    private Material Material;

    public Model(int id, int vertexCount){
        Id = id;
        VertexCount = vertexCount;
        Material = new Material();
    }

    public Model(int id, int vertexCount, Texture texture){
        Id = id;
        VertexCount = vertexCount;
        Material = new Material(texture);
    }

    public Model(Model model, Texture texture){
        Id = model.GetId();
        VertexCount = model.GetVertexCount();
        Material = model.GetMaterial();
        Material.SetTexture(texture);
    }

    public int GetId() {
        return Id;
    }

    public int GetVertexCount() {
        return VertexCount;
    }

    public Texture GetTexture() {
        return Material.GetTexture();
    }

    public Material GetMaterial() {
        return Material;
    }

    public void SetMaterial(Material material) {
        Material = material;
    }

    public void SetTexture(Texture texture) {
        Material.SetTexture(texture);
    }

    public void SetTexture(Texture texture, float reflectance) {
        Material.SetTexture(texture);
        Material.SetReflectance(reflectance);
    }
}
