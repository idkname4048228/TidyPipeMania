
import java.util.Random;
import javax.swing.ImageIcon;

public class Pipe {
    boolean empty = false;
    String sourceCode;
    String pipeCode;
    int degreeUnit;
    boolean[] flowDirections;
    boolean withWater = false;

    int width;
    int height;
    PIPEIMAGE pipeImage;
    PIPEIMAGE[] imageStorage;

    boolean change = false;

    Pipe(String code) {
        sourceCode = code;
        init();
    }

    public void init() {
        if (sourceCode.substring(0, 1).equals("-")) {
            empty = true;
            return;
        }
        withWater = false;
        change = true;

        pipeCode = sourceCode.substring(0, 1);
        degreeUnit = Integer.valueOf(sourceCode.substring(1));
        if (degreeUnit == 5) {
            degreeUnit = new Random().nextInt(4) + 1;
        }
        switch (pipeCode) {
            case "s":
                pipeImage = PIPEIMAGE.STRAIGHT_PIPE;
                break;
            case "b":
                pipeImage = PIPEIMAGE.BENT_PIPE;
                break;
            case "t":
                pipeImage = PIPEIMAGE.T_PIPE;
                break;
            case "c":
                degreeUnit = 1;
                pipeImage = PIPEIMAGE.CROSS_PIPE;
                break;
            case "W":
                withWater = true;
                imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE_WITH_WATER,
                        PIPEIMAGE.RIGHT_IN_WATER_STORE_WITH_WATER, PIPEIMAGE.DOWN_IN_WATER_STORE_WITH_WATER,
                        PIPEIMAGE.LEFT_IN_WATER_STORE_WITH_WATER };
                pipeImage = imageStorage[degreeUnit - 1];
                flowDirections = new boolean[] { false, false, false, false };
                flowDirections[degreeUnit - 1] = true;
                break;
            case "w":
                imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE, PIPEIMAGE.RIGHT_IN_WATER_STORE,
                        PIPEIMAGE.DOWN_IN_WATER_STORE, PIPEIMAGE.LEFT_IN_WATER_STORE };
                pipeImage = imageStorage[degreeUnit - 1];
                break;
        }
    }

    public boolean isWaterStorage() {
        return pipeCode.equals("w") || pipeCode.equals("W");
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ImageIcon getImage() {
        if (pipeCode.equals("W") || pipeCode.equals("w")) {
            return pipeImage.getImage(0, width, height);
        }
        return pipeImage.getImage(90 * (degreeUnit - 1), width, height);
    }

    public boolean rotate(boolean right) {
        if (pipeCode.equals("c"))
            return false;

        if (right) {
            degreeUnit = (degreeUnit % 4) + 1;
        } else {
            degreeUnit = (degreeUnit + 2) % 4 + 1;
        }

        if (pipeCode.equals("W")) {
            pipeImage = imageStorage[degreeUnit - 1];
            flowDirections = new boolean[] { false, false, false, false };
            flowDirections[degreeUnit - 1] = true;
        }
        if (pipeCode.equals("w")) {
            pipeImage = imageStorage[degreeUnit - 1];
        }
        change = true;

        return true;
    }

    public void waterPast(int from) {
        flowDirections = new boolean[] { false, false, false, false };
        switch (pipeCode) {
            case ("s"):
                if (!withWater) {
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit + 1) % 4] = true;
                    if (flowDirections[from - 1]) {
                        withWater = true;
                        pipeImage = PIPEIMAGE.STRAIGHT_PIPE_WITH_WATER;
                    }
                }
                break;

            case ("b"):
                if (!withWater) {
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit % 4)] = true;
                    if (flowDirections[from - 1]) {
                        withWater = true;
                        pipeImage = PIPEIMAGE.BENT_PIPE_WITH_WATER;
                    }
                }
                break;

            case ("t"):
                if (!withWater) {
                    flowDirections[degreeUnit - 1] = true;
                    flowDirections[(degreeUnit % 4)] = true;
                    flowDirections[(degreeUnit + 2) % 4] = true;
                    if (flowDirections[from - 1]) {
                        withWater = true;
                        pipeImage = PIPEIMAGE.T_PIPE_WITH_WATER;
                    }
                }
                break;

            case ("c"):
                if (withWater) {
                    if (degreeUnit != 3 && degreeUnit % 2 != (from + 1) % 2) {
                        degreeUnit = 3;
                        pipeImage = PIPEIMAGE.CROSS_PIPE_WITH_TWO_WATER;
                        flowDirections[from - 1] = true;
                        flowDirections[(from + 1) % 4] = true;
                    }
                } else {
                    flowDirections[from - 1] = true;
                    flowDirections[(from + 1) % 4] = true;
                    if (flowDirections[from - 1]) {
                        withWater = true;
                        degreeUnit = (from % 2 == 1) ? 2 : 1;
                        pipeImage = PIPEIMAGE.CROSS_PIPE_WITH_ONE_WATER;
                    }
                }
                break;

            case ("w"):
                if (from == degreeUnit) {
                    withWater = true;
                    imageStorage = new PIPEIMAGE[] { PIPEIMAGE.UP_IN_WATER_STORE_WITH_WATER,
                            PIPEIMAGE.RIGHT_IN_WATER_STORE_WITH_WATER, PIPEIMAGE.DOWN_IN_WATER_STORE_WITH_WATER,
                            PIPEIMAGE.LEFT_IN_WATER_STORE_WITH_WATER };
                    pipeImage = imageStorage[degreeUnit - 1];
                }
                break;
        }
        change = true;
        flowDirections[from - 1] = false;
    }

    public boolean[] nextDirection() {
        return flowDirections;
    }

}
