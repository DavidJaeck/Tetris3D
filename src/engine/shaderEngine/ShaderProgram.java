package engine.shaderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding and unbinding the necessary variables for the compiled shader programs on the gpu
public abstract class ShaderProgram {
    private final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    // buffer for loading up matrices to uniform variables
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    // constructs the shaderProgram from the vertex and fragment shader text files
    public ShaderProgram(String vertexFile, String fragmentFile) {
        programID = GL20.glCreateProgram();
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER); // read and compile shader-files
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        GL20.glAttachShader(programID, vertexShaderID); // attach shaders to the program
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAllAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getALlUniformLocations();
    }

    // compiles the shader by loading its source code returning the shaders identifier
    public static int loadShader(String file, int type) {
        int shaderID = GL20.glCreateShader(type); // creates a shader of type vertex or fragment
        GL20.glShaderSource(shaderID, readShaderFile(file)); // loads the shader source code
        GL20.glCompileShader(shaderID); // compiles the shader source code
        if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) { // checks if compiled successfully
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader-file: " + file);
            System.exit(-1);
        }
        return shaderID;
    }

    // reads the shader source file
    private static StringBuilder readShaderFile(String file) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                shaderSource.append(line).append("\n");
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read shader-file: " + file);
            e.printStackTrace();
            System.exit(-1);
        }
        return shaderSource;
    }

    protected abstract void bindAllAttributes();

    // binds the data of a VBO to an in-parameter of a shader
    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    protected abstract void getALlUniformLocations();

    // get the location of a uniform-variable of a shader, so values can be uploaded to it
    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    // METHODS for loading up values to the uniform variable
    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void load2DVector(int location, Vector2f vector) {
        GL20.glUniform2f(location, vector.x, vector.y);
    }

    protected void loadBoolean(int location, boolean value) {
        if (value)
            GL20.glUniform1f(location, 1f);
        else
            GL20.glUniform1f(location, 0f);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip(); //announces writing is finished, enables reading
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    //tells the gpu to use the shaderProgram by id
    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

}
