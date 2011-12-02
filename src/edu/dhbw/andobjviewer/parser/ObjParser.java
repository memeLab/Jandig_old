/**
	Copyright (C) 2010  Tobias Domhan

    This file is part of AndObjViewer.

    AndObjViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AndObjViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AndObjViewer.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package edu.dhbw.andobjviewer.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Vector;
import edu.dhbw.andobjviewer.models.Group;
import edu.dhbw.andobjviewer.models.Model;
import edu.dhbw.andobjviewer.util.BaseFileUtil;

/**
 * Simple obj parser.
 * Does not support the full obj specification.
 * It supports:
 * 	- vertices
 *  - vertice normals
 *  - texture coordinates
 *  - basic materials
 *  - faces(faces may not omit the face normal)
 *  - limited texture support, through the map_Kd statement (no options allowed, only image files allowed)
 * @author tobi
 *
 */
public class ObjParser {
	private final int VERTEX_DIMENSIONS = 3;
	private final int TEXTURE_COORD_DIMENSIONS = 2;
	
	private BaseFileUtil fileUtil;
	
	public ObjParser(BaseFileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}
	
	/**
	 * parses an wavefront obj model file
	 * @param modelName name of the model
	 * @param is stream of the file to parse
	 * @return the parsed model
	 * @throws IOException
	 * @throws ParseException
	 */
	public Model parse(String modelName, BufferedReader is) throws IOException, ParseException {
		//global vertices/normals
		ArrayList<float[]> vertices = new ArrayList<float[]>(1000);
		ArrayList<float[]> normals = new ArrayList<float[]>(1000);
		ArrayList<float[]> texcoords = new ArrayList<float[]>();
		
		// tgh: get max/min x,y,z dimensions
		float maxX, maxY, maxZ, minX, minY, minZ;
		maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
		minX = minY = minZ = Float.POSITIVE_INFINITY;
		
		Model model = new Model();
		Group curGroup = new Group();
		MtlParser mtlParser = new MtlParser(model,fileUtil);
		SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
		SimpleTokenizer slashTokenizer = new SimpleTokenizer();
		slashTokenizer.setDelimiter("/");
		
		String line;
		int lineNum = 1;
		for (line = is.readLine(); 
		line != null; 
		line = is.readLine(), lineNum++)
		{
			if (line.length() > 0) {
				if (line.startsWith("#")) {
					//ignore comments
				} else if (line.startsWith("v ")) {
					//add new vertex to vector
					String endOfLine = line.substring(2);
					spaceTokenizer.setStr(endOfLine);
					
					// tgh: x,z,y on the file.
					//   meaning: the second coordinate is the one for the axis that comes out of the screen
					float x0 = Float.parseFloat(spaceTokenizer.next());
					float z0 = Float.parseFloat(spaceTokenizer.next());
					float y0 = Float.parseFloat(spaceTokenizer.next());

					// update the max/min variables
					maxX = (x0 > maxX)?x0:maxX;
					minX = (x0 < minX)?x0:minX;
					maxY = (y0 > maxY)?y0:maxY;
					minY = (y0 < minY)?y0:minY;
					maxZ = (z0 > maxZ)?z0:maxZ;
					minZ = (z0 < minZ)?z0:minZ;

					// tgh: x,z,y
					vertices.add(new float[]{x0,z0,y0});

					/*
					vertices.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
							*/
				}
				else if (line.startsWith("vt ")) {
					//add new texture vertex to vector
					String endOfLine = line.substring(3);
					spaceTokenizer.setStr(endOfLine);
					texcoords.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
				}
				else if (line.startsWith("f ")) {
					//add face to group
					String endOfLine = line.substring(2);
					spaceTokenizer.setStr(endOfLine);
					int faces = spaceTokenizer.delimOccurCount()+1;
					if(faces != 3) {
						throw new ParseException(modelName, lineNum, "only triangle faces are supported");
					}
					for (int i = 0; i < 3; i++) {//only triangles supported
						String face = spaceTokenizer.next();
						slashTokenizer.setStr(face);
						int vertexCount = slashTokenizer.delimOccurCount()+1;
						int vertexID=0;
						int textureID=-1;
						int normalID=0;
						if(vertexCount == 2) {
							//vertex reference
							vertexID = Integer.parseInt(slashTokenizer.next())-1;
							//normal reference
							normalID = Integer.parseInt(slashTokenizer.next())-1;
							throw new ParseException(modelName,
									lineNum,
									"vertex normal needed.");
						} else if(vertexCount == 3) {
							//vertex reference
							vertexID = Integer.parseInt(slashTokenizer.next())-1;
							String texCoord = slashTokenizer.next();
							if(!texCoord.equals("")) {
								//might be omitted
								//texture coord reference
								textureID = Integer.parseInt(texCoord)-1;
							}
							//normal reference
							normalID = Integer.parseInt(slashTokenizer.next())-1;
						} else {
							throw new ParseException(modelName,
									lineNum,
									"a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
						}
						float[] vec;
						try {
							vec = vertices.get(vertexID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName,
									lineNum,
									"non existing vertex referenced.");
						}
						if(vec==null)
							throw new ParseException(modelName,
									lineNum,
									"non existing vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupVertices.add(vec[j]);
						if(textureID != -1) {
							//in case there is a texture on the face
							try {
								vec = texcoords.get(textureID);
							} catch (IndexOutOfBoundsException ex) {
								throw new ParseException(modelName,
										lineNum,
										"non existing texture coord referenced.");
							}
							if(vec==null)
								throw new ParseException(modelName,
										lineNum,
										"non existing texture coordinate referenced.");
							for (int j = 0; j < TEXTURE_COORD_DIMENSIONS; j++)
								curGroup.groupTexcoords.add(vec[j]);
						}
						try {
							vec = normals.get(normalID);
						} catch (IndexOutOfBoundsException ex) {
							throw new ParseException(modelName,
									lineNum,
									"non existing normal vertex referenced.");
						}
						if(vec==null)
							throw new ParseException(modelName,
									lineNum,
									"non existing normal vertex referenced.");
						for (int j = 0; j < VERTEX_DIMENSIONS; j++)
							curGroup.groupNormals.add(vec[j]);
					}
				}
				else if (line.startsWith("vn ")) {
					//add new vertex normal to vector
					String endOfLine = line.substring(3);
					spaceTokenizer.setStr(endOfLine);
					normals.add(new float[]{
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next()),
							Float.parseFloat(spaceTokenizer.next())});
				} else if (line.startsWith("mtllib ")) {
					//parse material file
					//get  ID of the mtl file
					String filename = line.substring(7);
					String[] files = Util.splitBySpace(filename);
					for (int i = 0; i < files.length; i++) {
						BufferedReader mtlFile = fileUtil.getReaderFromName(files[i]);
						if(mtlFile != null)
							mtlParser.parse(model, mtlFile);
					}					
				} else if(line.startsWith("usemtl ")) {
					//material changed -> new group
					if(curGroup.groupVertices.size()>0) {
						model.addGroup(curGroup);
						curGroup = new Group();
					}
					//the rest of the line contains the name of the new material
					curGroup.setMaterialName(line.substring(7));
				} else if(line.startsWith("g ")) {
					//new group definition
					if(curGroup.groupVertices.size()>0) {
						model.addGroup(curGroup);
						curGroup = new Group();
						//group name will be ignored so far...is there any use?
					}
				}
			}
		}
		if(curGroup.groupVertices.size()>0) {
			model.addGroup(curGroup);
		}
		Iterator<Group> groupIt = model.getGroups().iterator();
		while (groupIt.hasNext()) {
			Group group = (Group) groupIt.next();
			group.setMaterial(model.getMaterial(group.getMaterialName()));
		}
		
		// tgh: set the dimension variables in the model before returning it		
		model.setXdim(Math.abs(maxX-minX));
		model.setYdim(Math.abs(maxY-minY));
		model.setZdim(Math.abs(maxZ-minZ));

		return model;
	}
}