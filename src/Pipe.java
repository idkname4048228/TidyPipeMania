
import java.util.Random;
import javax.swing.ImageIcon;

public class Pipe {
    // 遊戲邏輯所需的 attribute
    boolean empty = false;
    String sourceCode;
    String pipeCode;
    int degreeUnit;
    boolean[] flowDirections;
    boolean withWater = false;

    // 顯示圖片所需的 attribute
    int width;
    int height;
    PIPEIMAGE pipeImage;
    PIPEIMAGE[] imageStorage;

    // 水管圖片需要更新
    boolean change = false;

    Pipe(String code) {
        // 把原本的 Code String 記下來，重新開始 (restart) 時可以用到
        sourceCode = code;
        init();
    }

    // 讓水管根據 sourceCode 做相關的動作
    public void init() {
        // 如果 sourceCode 是 -- ，那代表是空的
        if (sourceCode.substring(0, 1).equals("-")) {
            empty = true;
            return;
        }
        // 初始化沒有水，但是需要更換圖片(可能原本有水)
        withWater = false;
        change = true;

        // 取得水管代號以及旋轉角度
        pipeCode = sourceCode.substring(0, 1);
        degreeUnit = Integer.valueOf(sourceCode.substring(1));
        // 如果角度代號是 5 ，代表要隨機產生一個合法的代號
        if (degreeUnit == 5) {
            degreeUnit = new Random().nextInt(4) + 1;
        }

        // 根據不同的水管代號指定不同的 PIPEIMAGE 的 enum
        switch (pipeCode) {
            // 直的
            case "s":
                pipeImage = PIPEIMAGE.STRAIGHT_PIPE;
                break;
            // 彎的
            case "b":
                pipeImage = PIPEIMAGE.BENT_PIPE;
                break;
            // T 字
            case "t":
                pipeImage = PIPEIMAGE.T_PIPE;
                break;
            // 交叉
            case "c":
                degreeUnit = 1;
                pipeImage = PIPEIMAGE.CROSS_PIPE;
                break;

            // 有水的水庫
            case "W":
                // 所以他有水，設定 withWater 為 true
                withWater = true;
                // 設定相關圖片集
                imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE_WITH_WATER,
                        PIPEIMAGE.RIGHT_IN_WATER_STORE_WITH_WATER, PIPEIMAGE.DOWN_IN_WATER_STORE_WITH_WATER,
                        PIPEIMAGE.LEFT_IN_WATER_STORE_WITH_WATER };
                // 取得對應角度的圖片(代號 1-index 索引 0-index)
                pipeImage = imageStorage[degreeUnit - 1];

                // 設定可以流動的方向為對應角度(只有那個方向可以流水出去)
                flowDirections = new boolean[] { false, false, false, false };
                flowDirections[degreeUnit - 1] = true;
                break;
            // 沒水的水庫
            case "w":
                // 設定相關圖片集
                imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE, PIPEIMAGE.RIGHT_IN_WATER_STORE,
                        PIPEIMAGE.DOWN_IN_WATER_STORE, PIPEIMAGE.LEFT_IN_WATER_STORE };
                // 取得對應角度的圖片(代號 1-index 索引 0-index)
                pipeImage = imageStorage[degreeUnit - 1];
                break;
        }
    }

    // 回傳是否為水庫，不管有沒有水
    public boolean isWaterStorage() {
        return pipeCode.equals("w") || pipeCode.equals("W");
    }

    // 設定該顯示的大小，之後需要回傳圖片時會用到
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // 回傳所屬的 PIPEIMAGE 的 imageIcon
    public ImageIcon getImage() {
        // 如果是水庫
        if (pipeCode.equals("W") || pipeCode.equals("w")) {
            // 那圖片不用旋轉(角度為 0) ，只需要改變大小
            return pipeImage.getImage(0, width, height);
        }
        // 如果不是水庫
        // 那圖片需要根據相關的角度及大小旋轉及縮放
        return pipeImage.getImage(90 * (degreeUnit - 1), width, height);
    }

    // 水管旋轉
    public boolean rotate(boolean right) {
        // 如果是交叉水管
        if (pipeCode.equals("c"))
            // 不做任何事
            return false;

        // 如果向右轉
        if (right) {
            // degreeUnit 在 1-4 的範圍裡加 1
            degreeUnit = (degreeUnit % 4) + 1;
            // 如果向左轉
        } else {
            // degreeUnit 在 1-4 的範圍裡減 1
            degreeUnit = (degreeUnit + 2) % 4 + 1;
        }

        // 如果是水庫
        // 是有水的
        if (pipeCode.equals("W")) {
            // 圖片要更新，從圖片集拿相關角度的圖片
            pipeImage = imageStorage[degreeUnit - 1];
            // 更動水可以流動的方向
            flowDirections = new boolean[] { false, false, false, false };
            flowDirections[degreeUnit - 1] = true;
        }
        // 是沒水的
        if (pipeCode.equals("w")) {
            // 圖片要更新，從圖片集拿相關角度的圖片
            pipeImage = imageStorage[degreeUnit - 1];
        }

        // 圖片動過了，下次渲染須更新
        change = true;

        return true;
    }

    // 水流過去的 method，參數是從哪裡流過來的角度代號
    public void waterPast(int from) {
        // 預設無法向四周流動
        // 如果下面都沒有改動到這個 array ，那代表這個水管已經有水，並且流過了，避免重複流動
        flowDirections = new boolean[] { false, false, false, false };
        switch (pipeCode) {
            // 直的
            case ("s"):
                // 如果沒有水
                if (!withWater) {
                    // 更新水流方向
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit + 1) % 4] = true;
                    // 如果水是可以從 來自(from) 的方向流過來的
                    if (flowDirections[from - 1]) {
                        // 設為有水的
                        withWater = true;
                        // 更新圖片
                        pipeImage = PIPEIMAGE.STRAIGHT_PIPE_WITH_WATER;
                    }
                }
                break;

            // 彎的
            case ("b"):
                // 如果沒有水
                if (!withWater) {
                    // 更新水流方向
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit % 4)] = true;
                    // 如果水是可以從 來自(from) 的方向流過來的
                    if (flowDirections[from - 1]) {
                        // 設為有水的
                        withWater = true;
                        // 更新圖片
                        pipeImage = PIPEIMAGE.BENT_PIPE_WITH_WATER;
                    }
                }
                break;

            // T 字
            case ("t"):
                // 如果沒有水
                if (!withWater) {
                    // 更新水流方向
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit % 4)] = true;
                    flowDirections[(degreeUnit + 2) % 4] = true;
                    // 如果水是可以從 來自(from) 的方向流過來的
                    if (flowDirections[from - 1]) {
                        // 設為有水的
                        withWater = true;
                        // 更新圖片
                        pipeImage = PIPEIMAGE.T_PIPE_WITH_WATER;
                    }
                }
                break;

            // 交叉
            case ("c"):
                // 如果有水
                if (withWater) {
                    // 檢查是否是另一個方向
                    // 是的話
                    if (degreeUnit != 3 && degreeUnit % 2 != (from + 1) % 2) {
                        // 更新水流方向
                        flowDirections[from - 1] = true;
                        flowDirections[(from + 1) % 4] = true;
                        // 根據水流方向改變角度(3 代表兩邊都有水流過)
                        degreeUnit = 3;
                        // 更新圖片
                        pipeImage = PIPEIMAGE.CROSS_PIPE_WITH_TWO_WATER;
                    }
                    // 如果沒有水
                } else {
                    // 更新水流方向
                    flowDirections[from - 1] = true;
                    flowDirections[(from + 1) % 4] = true;
                    // 如果水是可以從 來自(from) 的方向流過來的
                    if (flowDirections[from - 1]) {
                        // 設為有水的
                        withWater = true;
                        // 根據水流方向改變角度(因為圖片的水流是水平，如果是垂直就要旋轉 90 度)
                        degreeUnit = (from % 2 == 1) ? 2 : 1;
                        // 更新圖片
                        pipeImage = PIPEIMAGE.CROSS_PIPE_WITH_ONE_WATER;
                    }
                }
                break;

            // 沒水的水庫
            case ("w"):
                // 如果水流的方向跟角度代號一樣，那代表水庫可以接到水
                if (from == degreeUnit) {
                    // 設為有水的
                    withWater = true;
                    // 因為是有水的，所以圖片集需要更新
                    imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE_WITH_WATER,
                            PIPEIMAGE.RIGHT_IN_WATER_STORE_WITH_WATER, PIPEIMAGE.DOWN_IN_WATER_STORE_WITH_WATER,
                            PIPEIMAGE.LEFT_IN_WATER_STORE_WITH_WATER };
                    // 從圖片集拿相關角度的圖片
                    pipeImage = imageStorage[degreeUnit - 1];
                }
                break;
        }
        // 圖片更新過了
        change = true;
        // 水不會往回流
        flowDirections[from - 1] = false;
    }

    // 回傳自己可以讓水流動的方向
    public boolean[] nextDirection() {
        return flowDirections;
    }

}
