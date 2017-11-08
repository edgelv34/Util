@SuppressLint("NewApi")
public class SyncJobService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * JobScheduler 로 부터 Job 을 하달 받아 실행하는 부분
     * JobParameters 로 JobId 를 구분하여 실행
	 * 현재 동작은 최초 일정 delay 가 지난 후에 실행되는 job (JOB_ID_DELAY) 이 실행되고
	 * 다른 job (JOB_ID_REPEAT) 을 시작한다.
	 * 다른 job (JOB_ID_REPEAT) 은 widget 에 broadcast 를 날린다. (widget 의 ui를 변경시키거나 원하는 작업을 할 수가 있음)
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Logger.d("onStartJob");
        // 신규 Job 수행 조건이 만족되었을 때 호출됩니다.
        // onStartJob()의 종료 후에도 지속할 동작이 있다면 true, 여기에서 완료되면 false를 반환합니다.
        // true를 반환할 경우 finishJob()의 호출을 통해 작업 종료를 선언하거나,
        // onStopJob()를 호출하여 작업을 중지할 수 있습니다.
        int jobId = jobParameters.getJobId();
        if (jobId == LMJobScheduler.JOB_ID_DELAY) {
            Logger.d("job delay");
            LMJobScheduler.setUpdateJobRepeat(getApplicationContext());
            return false;
        } else if (jobId == LMJobScheduler.JOB_ID_REPEAT) {            
            Intent intent = new Intent(this.getApplicationContext(), LMWidget.class);
            intent.putExtra("jobRepeat", true);
            intent.setAction(Const.WIDGET_BROADCAST_CHECK);
            this.getApplicationContext().sendBroadcast(intent);
            return false;
        }
        return false;
    }

	/**
	* 시스템에서 Job 종료 시 호출되며, 현재 처리 중인 동작들을 중지해야 합니다.
	* 갑작스러운 중지로 현재 실행하던 Job을 재실행해야 할 경우 true, 새로 스케쥴링을 할 필요가 없다면 false를 반환합니다.
	*/
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Logger.d("onStopJob");
        return false;
    }
}
