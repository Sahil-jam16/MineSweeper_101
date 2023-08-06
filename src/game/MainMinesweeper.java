package game;

class MainMinesweeper {
	static String FilePath = "G:\\College Work\\SEMESTER IV\\JAVA LAB\\HUSTLE\\MineSweeper_101\\Log Record\\log_record.txt";

	public static void main(String[] args) {

		ExecutionLogger logger = new ExecutionLogger(FilePath);
		logger.logStart();

		new WorldMinesweeper(WorldMinesweeper.DIFFICULTY[1]);

		logger.logEnd();
	}
}