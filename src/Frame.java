import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** Represents a window */
public class Frame extends JFrame {
    /** Possible types of tree */
    private enum TypeOfTree{
        INTEGER("Integer"), FLOAT("Float"), STRING("String");
        String representation;

        TypeOfTree(String text) {
            this.representation = text;
        }

        @Override
        public String toString() {
            return this.representation;
        }
    }
    /** Width of frame */
    int frameWidth = 800;
    /** Height of frame */
    int frameHeight = 600;

    /** Surface for tree to be displayed */
    DrawingSurface surface;
    /** A binary tree object */
    BinaryTree tree;
    /** Holds type of chosen tree */
    TypeOfTree type;

    /** Main function, starts an application. Displays a window with type of tree selection. */
    public static void main(String[] args) {
        Object[] possibleValues = { TypeOfTree.INTEGER, TypeOfTree.FLOAT, TypeOfTree.STRING };
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Type of tree",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);
        if(selectedValue != null){
            TypeOfTree t = (TypeOfTree)selectedValue;
            new Frame(t);
        }
    }
    /** Basic constructor *
     * @param type Type of chosen tree
     * @throws HeadlessException
     */
    public Frame(TypeOfTree type) throws HeadlessException {
        this.type = type;
        if(type == TypeOfTree.INTEGER)
            tree = new BinaryTree<Integer>(frameWidth, frameHeight);
        else if(type == TypeOfTree.FLOAT)
            tree = new BinaryTree<Float>(frameWidth, frameHeight);
        else
            tree = new BinaryTree<String>(frameWidth, frameHeight);

        this.setSize(frameWidth, frameHeight);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("Binary Tree | LABORATORIA 6");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        surface = new DrawingSurface();

        mainPanel.add(surface, BorderLayout.CENTER);
        mainPanel.add(new MenuPanel(), BorderLayout.SOUTH);
        this.add(mainPanel);
        this.setVisible(true);

        //Action listener
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Component c = (Component)e.getSource();
                tree.setSizeOfScreen(c.getWidth(), c.getHeight());
                surface.repaint();
            }
        });

    }
    /** Represents a panel with 3 buttons and text field */
    private class MenuPanel extends JPanel implements ActionListener {
        /** Text field */
        JTextField field;
        /** Insert button */
        JButton insert;
        /** Search button */
        JButton search;
        /** Remove button */
        JButton remove;
        /** Basic constructor. Sets up text field and buttons on a screen */
        public MenuPanel() {
             field = new JTextField(10);
             insert = new JButton("Insert");
             search = new JButton("Search");
             remove = new JButton("Remove");

            this.add(field);
            this.add(insert);
            this.add(search);
            this.add(remove);

            insert.addActionListener(this);
            search.addActionListener(this);
            remove.addActionListener(this);
        }

        /** Handles pressing buttons.
         * @param event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            String fieldText = field.getText().strip();
            if(fieldText.isBlank())
                JOptionPane.showMessageDialog(Frame.this, "Input must not be empty!", "Ooops...",JOptionPane.ERROR_MESSAGE );
            else{
                try{
                    Comparable input = parseValue(fieldText);
                    if(event.getSource() == insert){
                        tree.insert(input);
                    }
                    if(event.getSource() == remove){
                        tree.remove(input);
                    }
                    if(event.getSource() == search){
                        BinaryTree.Node node = tree.search(input);
                        Thread nodeHighlighter = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                node.isActive = true;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                node.isActive = false;
                                surface.repaint();
                            }
                        });
                        nodeHighlighter.start();

                    }
                } catch (ElementNotFoundException e) {
                    JOptionPane.showMessageDialog(Frame.this, e.getMessage(), "Ooops...",JOptionPane.ERROR_MESSAGE );
                } catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(Frame.this, "Input of not valid type.", "Ooops...",JOptionPane.ERROR_MESSAGE );
                }
                surface.repaint();
            }
        }
        /** Parses string value to an appropriate type
         * @param value String value to be parsed
         * @returns Comparable object
         */
        private Comparable parseValue(String value) throws NumberFormatException {
            Comparable input;
            if(type == TypeOfTree.INTEGER)
                input = Integer.parseInt(value);
            else if(type == TypeOfTree.FLOAT)
                input = Float.parseFloat(value);
            else
                input = value;
            return input;
        }
    }
    /** Represents a surface for tree to be drawn */
    private class DrawingSurface extends JPanel {
        /** Paints components on a surface
         * @param g Graphics object
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            drawTree(g2d);
        }
        /** Draws a tree on a surface
         * @param g2d Graphics2D object
         */
        private void drawTree(Graphics2D g2d) {
            try {
                tree.countYCoords();
                tree.countXCoords();
                drawNode(tree.root, g2d);
            } catch (NoElementsException e) {
            }

        }
        /** Recursive function for drawing node and its children */
        private void drawNode(BinaryTree.Node node, Graphics2D g2d){
            if(node != null){
                g2d.drawString((node.data).toString(), node.x-5, node.y-15);
                if(node.left != null)
                    g2d.drawLine(node.x, node.y, node.left.x, node.left.y);
                if(node.right != null)
                    g2d.drawLine(node.x, node.y, node.right.x, node.right.y);

                if(node.isActive)
                    g2d.setPaint(Color.RED);
                g2d.fillRect(node.x-5, node.y-5, 10, 10);
                g2d.setPaint(Color.BLACK);

                drawNode(node.left, g2d);
                drawNode(node.right, g2d);
            }

        }
    }
}
