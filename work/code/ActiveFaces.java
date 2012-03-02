private int[] activeFaces(float z)
{
    int j = 0;
    int[] activeFaces = new int[this.faces.length];

    for(int i = 0; i < this.faces.length; i += 3)
    {
        if(!((this.vertices[this.faces[i]     * 3 - 1] < z) &&
             (this.vertices[this.faces[i + 1] * 3 - 1] < z) &&
             (this.vertices[this.faces[i + 2] * 3 - 1] < z)) &&
           !((this.vertices[this.faces[i]     * 3 - 1] > z) &&
             (this.vertices[this.faces[i + 1] * 3 - 1] > z) &&
             (this.vertices[this.faces[i + 2] * 3 - 1] > z)))
        {
            activeFaces[j++] = this.faces[i];
            activeFaces[j++] = this.faces[i + 1];
            activeFaces[j++] = this.faces[i + 2];
        }
    }
    return activeFaces;
}
