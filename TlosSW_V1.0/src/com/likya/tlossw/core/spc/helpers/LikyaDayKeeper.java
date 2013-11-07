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

	private Calendar transitionTime = null;

	private static Calendar TRANSITION_PERIOD;

	private boolean isForced = false;

	public static LikyaDayKeeper getInstance(SpaceWideRegistry spaceWideRegistry) {

		if (ref == null) {
			ref = new LikyaDayKeeper(spaceWideRegistry);
		}
		return ref;
	}

	private LikyaDayKeeper(SpaceWideRegistry spaceWideRegistry) {

		LikyaDayKeeper.spaceWideRegistry = spaceWideRegistry;
		
		transitionTime = DateUtils.getTransitionDateTime(spaceWideRegistry.getTlosSWConfigInfo().getSettings().getTransitionTime().getTransition());
		
		TRANSITION_PERIOD = DateUtils.getTransitionPeriod( spaceWideRegistry.getTlosSWConfigInfo().getSettings().getTransitionPeriod().getPeriod());

	}

	public void run() {

		while (executePermission) {

			Date currentTime = Calendar.getInstance().getTime();

			if (transitionTime.before(currentTime)) {

				SpaceWideRegistry.getGlobalLogger().info("");
				SpaceWideRegistry.getGlobalLogger().info("   > Gündönümü gelmiştir !!");

				shiftTransitionTime();

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

	public void shiftTransitionTime() {

		Calendar nextTime = Calendar.getInstance();

		SpaceWideRegistry.getGlobalLogger().info("   > Şimdi : " + DateUtils.getDate(nextTime.getTime()));

		nextTime = DateUtils.addPeriod2TransitionDateTime(transitionTime, TRANSITION_PERIOD); 


		SpaceWideRegistry.getGlobalLogger().info("   > Süreç sonrası yeni Gündönümü : " + DateUtils.getDate(nextTime.getTime()) + " olarak set edilecektir.");

		transitionTime = nextTime;

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

	public Calendar getTransitionTime() {
		return transitionTime;
	}
}