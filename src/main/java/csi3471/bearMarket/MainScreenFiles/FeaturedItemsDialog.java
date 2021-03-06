package csi3471.bearMarket.MainScreenFiles;

import csi3471.bearMarket.Logging.ProgLogger;
import csi3471.bearMarket.ProductFiles.Product;
import csi3471.bearMarket.ProductReview.ReviewDialog;
import csi3471.bearMarket.PurchaseHistory.PurchaseProduct;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

/**
 * Create Dialog for Featured items from Product table and handle user interaction with given featured item
 * @author Richard Hutcheson
 */
public class FeaturedItemsDialog{
    private Product item1 = null, item2 = null, item3 = null;
    private Product prod;

    /**
     * Constructor for dialog randomly selects items from product vector to be
     */
    FeaturedItemsDialog(){
        ProgLogger.LOGGER.info("FeaturedItems constructor called, selecting ");
        try{
            Random rand = new Random();
            item1 = ProductTable.productVector.get(rand.nextInt(ProductTable.productVector.size() -1));
            item2 = ProductTable.productVector.get(rand.nextInt(ProductTable.productVector.size() -1));
            item3 = ProductTable.productVector.get(rand.nextInt(ProductTable.productVector.size() -1));

        }catch(Exception e){
            e.printStackTrace();
            ProgLogger.LOGGER.info("Attempted to assign featured items to random products but encountered error," +
                    "possibly that the product table vector ");
        }
     }

    /**
     * create a JDialog for the selected featured item and handle user input for the featured item
     * @param itemVal integer variable that tells the dialog which featured item had been selected by the user
     */
    public void createFIDialog(int itemVal){
        ProgLogger.LOGGER.info("Creating Featured Item Dialog");
        final int WIDTH = 400, HEIGHT = 400;
        Random rand = new Random();
        DecimalFormat df = new DecimalFormat("###.##"); //rounds to 2 decimal places
        ProgLogger.LOGGER.info("Determining which featured item product was selected");
        //determine which item product was selected and assign to local product
        prod = new Product();
        if (itemVal == 1){
            prod = item1;
        }else if (itemVal == 2){
            prod = item2;
        }else if (itemVal == 3){
            prod = item3;
        }else{
            ProgLogger.LOGGER.warning("Featured Item Product not properly assigned to local featured item");
            System.out.println("ERROR: product not properly assigned to featured item");
        }
        ProgLogger.LOGGER.info("Local Featured Item product assigned to featured item associated with button press");
        JDialog dialog = new JDialog(new JFrame(), "featured item " + itemVal);
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "FEATURED ITEM: " + prod.getProductName()));

        //determine what the discount for the product should be if the product does not already have a discounted price
        if (prod.getDiscountPrice() == 0.0){
            double discountPrice = rand.nextDouble() +.1f;
            int tempInt = rand.nextInt((int)prod.getPrice() / 3) + 1;
            discountPrice += tempInt;
            discountPrice = prod.getPrice() - discountPrice;
            discountPrice = Double.parseDouble(df.format(discountPrice));
            prod.setDiscountPrice(discountPrice);
        }
        //percent off the product is
        int percentOff =(int) (100*(1-((prod.getDiscountPrice() / prod.getPrice()))));

        //TEXT AREA
        JTextArea textArea = new JTextArea(30, 24);
        textArea.setEditable(false);
        textArea.setLineWrap(true); //enable line wrapping by the word
        textArea.setWrapStyleWord(true);
        textArea.append("Product Name: " + prod.getProductName() + "\n");
        textArea.append("Category: " + prod.getCategory() + "\n");
        textArea.append("Quantity: " + prod.getQuantity() + "\n");
        textArea.append("Rating: " + prod.getRating() + "\n\n");
        textArea.append("Original Price: " + prod.getPrice() + "\n");
        textArea.append("SALE Price: " + prod.getDiscountPrice() + "\nThe featured product is " + percentOff + "% off!" + "\n\n");
        textArea.append("Description: " + prod.getDescription());
        ProgLogger.LOGGER.info("Creating Featured Item Text Area");
        //SCROLL PANE
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS ); //always show scroll bar
        scrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT - 100));
        ProgLogger.LOGGER.info("Creating Featured Item Scroll Pane");
        //BACK BUTTON
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgLogger.LOGGER.info("Featured Item Back Button clicked");
                dialog.dispose();
            }
        });
        ProgLogger.LOGGER.info("Featured Item Back Button Created");
        //REVIEW BUTTON
        JButton reviewButton = new JButton("REVIEWS");
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgLogger.LOGGER.info("Review Button clicked on product, " + prod.getProductName());
                ReviewDialog reviewDialog = new ReviewDialog();
                reviewDialog.createDialog(prod);
            }
        });
        ProgLogger.LOGGER.info("Creating Featured Item Purchase Button");
        //PURCHASE BUTTON
        JButton purchase = new JButton("PURCHASE");
        purchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgLogger.LOGGER.info("Purchase Item Button Clicked");
                ProgLogger.LOGGER.info("Purchased featured item");
                ImageIcon icon = new ImageIcon("src/main/resources/bear.gif");
                JLabel iconLabel = new JLabel(icon);
                //JLabel label = new JLabel("You have purchased the item.");
                JLabel label = new JLabel("ITEM PURCHASED");
                label.setFont(new Font("Century Gothic", Font.BOLD, 30));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.add(iconLabel, BorderLayout.CENTER);
                imagePanel.add(label, BorderLayout.SOUTH);
                imagePanel.setPreferredSize(new Dimension(400,400));
                
                //JOptionPane.showMessageDialog(null, label, "Confirmed", JOptionPane.INFORMATION_MESSAGE, icon);
                JOptionPane.showMessageDialog(null, imagePanel, "Confirmed", JOptionPane.INFORMATION_MESSAGE, null);
                
                //prod.setQuantity(prod.getQuantity() - 1);
                //if(prod.getQuantity() <= 0) {
                //    ProductTable.productVector.remove(prod);
                //}
                MainScreen.tableModel.fireTableDataChanged();
                MainScreen.ai.purchaseHistoryVector.add(new PurchaseProduct(prod.getID()));
                MainScreen.ai.purchaseHistoryProductVector.add(prod);
            }
        });
        panel.add(scrollPane);
        panel.add(backButton);
        panel.add(reviewButton);
        panel.add(purchase);
        ProgLogger.LOGGER.info("Featured Item Purchase Item Button Created");
        dialog.add(panel);
        dialog.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        dialog.pack();// try to arrange window so that it fits preferred size
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        ProgLogger.LOGGER.info("Featured Items Dialog Created");
    }
    public String getItem1Name(){
        return item1.getProductName();
    }
    public String getItem2Name(){
        return item2.getProductName();
    }
    public String getItem3Name(){
        return item3.getProductName();
    }
}
