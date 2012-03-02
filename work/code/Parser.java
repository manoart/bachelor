while((line = br.readLine()) != null)
{
    if(getPrefix(line).equals(VERTEX_PREFIX))
    {
        addVertices(line);
    }
    else if(getPrefix(line).equals(FACE_PREFIX))
    {
        addFaces(line);
    }
    else if(getPrefix(line).equals(NORMAL_PREFIX))
    {
        addNormals(line);
    }
}
