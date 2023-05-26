import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class GameMap {
    ArrayList<ArrayList<Pipe>> pipeMap = new ArrayList<>();
    int width;
    int height;
    ArrayList<ArrayList<String>> originalMapFile = null;// 當前地圖(文字檔)
    ArrayList<int[]> start = new ArrayList<>();// 地圖的起點(有水的水庫)
    ArrayList<int[]> end = new ArrayList<>();// 地圖的終點(沒水的水庫)

    boolean waste = false;
    boolean finish = false;
    ArrayList<int[]> waterCoordinates;
    ArrayList<int[]> nextCoordinates;

    GameMap(int width, int height) {
        for (int row = 0; row < height; row++) {
            ArrayList<Pipe> pipes = new ArrayList<>();
            for (int col = 0; col < width; col++) {
                Pipe pipe = new Pipe("--");
                pipes.add(pipe);
            }
            pipeMap.add(pipes);
        }
    }

    GameMap(ArrayList<ArrayList<String>> mapFile) {
        originalMapFile = mapFile;// 記住當前地圖檔
        // 初始化
        width = originalMapFile.get(0).size();// 取得寬度
        height = originalMapFile.size();// 取得高度
        start = new ArrayList<>();// 初始化起點
        end = new ArrayList<>();// 初始化終點

        for (int row = 0; row < height; row++) {
            ArrayList<Pipe> pipes = new ArrayList<>();
            for (int col = 0; col < width; col++) {
                Pipe tmpPipe = new Pipe(originalMapFile.get(row).get(col));
                pipes.add(tmpPipe);

                if (tmpPipe.empty)
                    continue;
                if (tmpPipe.isWaterStorage()) {
                    if (tmpPipe.withWater)// 儲存起點座標
                        start.add(new int[] { row, col });
                    else// 儲存終點座標
                        end.add(new int[] { row, col });
                }
            }
            pipeMap.add(pipes);
        }
    }

    public void restart() {// restart 用的 method ，參數是二維的 String 的 ArrayList
        waste = false;
        finish = false;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Pipe tmpPipe = pipeMap.get(row).get(col);
                tmpPipe.init();
            }
        }

    }

    public ArrayList<ArrayList<Pipe>> getPipes() {
        return pipeMap;
    }

    public boolean endWithWater() {
        for (int[] coordinate : end) {
            Pipe pipe = pipeMap.get(coordinate[0]).get(coordinate[1]);
            if (!pipe.withWater)
                return false;
        }
        return true;
    }

    public void startFlow() {
        waterCoordinates = start;

        Timer timer = null;
        CountDownLatch latch = new CountDownLatch(1);// 等待計時器執行完畢，才能回傳玩家是否成功
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// 使用 BFS 進行每次水管流向的確認
                BFS();
                if (finish) {
                    ((Timer) e.getSource()).stop();// 停止計時器
                    latch.countDown();
                }
            }
        });
        timer.start();// 開始計時器
        try {
            latch.await();// 等待計時器結束
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    private void BFS() {
        nextCoordinates = new ArrayList<>();
        for (int[] coordinate : waterCoordinates) {// 遍歷 waterPipe 內的每個水管，裡面的每個水管都是有水流過的
            // 取得座標
            int pipeRow = coordinate[0];
            int pipeCol = coordinate[1];

            Pipe nowPipe = pipeMap.get(pipeRow).get(pipeCol);
            if (nowPipe.empty)
                continue;
            boolean[] flowDirections = nowPipe.nextDirection();

            int[] fromDirection = new int[] { 3, 4, 1, 2 };
            boolean[] conditions = new boolean[] { pipeRow != 0, pipeCol != width - 1, pipeRow != height - 1,
                    pipeCol != 0 };
            int[] rowDiff = new int[] { -1, 0, 1, 0 };
            int[] colDiff = new int[] { 0, 1, 0, -1 };

            for (int i = 0; i < 4; i++) {
                if (!flowDirections[i]) { // 如果現在這個水管不能流這個方向
                    continue; // 跳過這個座標
                }

                if (!conditions[i]) { // 如果超出邊界
                    waste = true; // 如果超出邊界 浪費水
                    continue;// 跳過這個座標
                }

                int[] nextPipeCoordinate = new int[] { pipeRow + rowDiff[i], pipeCol + colDiff[i] }; // 取得下個座標
                if (isInNextCoordinates(nextPipeCoordinate)) {// 如果座標在下次要看的座標陣列裡面
                    continue;// 跳過這個座標
                }

                Pipe nextPipe = pipeMap.get(nextPipeCoordinate[0]).get(nextPipeCoordinate[1]);

                if (nextPipe.empty) {// 如果 pipe 是空的
                    waste = true;// 如果 pipe 是空的 浪費水
                    continue;// 跳過這個方向
                }

                nextPipe.waterPast(fromDirection[i]); // 讓水流過 nextPipe
                if (nextPipe.withWater) // 如果 nextPipe 是可以流過去的
                    nextCoordinates.add(nextPipeCoordinate); // 加進下次要看的 陣列
                else
                    waste = true;// 不然就是浪費水

            }
        }
        waterCoordinates = nextCoordinates;
        finish = waterCoordinates.size() == 0;
    }

    private boolean isInNextCoordinates(int[] coordinate) {
        for (int[] innerCoordinate : nextCoordinates) {
            if (coordinate[0] == innerCoordinate[0] && coordinate[1] == innerCoordinate[1])
                return true;
        }
        return false;
    }

    public void save() {
        try {
            String filename = "src/GameMapTxtFiles/map.txt";

            FileWriter fileWriter = new FileWriter(filename, true);
            fileWriter.write("\n");

            for (int row = 0; row < pipeMap.size(); row++) {
                for (int col = 0; col < pipeMap.get(0).size(); col++) {
                    String content = pipeMap.get(row).get(col).sourceCode;
                    fileWriter.write(content + " ");
                }
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}