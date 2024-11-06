import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trong đoạn code bên dưới, tôi sẽ cài đặt mô phỏng với TaxiPool quản lý được 4 taxi, cùng lúc có 8 cuộc gọi
 * của khách hàng đến công ty để gọi xe, thời gian mỗi taxi đến địa điểm chở khách là 200ms, mỗi taxi chở khách
 * trong khoảng thời gian từ 1000ms đến 1500ms (ngẫu nhiên), mỗi khách hàng chịu chờ tối đa 1200ms trước khi hủy.
 */

public class TaxiPool {
	private static final long EXPIRED_TIME_IN_MILLISECOND = 2000;
	private static final int NUMBER_OF_TAXI = 4;
	private final List<Taxi> available = Collections.synchronizedList(new ArrayList<>());
	private final List<Taxi> inUse = Collections.synchronizedList(new ArrayList<>());
	private final AtomicInteger count = new AtomicInteger(0);
	private final AtomicBoolean waiting = new AtomicBoolean(false);

	public void release(Taxi taxi) {    // if release is synchronized, it will have to wait after all getTaxi done
		// --> 1 object only has 1 monitor lock for all methods?
		inUse.remove(taxi);
		available.add(taxi);
		System.out.println(taxi.getName() + " is free");
	}

	public synchronized Taxi getTaxi() {
		if (!available.isEmpty()) {
			Taxi taxi = available.remove(0);
			inUse.add(taxi);
			return taxi;
		}
		if (count.get() == NUMBER_OF_TAXI) {
			this.waitingUntilTaxiAvailable();
			return this.getTaxi();
		}
		Taxi taxi = this.createTaxi();
		inUse.add(taxi);
		return taxi;
	}

	private Taxi createTaxi() {
		waiting(200);
		Taxi taxi = new Taxi("Taxi " + count.incrementAndGet());
		System.out.println(taxi.getName() + " is Created");
		return taxi;
	}

	private void waitingUntilTaxiAvailable() {
		if (waiting.get()) {
			waiting.set(false);
			throw new TaxiNotFoundException("No taxi avaiable");
		}
		waiting.set(true);
		waiting(EXPIRED_TIME_IN_MILLISECOND);
	}

	private void waiting(long time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
}
