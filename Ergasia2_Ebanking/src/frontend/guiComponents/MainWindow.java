package frontend.guiComponents;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import backend.context.AppContext;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Map<String, JPanel> panelMap; //map to save the panels

    public MainWindow() {
        setTitle("Bank of TUC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null); // center of the screen

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        panelMap = new HashMap<>();

        // add the start panels
        addPanel("start", new StartPanel(this));
        addPanel("login", new LoginPanel(this, AppContext.getInstance().getUserManager()));
        addPanel("signup", new SignupPanel(this));

        add(contentPanel);
        setVisible(true);

        showPanel("start"); // Starts with start panel
    }

    // add a new panel if it doesn't exist
    public void addPanel(String name, JPanel panel) {
        if (!panelMap.containsKey(name)) {
            contentPanel.add(panel, name);
            panelMap.put(name, panel);
        }
    }

    //show the panel based on the name
    public void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

   /*  //(Προαιρετικό) Επιστροφή ενός panel με βάση το όνομα
    public JPanel getPanel(String name) {
        return panelMap.get(name);
    }*/
}
