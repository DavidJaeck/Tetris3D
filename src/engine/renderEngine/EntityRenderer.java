package engine.renderEngine;

import engine.entities.Entity;
import engine.models.RawModel;
import engine.models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import engine.shaderEngine.StaticShader;
import engine.models.ModelTexture;
import engine.util.Maths;

import java.util.List;
import java.util.Map;

// all renderer classes are similar:
// the bind and unbind necessary variables and most importantly DRAW to the DISPLAY
public class EntityRenderer {
    private final StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    // binds and unbinds Variables enables and disables rendering specific settings and DRAWs the entity
    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) { // for all engine.entities:
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) { // for all engine.entities of type model
                prepareInstance(entity); // loads the transformation of the entity to a uniform variable
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0); // draws the model triangle by triangle
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID()); // binds VAO
        GL20.glEnableVertexAttribArray(0); //binds VBOs
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        if (texture.isHasTransparency())
            MasterRenderer.disableCulling();
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0); // activate the default texture bank for uniform sampler2D in fragment shader
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID()); // bind the texture to the texture bank
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0); // unbinds VBOs
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0); // unbinds VAO
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
