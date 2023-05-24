import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class Game {
    GamePanel gamePanel = new GamePanel();
    MapStorage storage = new MapStorage();
    ArrayList<GameMap> maps = new ArrayList<>();
    int nowMapIndex = 0;

    boolean gameOver = false;
    boolean flowing = false;
    boolean lastIsReady = false;
    boolean nextIsReady = false;

    GameMap editGameMap;
    boolean editMode = false;
    int width;
    int height;
    String selectPipeCode;

    Game() {
        editMode = false;
        makeMaps();
        bindPanel(maps.get(nowMapIndex), gamePanel.getnowPanel());
        if (nowMapIndex != 0) {
            lastIsReady = false;
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bindPanel(maps.get(nowMapIndex - 1), gamePanel.getLastPanel());
                    lastIsReady = true;
                    return null;
                }
            };
            worker.execute();
        }

        if (nowMapIndex != maps.size() - 1) {
            nextIsReady = false;
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bindPanel(maps.get(nowMapIndex + 1), gamePanel.getNextPanel());
                    nextIsReady = true;
                    return null;
                }
            };
            worker.execute();
        }
    }

    public boolean goStage(boolean next) {
        flowing = false;
        gamePanel.goNext(next);
        lastIsReady = false;
        nextIsReady = false;

        if (!next && nowMapIndex != 0) {
            nowMapIndex -= 1;
            nextIsReady = true;
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bindPanel(maps.get(nowMapIndex - 1), gamePanel.getLastPanel());
                    lastIsReady = true;
                    return null;
                }
            };
            worker.execute();

            return true;
        }

        if (next && nowMapIndex != maps.size() - 1) {
            nowMapIndex += 1;
            lastIsReady = true;
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bindPanel(maps.get(nowMapIndex + 1), gamePanel.getNextPanel());
                    nextIsReady = true;
                    return null;
                }
            };
            worker.execute();
            return true;
        }

        return false;
    }

    // 回傳遊戲面板
    public JPanel getPanel() {
        return gamePanel.mainPanel;
    }

    private void makeMaps() {
        for (ArrayList<ArrayList<String>> map : storage.getAllMapFiles()) {
            GameMap gameMap = new GameMap(map);
            maps.add(gameMap);
        }
    }

    private GameMap getNowMap() {
        return editMode ? editGameMap : maps.get(nowMapIndex);
    }

    public void render() {
        JPanel panel = gamePanel.getnowPanel();
        GameMap gameMap = getNowMap();
        Component[] components = panel.getComponents();

        int originaLength = gameMap.width * gameMap.height;

        for (int row = 0; row < gameMap.height; row++) {
            for (int col = 0; col < gameMap.width; col++) {
                if (originaLength != components.length)
                    return;
                Component component = components[row * gameMap.width + col];
                Pipe pipe = gameMap.getPipes().get(row).get(col);
                if (!pipe.empty && component instanceof JLabel && pipe.change) {
                    JLabel element = (JLabel) component;
                    element.setIcon(pipe.getImage());
                    pipe.change = false;
                }
            }
        }

    }

    private void bindPanel(GameMap gameMap, JPanel panel) {
        panel.removeAll();// 把面板上的 JLabl 全部移除
        panel.revalidate();// 並讓它重新可用

        // 計算圖片的寬及高
        int elementWidth = panel.getWidth() / gameMap.width;
        int elementHeight = panel.getHeight() / gameMap.height;

        // 遍歷 nowMap 內的每個單位
        for (int row = 0; row < gameMap.height; row++) {
            for (int col = 0; col < gameMap.width; col++) {
                JLabel element = new JLabel();// 創建新的 JLabel
                element.setBounds(elementWidth * col, elementHeight * row, elementWidth, elementHeight);// 計算 JLabel
                                                                                                        // 在面板應所處的座標，及給定寬度及高度
                panel.add(element);// 把 JLabel 加進面板

                Pipe pipe = gameMap.getPipes().get(row).get(col);
                if (pipe.empty) {// 如果他不是水管或水庫，是空的，那繼續做下一個
                    continue;
                }
                pipe.setSize(elementWidth, elementHeight);

                element.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// 設定畫數在水管圖片上的樣式
                element.setIcon(pipe.getImage());// 再縮放，並設定它為 JLabel 的 icon
                element.addMouseListener(new MouseAdapter() {// 對每個 JLabel 設定點擊的事件監聽器
                    // 取得相關於綁定的面板及步數限制
                    int labelWidth = elementWidth;
                    Pipe nowPipe = pipe;

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (flowing) {// 如果水正在流，不做任何事
                            System.out.println("forbidden");
                            return;
                        }

                        int clickX = e.getX();
                        nowPipe.rotate(clickX >= labelWidth / 2);
                    }
                });
            }
        }

    }

    public void restart() {
        GameMap nowGameMap = maps.get(nowMapIndex);
        nowGameMap.restart();
        flowing = false;
    }

    public boolean check() {
        GameMap nowGameMap = maps.get(nowMapIndex);
        flowing = true;
        nowGameMap.startFlow();
        System.out.println(nowGameMap.waste + " " + nowGameMap.endWithWater());
        return !nowGameMap.waste && nowGameMap.endWithWater();
    }

    public void switchMode() {
        editMode = !editMode;
        gamePanel.switchMode();
        if (editMode) {
            width = 5;
            height = 5;
            selectPipeCode = "";
            editGameMap = new GameMap(width, height);
            initEditPanel();
        }
    }

    private void initEditPanel() {
        JPanel editPanel = gamePanel.getnowPanel();
        editPanel.removeAll();
        editPanel.revalidate();
        int elementWidth = editPanel.getWidth() / width;
        int elementHeight = editPanel.getHeight() / height;

        ArrayList<ArrayList<Pipe>> editPipes = editGameMap.getPipes();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                JLabel element = new JLabel();// 創建新的 JLabel
                Pipe pipe = editPipes.get(row).get(col);
                element.setBounds(elementWidth * col, elementHeight * row, elementWidth, elementHeight);// 計算 JLabel
                                                                                                        // 在面板應所處的座標，及給定寬度及高度
                editPanel.add(element);// 把 JLabel 加進面板
                element.setBorder(BorderFactory.createLineBorder(Color.GREEN, 10));
                element.addMouseListener(new MouseAdapter() {
                    Pipe nowPipe = pipe;
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        element.setBorder(BorderFactory.createLineBorder(Color.GREEN, 10));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        element.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (selectPipeCode.length() != 0)
                            nowPipe.setSourceCode(selectPipeCode, 1);
                    }
                });

            }
        }
    }

    public void selectPipe(String pipeCode){
        selectPipeCode = pipeCode;
    }

    public void cancelSelect(){
        selectPipeCode = "";
    }

}
