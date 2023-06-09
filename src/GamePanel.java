import javax.swing.*;

import java.awt.*;

public class GamePanel extends JPanel {
    JPanel mainPanel;
    JPanel[] mapPanels = new JPanel[3];
    int mapIndex = 0;

    boolean editMode = false;
    JPanel editPanel = new JPanel();

    // 地圖面板的初始化
    private void mapPanelInit() {
        for (int i = 0; i < mapPanels.length; i++) {
            JPanel tmpMapPanel = new JPanel();
            tmpMapPanel.setBackground(new Color(250, 250, 250));
            tmpMapPanel.setLayout(null);
            tmpMapPanel.setBounds(0, 0, 650, 550);
            mapPanels[i] = tmpMapPanel;
            mainPanel.add(tmpMapPanel, Integer.toString(i));
        }
        editPanel.setLayout(null);
        editPanel.setBounds(0, 0, 650, 550);

        mainPanel.add(editPanel, "edit");
    }

    // 遊戲面板的建構子，參數是 Game
    GamePanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout(0, 0));
        mapPanelInit();

    }

    public JPanel getnowPanel() {
        return !editMode ? mapPanels[mapIndex] : editPanel;
    }

    public JPanel getNextPanel() {
        return mapPanels[(mapIndex + 1) % 3];
    }

    public JPanel getLastPanel() {
        return mapPanels[(mapIndex + 2) % 3];
    }

    public void goNext(boolean next) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        if (next) {
            mapIndex = ++mapIndex % 3;
        } else {
            mapIndex = (mapIndex + 2) % 3;
        }
        cardLayout.show(mainPanel, Integer.toString(mapIndex));
    }

    public void switchMode() {
        editMode = !editMode;
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        if (editMode) {
            cardLayout.show(mainPanel, "edit");
        } else {
            cardLayout.show(mainPanel, Integer.toString(mapIndex));
        }
    }

}