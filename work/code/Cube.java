Voxel[] cube = new Voxel[8];
// cube[0] ist der Referenzvoxel
cube[0] = v;
cube[1] = v.getRightNeighbor();
cube[2] = v.getRightNeighbor().getFrontNeighbor();
cube[3] = v.getFrontNeighbor();
cube[4] = v.getTopNeighbor();
cube[5] = v.getTopNeighbor().getRightNeighbor();
cube[6] = v.getTopNeighbor().getRightNeighbor().getFrontNeighbor();
cube[7] = v.getTopNeighbor().getFrontNeighbor();
