package com.likya.tlossw.core.spc.helpers;

import java.util.Calendar;
import java.util.Date;

import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public final class LikyaDayKeeper implements Runnable {

	private boolean executePermission = true;

	private Thread myExecuter;

	private static LikyaDayKeeper ref;

	private static SpaceWideRegistry spaceWideRegistry;

	private Date solsticeTime = null;

	private static int fONCE_PER_PERIOD;

	private boolean isForced = false;

	public static LikyaDayKeeper getInstance(SpaceWideRegistry spaceWideRegistry) {

		if (ref == null) {
			ref = new LikyaDayKeeper(spaceWideRegistry);
		}
		return ref;
	}

	private LikyaDayKeeper(SpaceWideRegistry spaceWideRegistry) {

		LikyaDayKeeper.spaceWideRegistry = spaceWideRegistry;

		Calendar solsticeCalendar = Calendar.getInstance();
		solsticeCalendar = DateUtils.getSolsticeDateTime(solsticeCalendar, spaceWideRegistry.getTlosSWConfigInfo().getSettings().getSolstice().getTime());

		solsticeTime = solsticeCalendar.getTime();

		fONCE_PER_PERIOD = spaceWideRegistry.getTlosSWConfigInfo().getSettings().getPeriod().getPeriodValue().intValue();

	}

	public void run() {

		while (executePermission) {

			Date currentTime = Calendar.getInstance().getTime();

			if (solsticeTime.before(currentTime)) {

				SpaceWideRegistry.getGlobalLogger().info("");
				SpaceWideRegistry.getGlobalLogger().info("   > Gündönümü gelmiştir !!");

				shiftSolsticeTime();

				SpaceWideRegistry.getGlobalLogger().info("   > is yuku islenmesi sureci baslamistir !!");
				spaceWideRegistry.getSpaceWideReference().startCpc();

			} else if (isForced) {

				isForced = false;
				spaceWideRegistry.getSpaceWideReference().startCpc();

			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void shiftSolsticeTime() {

		Calendar nextTime = Calendar.getInstance();

		SpaceWideRegistry.getGlobalLogger().info("   > Şimdi : " + DateUtils.getDate(nextTime.getTime()));

		nextTime.setTime(solsticeTime);
		nextTime.add(Calendar.HOUR, fONCE_PER_PERIOD);

		SpaceWideRegistry.getGlobalLogger().info("   > Süreç sonrası yeni Gündönümü : " + DateUtils.getDate(nextTime.getTime()) + " olarak set edilecektir.");

		solsticeTime = nextTime.getTime();

	}

	public boolean isExecutePermission() {
		return executePermission;
	}

	public void setExecutePermission(boolean executePermission) {
		this.executePermission = executePermission;
	}

	public Thread getMyExecuter() {
		return myExecuter;
	}

	public void setMyExecuter(Thread myExecuter) {
		this.myExecuter = myExecuter;
	}

	public boolean isForced() {
		return isForced;
	}

	public synchronized void setForced(boolean isForced) {
		this.isForced = isForced;
	}

	public Date getSolsticeTime() {
		return solsticeTime;
	}
}