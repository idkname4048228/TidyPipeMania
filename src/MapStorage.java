import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapStorage {
	ArrayList<ArrayList<ArrayList<String>>> storage = new ArrayList<>();
	int nowIndex = 0;

	MapStorage() {
		nowIndex = 0;
		if (storage.size() == 0)
			storage = readMapFile();
	}

	protected ArrayList<ArrayList<ArrayList<String>>> readMapFile() {// 讀地圖檔，並把他們都放到 mapStorage 裡面
		/*
		 * StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		 * 
		 * System.out.println(Thread.currentThread().getStackTrace()); for
		 * (StackTraceElement element : stackTrace) {
		 * System.out.println(element.getClassName() + " - " + element.getMethodName());
		 * }
		 * 
		 * LocalDateTime currentTime = LocalDateTime.now(); DateTimeFormatter formatter
		 * = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); String
		 * formattedTime = currentTime.format(formatter);
		 * System.out.println("start time: " + formattedTime);
		 */
		System.out.println("reading");
		File f = new File("src/GameMapTxtFiles/map.txt");
		Scanner sc = null;
		ArrayList<String> tmpArray = new ArrayList<String>();
		ArrayList<ArrayList<ArrayList<String>>> returnList = new ArrayList<>();
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (sc.hasNext()) {
			String tmp = sc.nextLine();
			if (tmp.length() == 0) {
				ArrayList<ArrayList<String>> tmpStrArray = new ArrayList<>();
				for (int i = 0; i < tmpArray.size(); i++) {
					ArrayList<String> rowArray = new ArrayList<String>();
					for (String unit : tmpArray.get(i).split(" ")) {
						rowArray.add(unit);
					}
					tmpStrArray.add(rowArray);
				}
				tmpArray.clear();
				returnList.add(tmpStrArray);
			} else {
				tmpArray.add(tmp);
			}
		}
		if (tmpArray.size() != 0) {
			ArrayList<ArrayList<String>> tmpStrArray = new ArrayList<>();
			for (int i = 0; i < tmpArray.size(); i++) {
				ArrayList<String> rowArray = new ArrayList<String>();
				for (String unit : tmpArray.get(i).split(" ")) {
					rowArray.add(unit);
				}
				tmpStrArray.add(rowArray);
			}
			tmpArray.clear();
			returnList.add(tmpStrArray);
		}
		System.out.println(returnList.size());
		return returnList;
	}

	public ArrayList<ArrayList<ArrayList<String>>> getAllMapFiles() {
		return storage;
	}

	public void mapMove(boolean next) {
		if (nowIndex != storage.size() - 1 && next) {
			nowIndex += 1;
		}
		if (nowIndex != 0 && !next) {
			nowIndex -= 1;
		}
	}

	public ArrayList<ArrayList<String>> getNowMap() {
		return storage.get(nowIndex);
	}

	public ArrayList<ArrayList<String>> getNextMap() {
		if (nowIndex == storage.size() - 1)
			return null;
		return storage.get(nowIndex + 1);
	}

	public ArrayList<ArrayList<String>> getLastMap() {
		if (nowIndex == 0)
			return null;
		return storage.get(nowIndex - 1);
	}

}
