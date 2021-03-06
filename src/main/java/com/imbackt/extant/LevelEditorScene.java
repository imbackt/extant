package com.imbackt.extant;

import com.imbackt.extant.component.FontRenderer;
import com.imbackt.extant.component.SpriteRenderer;
import com.imbackt.extant.renderer.Shader;
import com.imbackt.extant.renderer.Texture;
import com.imbackt.extant.util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private final float[] vertexArray = {
            // position             // color          // uv coordinates
            100f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // Bottom right 0
            0.0f, 100f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, // Top left     1
            100f, 100f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, // Top right    2
            0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private final int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3  // Bottom right triangle
    };

    private int vaoID;

    private Shader defaultShader;
    private Texture testTexture;

    GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        System.out.println("Creating 'test object'");
        testObj = new GameObject("test object");
        testObj.addComponent(new SpriteRenderer());
        testObj.addComponent(new FontRenderer());
        addGameObjectToScene(testObj);

        camera = new Camera(new Vector2f(-200f, -300f));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        testTexture = new Texture("assets/images/testImage.png");

        // ==========================================================
        // Generate VAO, VBO and EBO buffer objects and send to GPY
        // ==========================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // Create EBO
        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Upload texture to shader
        defaultShader.uploadTexture("TEXT_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

        if (!firstTime) {
            System.out.println("Creating gameObject!!");
            GameObject go = new GameObject("Game test 2");
            go.addComponent(new SpriteRenderer());
            addGameObjectToScene(go);
            firstTime = true;
        }

        for (GameObject gameObject : gameObjects) {
            gameObject.update(dt);
        }
    }
}
