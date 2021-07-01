/** Represents a binary tree */
public class BinaryTree<T extends Comparable<T>> {
    /** Root node */
    Node root;
    /** Width of screen */
    int widthOfScreen;
    /** Height of screen */
    int heightOfScreen;

    /** Basic constructor.
     * @param widthOfScreen Width of a screen
     * @param heightOfScreen Height of a screen
     */
    public BinaryTree(int widthOfScreen, int heightOfScreen) {
        this.widthOfScreen = widthOfScreen;
        this.heightOfScreen = heightOfScreen;
    }
    /** Setter for width and height of a screen
     * @param width Width of a screen
     * @param height Height of a screen
     * */
    public void setSizeOfScreen(int width, int height){
        this.widthOfScreen = width;
        this.heightOfScreen = height;
    }
    /** Inserts a value to a tree
     * @param value value to be inserted*/
    public void insert(T value){
        if(root == null)
            root = new Node(value);
        else
            insert(value, root);
    }
    /** Search for a value in a tree.
     * @param value value to be searched for
     * @returns Node with a value or null if value is not in a tree.
     * @throws ElementNotFoundException
     * */
    public Node search(T value) throws ElementNotFoundException{
        Node node = search(value, root);
        if(node == null) throw new ElementNotFoundException();
        return node;
    }
    /** Counts height of a tree */
    public int heightOfTree(){
        return heightOfTree(root);
    }
    /** Removes an element from a tree
     * @param value value to be removed
     * @throws ElementNotFoundException
     * */
    public void remove(T value) throws ElementNotFoundException {
        Node to_remove = search(value);
        if(to_remove == null)
            throw new ElementNotFoundException();
        else
            root = removeHelper(value, root);
    }

    /** Counts appropriate y coords for each node
     * @throws NoElementsException*/
    public void countYCoords() throws NoElementsException{
        if(root == null)
            throw new NoElementsException();
        int dh = heightOfScreen/( heightOfTree()+1);
        countYCoords(dh/4, dh, root);
    }
    /** Counts appropriate x coords for each node */
    public void countXCoords(){
        countXCoords(widthOfScreen/2, 0, widthOfScreen, root);
    }

    /** Helping method to count height of a tree.
     *
     * @param startNode node to start from
     * @return height starting from startNode
     */
    private int heightOfTree(Node startNode){
        if(startNode== null)
            return -1;

        int leftHeight = heightOfTree(startNode.left);
        int rightHeight = heightOfTree(startNode.right);
        return (leftHeight > rightHeight ? leftHeight : rightHeight)+1;
    }

    /** Helping method to count y coords
     *
     * @param y y coordinate
     * @param dh distance between two floors
     * @param node a node to start from
     */
    private void countYCoords(int y, int dh, Node node){
        if(node != null){
            node.y = y;
            countYCoords(y+dh, dh, node.left);
            countYCoords(y+dh, dh, node.right);
        }
    }

    /** Helping method to count x coords
     *
     * @param x x coordinate
     * @param lBound left bound
     * @param rBound right bound
     * @param node node to start from
     */
    private void countXCoords(int x, int lBound, int rBound, Node node){
        if(node != null){
            node.x = x;
            countXCoords((x+lBound)/2, lBound, x, node.left);
            countXCoords((x+rBound)/2, x, rBound, node.right);
        }
    }

    /** Finds minimum subnode in a node
     *
     * @param node node to start from
     * @return minimum subnode
     */
    private Node findMinSubnode(Node node){
        if(node.left == null)
            return node;
        return findMinSubnode(node.left);
    }

    /** Helphing method to remove value
     *
     * @param value value to be removed
     * @param root node to start from
     * @return
     */
    private Node removeHelper(T value, Node root){
        if(root == null)
            return null;
        else if(value.compareTo(root.data) < 0)
            root.left = removeHelper(value, root.left);
        else if(value.compareTo(root.data) > 0)
            root.right = removeHelper(value, root.right);
        else{
            if(root.left == null && root.right == null)
                root =  null;
            else if(root.left == null || root.right == null)
                root = root.left == null ? root.right : root.left;
            else{
                Node minNode = findMinSubnode(root.right);
                T temp = root.data;
                root.data = minNode.data;
                minNode.data = temp;
                root.right = removeHelper(minNode.data, root.right);
            }
        }
        return root;
    }

    /** Helphing method to find a value in a tree
     *
     * @param value value to be found
     * @param node node at which value appears, null if there is no such value
     * @return
     */
    private Node search(T value, Node node){
        if(node == null || node.data.compareTo(value) == 0)
            return node;
        else if(value.compareTo(node.data) < 0)
            return search(value, node.left);
        else
            return search(value, node.right);
    }

    /** Helping method to insert a value
     *
     * @param value value to be inserted
     * @param node starting node
     */
    private void insert(T value, Node node){
        if(value.compareTo(node.data) <= 0) {
            if(node.left == null)
                node.left = new Node(value);
            else
                insert(value, node.left);
        } else {
            if(node.right == null)
                node.right = new Node(value);
            else
                insert(value, node.right);
        }
    }
    /** Represents a single node in a tree */
    public class Node{
        /** Left children */
        Node left;
        /** Right children */
        Node right;
        /** Data */
        T data;
        /** x position */
        int x;
        /** y position */
        int y;
        /** Node is active if it has been searched. It is then highlighted */
        boolean isActive = false;

        /** Basic constructor
         *
         * @param data data value
         */
        Node(T data) {
            this.data = data;
        }
    }
}
