package com.peachyseed.core;

import com.peachyseed.core.data.MeshData;
import com.peachyseed.core.entity.Model;
import com.peachyseed.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    private List<Integer> _vaos = new ArrayList<>();
    private List<Integer> _vbos = new ArrayList<>();
    private List<Integer> _textures = new ArrayList<>();

    public Model LoadOBJModel(String fileName) {
        List<String> lines = Utils.ReadAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //vertices
                    Vector3f verticesVector = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVector);
                    break;
                case "vt":
                    //vertex textures
                    Vector2f texturesVector = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(texturesVector);
                    break;
                case "vn":
                    //vertex normals
                    Vector3f normalVector = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalVector);
                    break;
                case "f":
                    //faces
                    ProcessFace(tokens[1], faces);
                    ProcessFace(tokens[2], faces);
                    ProcessFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }

        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f position : vertices) {
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            i++;
        }

        float[] textureCoordinateArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            ProcessVertex(face.x, face.y, face.z, textures, normals,
                    indices, textureCoordinateArray, normalsArray);
        }

        int[] indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        return LoadModel(verticesArray, textureCoordinateArray, normalsArray, indicesArray);
    }

    public Model LoadFBXModel(String fileName) {
        //need to clean up scene after reading model
        AIScene scene = Assimp.aiImportFile(fileName, Assimp.aiProcess_Triangulate);

        PointerBuffer buffer = scene.mMeshes();

        AIMesh mesh = AIMesh.create(buffer.get(0));
        MeshData processedMesh = ProcessMesh(mesh);
        Model processedModel;

        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[processedMesh.GetVertices().size() * 3];
        int i = 0;

        for (Vector3f position : processedMesh.GetVertices()) {
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            i++;
        }

        float[] textureCoordinateArray = new float[processedMesh.GetVertices().size() * 2];
        float[] normalsArray = new float[processedMesh.GetVertices().size() * 3];

        for (Vector3i face : processedMesh.GetFaces()) {
            ProcessVertex(face.x, face.y, face.z, processedMesh.GetTextures(), processedMesh.GetNormals(),
                    indices, textureCoordinateArray, normalsArray);
        }

        int[] indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        processedModel = LoadModel(verticesArray, textureCoordinateArray, normalsArray, indicesArray);

        scene.clear();
        return processedModel;
    }

    public Model LoadModel(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices) {
        int id = CreateVAO();
        StoreIndicesBuffer(indices);
        StoreDataInAttributeList(0, 3, vertices);
        StoreDataInAttributeList(1, 2, textureCoordinates);
        StoreDataInAttributeList(2, 3, normals);
        Unbind();
        return new Model(id, indices.length);
    }

    public int LoadTexture(String filename) throws Exception {
        int width;
        int height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null)
                throw new Exception("Image file " + filename + " not loaded " +
                        STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        _textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);

        return id;
    }

    private static void ProcessVertex(int position, int textureCoordinate,
                                      int normal, List<Vector2f> textureCoordinateList,
                                      List<Vector3f> normalList, List<Integer> indices,
                                      float[] textureCoordinateArray, float[] normalArray) {

        indices.add(position);

        if (textureCoordinate >= 0) {
            Vector2f textureCoordinateVector = textureCoordinateList.get(textureCoordinate);
            textureCoordinateArray[position * 2] = textureCoordinateVector.x;
            textureCoordinateArray[position * 2 + 1] = 1 - textureCoordinateVector.y;
        }

        if (normal >= 0) {
            Vector3f normalVector = normalList.get(normal);
            normalArray[position * 3] = normalVector.x;
            normalArray[position * 3 + 1] = normalVector.y;
            normalArray[position * 3 + 2] = normalVector.z;
        }
    }

    private static MeshData ProcessMesh(AIMesh mesh) {
        MeshData meshData = new MeshData();
        AIVector3D.Buffer vertices = mesh.mVertices();

        for (AIVector3D vertex : vertices) {
            meshData.AddVertex(new Vector3f(vertex.x(), vertex.y(), vertex.z()));
        }

        AIVector3D.Buffer textureCoordinates = mesh.mTextureCoords(0);

        for (AIVector3D coordinate : textureCoordinates) {
            meshData.AddTextureCoordinate(new Vector2f(coordinate.x(),coordinate.y()));
        }

        AIVector3D.Buffer normals = mesh.mNormals();

        for (AIVector3D normal : normals) {
            meshData.AddNormal(new Vector3f(normal.x(), normal.y(), normal.z()));
        }

        AIFace.Buffer indices = mesh.mFaces();

        for (AIFace face : indices) {
            if (face.mNumIndices() == 3) {
                IntBuffer indicesBuffer = face.mIndices();
                meshData.AddFace(new Vector3i(indicesBuffer.get(0), indicesBuffer.get(1), indicesBuffer.get(2)));
            }
        }

        return meshData;
    }

    private static void ProcessFace(String token, List<Vector3i> faces){
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int position = -1;
        int coordinate = -1;
        int normal = -1;

        position = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1) {
            String textureCoordinate = lineToken[1];
            coordinate = textureCoordinate.length() > 0
                    ? Integer.parseInt(textureCoordinate) - 1
                    : -1;
            if (length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }
        Vector3i facesVector = new Vector3i(position, coordinate, normal);
        faces.add(facesVector);
    }

    private int CreateVAO(){
        int id = GL30.glGenVertexArrays();
        _vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void StoreIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        _vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.StoreDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void StoreDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        _vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.StoreDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void Unbind() {
        GL30.glBindVertexArray(0);
    }

    public void Cleanup() {
        for (int vao : _vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : _vbos)
            GL30.glDeleteBuffers(vbo);
        for (int texture : _textures)
            GL11.glDeleteTextures(texture);
    }
}
