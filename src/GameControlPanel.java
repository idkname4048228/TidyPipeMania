import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameControlPanel extends JPanel {
    Panel ControlPanel = new Panel();
    Game game;
    Font usingFont = new Font("微軟正黑體", Font.BOLD, 24);

    JButton lastRoundButton = new JButton("上一關");
    JButton nextRoundButton = new JButton("下一關");
    JButton restrartButton = new JButton("重新開始");
    JButton checkButton = new JButton("確認");

    Component[] components = { lastRoundButton, nextRoundButton, restrartButton,
            checkButton };

    private void setEventLister() {
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

    public GameControlPanel(Game game) {
        this.game = game;
        ControlPanel.setLayout(null);

        lastRoundButton.setBounds(10, 450, 150, 70);
        nextRoundButton.setBounds(164, 450, 150, 70);
        checkButton.setBounds(10, 370, 150, 70);
        restrartButton.setBounds(164, 370, 150, 70);

        setEventLister();

        for (Component component : components) {
            if (component instanceof JComponent) {
                JComponent jComponent = (JComponent) component;
                jComponent.setFont(usingFont);
                ControlPanel.add(jComponent);
            }
        }

        lastRoundButton.setEnabled(false);
        nextRoundButton.setEnabled(false);

        // 在构造函数中创建并添加游戏控制面板的组件，如按钮、文本框等
        // 并注册事件监听器来处理用户输入和控制游戏逻辑
    }

    // 其他游戏控制逻辑和方法
}