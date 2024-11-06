public class TaxiDemo {
	public static final int NUM_OF_CLIENT = 8;

	public static void main(String[] args) {
		TaxiPool taxiPool = new TaxiPool();
		for (int i = 0; i < NUM_OF_CLIENT; i++) {
			Runnable client = new TaxiClientThread(taxiPool);
			Thread thread = new Thread(client);
			thread.start();
		}
	}
}
