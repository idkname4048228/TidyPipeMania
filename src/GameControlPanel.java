import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameControlPanel extends JPanel {
    Panel ControlPanel = new Panel();
    Game game;
    Font usingFont = new Font("微軟正黑體", Font.BOLD, 24);

    JButton switchModeButton = new JButton("切換模式");
    boolean editMode = false;

    JButton lastRoundButton = new JButton("上一關");
    JButton nextRoundButton = new JButton("下一關");
    JButton restrartButton = new JButton("重新開始");
    JButton checkButton = new JButton("確認");

    JLabel strightPipe = new JLabel();
    JLabel bentPipe = new JLabel();
    JLabel tPipe = new JLabel();
    JLabel crossPipe = new JLabel();
    JLabel waterStorage = new JLabel();
    JLabel waterStorageWithWater = new JLabel();

    JButton appendRowButton = new JButton("增加行");
    JButton removeRowButton = new JButton("刪除行");
    JButton appendColButton = new JButton("增加列");
    JButton removeColButton = new JButton("刪除列");
    JButton deleteButton = new JButton("刪除");
    JButton saveButton = new JButton("存檔");

    Component[] problemComponents = { lastRoundButton, nextRoundButton, restrartButton, checkButton };
    Component[] editLabels = { strightPipe, bentPipe, tPipe, crossPipe, waterStorage, waterStorageWithWater };
    Component[] editComponents = { appendRowButton, removeRowButton, appendColButton, removeColButton, deleteButton,
            saveButton, waterStorage, waterStorageWithWater };

    private void setEventLister() {
        switchModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMode = !editMode;
                switchMode();
                game.switchMode();
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkButton.setEnabled(false);// 讓check Button不能再按
                restrartButton.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        System.out.println(game.check());
                        restrartButton.setEnabled(true);
                        return null;
                    }
                };
                worker.execute();

            }
        });

        restrartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 以下是對 game 內的 attribute 做初始化
                checkButton.setEnabled(true);
                game.restart();
            }
        });

        lastRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 顯示上一張卡片，也就是索引為 game_map_index - 1 的 game_maps_name 內的面板
                lastRoundButton.setEnabled(false);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        lastRoundButton.setEnabled(game.goStage(false));
                        return null;
                    }
                };
                worker.execute();

                // 確認是否為最後一題或第一題，並對他們(上下關按鈕)做能否繼續使用的判斷
                // TODO
                checkButton.setEnabled(true);
            }
        });

        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 顯示上一張卡片，也就是索引為 game_map_index - 1 的 game_maps_name 內的面板
                nextRoundButton.setEnabled(false);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        nextRoundButton.setEnabled(game.goStage(true));
                        return null;
                    }
                };
                worker.execute();

                // 確認是否為最後一題或第一題，並對他們(上下關按鈕)做能否繼續使用的判斷
                // TODO
                checkButton.setEnabled(true);
            }
        });
    }

    private void switchMode() {
        if (editMode) {
            for (Component component : problemComponents) {
                ControlPanel.remove(component);
                System.out.println("remove");
            }
            for (Component component : editComponents) {
                ControlPanel.add(component);
                System.out.println("add");
            }
            for (Component label : editLabels) {
                ControlPanel.add(label);
                System.out.println("add");
            }

        } else {
            for (Component label : editLabels) {
                ControlPanel.remove(label);
                System.out.println("remove");
            }
            for (Component component : editComponents) {
                ControlPanel.remove(component);
                System.out.println("add");
            }
            for (Component component : problemComponents) {
                ControlPanel.add(component);
                System.out.println("add");
            }

        }
        ControlPanel.revalidate();
        ControlPanel.repaint();

    }

    public GameControlPanel(Game game) {
        this.game = game;
        ControlPanel.setLayout(null);

        switchModeButton.setBounds(10, 0, 304, 70);
        lastRoundButton.setBounds(10, 370, 150, 70);
        nextRoundButton.setBounds(164, 370, 150, 70);
        checkButton.setBounds(10, 450, 150, 70);
        restrartButton.setBounds(164, 450, 150, 70);

        strightPipe.setBounds(10, 100, 60, 60);
        strightPipe.setIcon(PIPEIMAGE.STRAIGHT_PIPE.getImage(0, 60, 60));
        bentPipe.setBounds(132, 100, 60, 60);
        bentPipe.setIcon(PIPEIMAGE.BENT_PIPE.getImage(0, 60, 60));
        tPipe.setBounds(254, 100, 60, 60);
        tPipe.setIcon(PIPEIMAGE.T_PIPE.getImage(0, 60, 60));
        crossPipe.setBounds(10, 180, 60, 60);
        crossPipe.setIcon(PIPEIMAGE.CROSS_PIPE.getImage(0, 60, 60));
        waterStorage.setBounds(132, 180, 60, 60);
        waterStorage.setIcon(PIPEIMAGE.UP_IN_WATER_STORE.getImage(0, 60, 60));
        waterStorageWithWater.setBounds(254, 180, 60, 60);
        waterStorageWithWater.setIcon(PIPEIMAGE.UP_IN_WATER_STORE_WITH_WATER.getImage(0, 60, 60));

        appendRowButton.setBounds(10, 290, 150, 70);
        removeRowButton.setBounds(164, 290, 150, 70);
        appendColButton.setBounds(10, 370, 150, 70);
        removeColButton.setBounds(164, 370, 150, 70);
        deleteButton.setBounds(10, 450, 150, 70);
        saveButton.setBounds(164, 450, 150, 70);
        setEventLister();

        switchModeButton.setFont(usingFont);
        ControlPanel.add(switchModeButton);

        for (Component component : problemComponents) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.setFont(usingFont);
                ControlPanel.add(jComponent);
            }
        }

        for (Component component : editComponents) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.setFont(usingFont);
            }
        }

        lastRoundButton.setEnabled(false);
        nextRoundButton.setEnabled(false);

        // 在构造函数中创建并添加游戏控制面板的组件，如按钮、文本框等
        // 并注册事件监听器来处理用户输入和控制游戏逻辑
    }

    // 其他游戏控制逻辑和方法
}