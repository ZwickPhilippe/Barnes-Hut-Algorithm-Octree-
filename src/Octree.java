public class Octree {

    public static final double AU = 150e9;

    private Node root;
    private static final double universeLength = 2 * AU;
    public final double T = 1;
    private int treeSize = 0;
    private Body[] rebuildList; // Stores the bodies after moving
    private int listAddIndex = 0;
    private boolean useCenterOfOctant;


    public Octree(boolean useCenterOfOctant){
        this.useCenterOfOctant=useCenterOfOctant;
    }


    public int getTreeSize() {
        return this.treeSize;
    }


    // Add new body into the octree. If the tree is empty, set the new body as root,
    // otherwise insert recursively
    public void add(Body newValue) {
        if (root == null) {
            this.root = new Node(newValue);
            treeSize++;
        } else {
            root.add(newValue, universeLength, 0, 0, 0);
        }
    }

    // Move bodies according to force exerted onto them, then add them into rebuildList
    public void calculateForce() {
        this.root.calculateForce(this.root);
    }

    public void moveTree(){
        this.root.moveSystem(this.root);
    }

    public int getEmptyNodes(){
        if(this.root==null){
            return 0;
        }else{
            return this.root.getEmptyNodes(this.root);
        }
    }

    // Rebuild the octree after bodies have been moved
    public void rebuildTree() {
        treeSize = 0;
        this.root = null;
        for (int i = 0; i < rebuildList.length; i++) {
            if (root == null) {
                this.root = new Node(rebuildList[i]);
                treeSize++;
            } else {
                // check if bodies exceeded limits, if so, don't insert them into the tree
                if (Math.abs(rebuildList[i].getPosition().getX()) < universeLength && Math.abs(rebuildList[i].getPosition().getY()) < universeLength && Math.abs(rebuildList[i].getPosition().getZ()) < universeLength) {
                    root.add(rebuildList[i], universeLength, 0, 0, 0);
                }
            }
        }
        // initialise new rebuildList
        rebuildList = new Body[treeSize];
        listAddIndex = 0;
    }


    public void printSize(){
        System.out.println(this.root.size(this.root));
    }

    public void drawTree() {
        this.root.drawNode(this.root);
    }

    // Node class implementing functionality for the octree nodes
    private class Node {
        private Node[] subNodes; // array representing the eight octants
        private Body value;
        private Vector3 centerOfGravity;
        private double totalMass;
        private Vector3 centerOfOctant;
        private double gridDiameter;

        public Node(Body value) {
            this.value = value;
            this.centerOfGravity = value.getPosition();
            this.totalMass = value.getMass();
        }

        // Add nodes recursively into the octree
        public void add(Body newValue, double gridLength, double gridX, double gridY, double gridZ) {

            // node has value, but no children
            if (this.subNodes == null) {

                // create a new subNode array
                this.subNodes = new Node[8];

                // set center and diameter of the newly created octant
                this.centerOfOctant = new Vector3(gridX, gridY, gridZ);
                this.gridDiameter = gridLength * 2;

                // calculate position for this body in new subNode array
                int addIndexForCurrentValue = findAddIndex(this.value.getPosition(), gridX, gridY, gridZ);

                // calculate position for new body in subNode array
                int addIndexForNewValue = findAddIndex(newValue.getPosition(), gridX, gridY, gridZ);

                // check if positions are equal
                boolean positionIsEqual = this.value.getPosition().isEqualTo(newValue.getPosition());

                // insert this body into correct position in subNode array
                this.subNodes[addIndexForCurrentValue] = new Node(this.value);

                // remove current body from parent node
                this.value = null;

                // check if this body and new body would be in the same subNode
                if (addIndexForCurrentValue == addIndexForNewValue) {
                    if (gridLength == 0) {
                        return;
                    }
                    // only add if there is no body at exactly the same position
                    if (!positionIsEqual) {
                        // determine which subNode to add new body into
                        addRecursive(addIndexForNewValue,  newValue, gridLength, gridX, gridY, gridZ);
                    }
                // insert new body into own subNode
                } else {
                    this.subNodes[addIndexForNewValue] = new Node(newValue);
                    treeSize++;
                }

            // node has no value, but children
            } else {

                // calculate position for new body in subNode array
                int addIndexForNewValue = this.findAddIndex(newValue.getPosition(), gridX, gridY, gridZ);

                // add new body into corresponding subnode if it is emtpy
                if (subNodes[addIndexForNewValue] == null) {
                    subNodes[addIndexForNewValue] = new Node(newValue);
                    treeSize++;

                // corresponding subNode is not empty => insert recursively
                } else {
                    addRecursive(addIndexForNewValue,  newValue, gridLength, gridX, gridY, gridZ);
                }
            }
            // update total mass of node
            double oldMass = this.totalMass;
            this.totalMass += newValue.getMass();

            // update center of gravity of node
            this.centerOfGravity = this.centerOfGravity.times(oldMass).plus(newValue.getPosition().times(newValue.getMass())).times(1 / this.totalMass);
        }


        private void addRecursive(int addIndexForNewValue, Body newValue, double gridLength, double gridX, double gridY, double gridZ ){

            switch (addIndexForNewValue) {
                case 0:
                    this.subNodes[0].add(newValue, gridLength / 2, gridX + gridLength / 2, gridY + gridLength / 2, gridZ - gridLength / 2);
                    break;
                case 1:
                    this.subNodes[1].add(newValue, gridLength / 2, gridX - gridLength / 2, gridY + gridLength / 2, gridZ - gridLength / 2);
                    break;
                case 2:
                    this.subNodes[2].add(newValue, gridLength / 2, gridX - gridLength / 2, gridY - gridLength / 2, gridZ - gridLength / 2);
                    break;
                case 3:
                    this.subNodes[3].add(newValue, gridLength / 2, gridX + gridLength / 2, gridY - gridLength / 2, gridZ - gridLength / 2);
                    break;
                case 4:
                    this.subNodes[4].add(newValue, gridLength / 2, gridX + gridLength / 2, gridY + gridLength / 2, gridZ + gridLength / 2);
                    break;
                case 5:
                    this.subNodes[5].add(newValue, gridLength / 2, gridX - gridLength / 2, gridY + gridLength / 2, gridZ + gridLength / 2);
                    break;
                case 6:
                    this.subNodes[6].add(newValue, gridLength / 2, gridX - gridLength / 2, gridY - gridLength / 2, gridZ + gridLength / 2);
                    break;
                case 7:
                    this.subNodes[7].add(newValue, gridLength / 2, gridX + gridLength / 2, gridY + gridLength / 2, gridZ + gridLength / 2);
                    break;
                default:
                    System.err.println("Invalid add index");
                    break;
            }

        }



        // Find positioning of new node relative to node calling the method to determine
        // which octant to insert the new node
        public int findAddIndex(Vector3 position, double gridX, double gridY, double gridZ) {
            int addIndex;

            if (position.getX() <= gridX) {
                if (position.getY() <= gridY) {
                    if (position.getZ() <= gridZ) {
                        addIndex = 2;
                    } else {
                        addIndex = 6;
                    }
                } else {
                    if (position.getZ() <= gridZ) {
                        addIndex = 1;
                    } else {
                        addIndex = 5;
                    }
                }
            } else {
                if (position.getY() <= gridY) {
                    if (position.getZ() <= gridZ) {
                        addIndex = 3;
                    } else {
                        addIndex = 7;
                    }
                } else {
                    if (position.getZ() <= gridZ) {
                        addIndex = 0;
                    } else {
                        addIndex = 4;
                    }
                }
            }
            return addIndex;
        }

        public void calculateForce(Node node) {
            if (node.subNodes == null) {
                Vector3 forceToAdd = node.calculateForceRecursive(root, new Vector3(0, 0, 0));
                node.value.setForce(forceToAdd);
                if (rebuildList == null) {
                    rebuildList = new Body[treeSize];
                }
                rebuildList[listAddIndex] = node.value;
                listAddIndex++;

            } else {
                for (int i = 0; i < 8; i++) {
                    if (node.subNodes[i] != null) {
                        calculateForce(node.subNodes[i]);
                    }
                }
            }
        }

        public void moveSystem(Node node){
            if (node.subNodes == null) {
                node.value.move();
            } else {
                for (int i = 0; i < 8; i++) {
                    if (node.subNodes[i] != null) {
                        moveSystem(node.subNodes[i]);
                    }
                }
            }
        }

        public int size(Node node){
            if (node.subNodes == null) {
                return 1;
            } else {
                int size=0;
                for (int i = 0; i < 8; i++) {
                    if (node.subNodes[i] != null) {
                      size= size +  size(node.subNodes[i]);
                    }
                }
                return size;
            }

        }


        // Draw nodes onto StdDraw window
        public void drawNode(Node node) {
            if (node.subNodes == null) {
                node.value.draw();
            } else {
                for (int i = 0; i < 8; i++) {
                    if (node.subNodes[i] != null) {
                        drawNode(node.subNodes[i]);
                    }
                }
            }
        }

        // Calculate the force exerted on a body. If ratio of gridLength / distance is within a
        // threshold T, consider the whole subNode array as a singular body to reduce the number of
        // operations needed
        public Vector3 calculateForceRecursive(Node currentNode,  Vector3 forceVector) {

            if (currentNode == null) {
                return new Vector3(0, 0, 0);
            }
            if (currentNode.subNodes == null) {
                if (!currentNode.equals(this)) {
                    return this.value.gravitationalForce(currentNode.value);
                }
                return new Vector3(0, 0, 0);
            } else {
                double distance;
                if(useCenterOfOctant){
                     distance = this.centerOfGravity.distanceTo(currentNode.centerOfOctant);
                }else{
                    distance = this.centerOfGravity.distanceTo(currentNode.centerOfGravity);
                }
                double ratio = currentNode.gridDiameter / distance;
                if (ratio < T) {
                    return this.value.gravitationalForce(currentNode.totalMass, currentNode.centerOfGravity);
                } else {
                    forceVector = forceVector
                            .plus(calculateForceRecursive(currentNode.subNodes[0],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[1],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[2],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[3],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[4],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[5],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[6],  forceVector))
                            .plus(calculateForceRecursive(currentNode.subNodes[7],  forceVector));
                    return forceVector;
                }
            }
        }

        public int getEmptyNodes(Node currentNode){
            if(currentNode==null){
                return 1;
            }else if(currentNode.subNodes==null){
                return 0;
            }else{
                int numberOfLeaves=0;
                for (int i = 0; i < 8; i++) {
                        numberOfLeaves= numberOfLeaves +  getEmptyNodes(currentNode.subNodes[i]);
                }
                return numberOfLeaves;
            }
        }
    }
}
