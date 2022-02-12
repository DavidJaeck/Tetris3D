package engine.objConverter;

import org.lwjgl.util.vector.Vector3f;

// data class holding the information of a vertex of a to be parsed object
public class Vertex {
	private static final int NO_INDEX = -1;
	private final Vector3f position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private final int index;
	
	public Vertex(int index,Vector3f position){
		this.index = index;
		this.position = position;
	}
	
	public int getIndex(){
		return index;
	}
	
	public boolean hasNotBeenSet(){
		return textureIndex == NO_INDEX || normalIndex == NO_INDEX;
	}
	
	public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
		return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
	}
	
	public void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	
	public void setNormalIndex(int normalIndex){
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}
}
