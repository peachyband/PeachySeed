package com.peachyseed.core.data;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class MeshData {
    private final ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    private final ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    private final ArrayList<Vector2f> textureCoordinates = new ArrayList<Vector2f>();
    private final ArrayList<Vector3i> faces = new ArrayList<Vector3i>();

    public void AddVertex(Vector3f vertex) {
        vertices.add(vertex);
    }

    public void AddNormal(Vector3f normal) {
        normals.add(normal);
    }

    public void AddTextureCoordinate(Vector2f coordinate) {
        textureCoordinates.add(coordinate);
    }

    public void AddFace(Vector3i face) {
        faces.add(face);
    }

    public ArrayList<Vector3f> GetVertices() {
        return vertices;
    }

    public ArrayList<Vector3f> GetNormals() {
        return normals;
    }

    public ArrayList<Vector2f> GetTextures() {
        return textureCoordinates;
    }

    public ArrayList<Vector3i> GetFaces() {
        return faces;
    }
}
